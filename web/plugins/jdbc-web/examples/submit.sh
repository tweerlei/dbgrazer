#!/bin/bash

. `dirname $0`/common.sh || exit 1

# show usage text
function usage
	{
	cat << EOT
Submit raw SQL queries.

  Usage: $COMMAND [-F config] [-f format] dbname [statement]
         $COMMAND [-F config] -l dbname

  Arguments:
    -F  Specify an alternate configuration
    -f  Specify output format (see -l)
    -l  List installed output formats

  If no statement is specified, input is read from standard input.
EOT
	showConfig
	}

# create multipart/form-data entity from given files or stdin
function createPostFile # format query
	{
	multipartValue format "$1"
	if [ -n "$2" ]; then
		multipartValue query "$2"
	else
		multipartStream query
	fi
	multipartEnd
	}

# parse command line options
FORMAT=CSV
LIST=

while [ -n "$1" ]; do
	case "$1" in
		-F)
			shift
			USER_CONFIG="$1"
			;;
		-f)
			shift
			FORMAT="$1"
			;;
		-l)
			shift
			LIST=1
			break
			;;
		*)
			break
			;;
	esac
	shift
done

readConfig

if [ -z "$1" ]; then
	usage >&2
	exit 1
fi

DBNAME="$1"
shift

# prompt for credentials
if [ -z "$HTTP_USER" ]; then
	readLogin || exit 1
fi

# prepare and send data
if [ -n "$LIST" ]; then
	getFile "$URL/ws/$DBNAME/tbllinks.html"
	RC=$?
else
	TEMPFILE=`mktemp`
	
	createPostFile "$FORMAT" "$1" > "$TEMPFILE"
	
	postFile "$URL/ws/$DBNAME/submit.html" "$TEMPFILE"
	
	RC=$?
	
	rm -f "$TEMPFILE"
fi

if [ $RC != 0 ]; then
	echo "Request failed" >&2
	exit $RC
fi

exit 0
