#!/bin/bash

. `dirname $0`/common.sh || exit 1

# show usage text
function usage
	{
	cat << EOT
Submit SQL scripts for execution.

  Usage: $COMMAND [-F config] [-m mode] dbname [filename...]
         $COMMAND [-F config] -l dbname

  Arguments:
    -F  Specify an alternate configuration
    -m  Specify script execution mode
    -l  List installed execution modes

  Files are interpreted as SQL scripts, SQL*Plus commands will be silently ignored.
  If no files are specified, input is read from standard input.
EOT
	showConfig
	}

# create multipart/form-data entity from given files or stdin
function createPostFile # file1 file2 file3
	{
	if [ -n "$MODE" ]; then
		multipartValue mode "$MODE"
	fi
	multipartFile file "$@"
	multipartEnd
	}

# parse command line options
MODE=
LIST=

while [ -n "$1" ]; do
	case "$1" in
		-F)
			shift
			USER_CONFIG="$1"
			;;
		-m)
			shift
			MODE="$1"
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
	getFile "$URL/ws/$DBNAME/runmodes.html"
	RC=$?
else
	TEMPFILE=`mktemp`
	
	if [ -z "$1" ]; then
		createPostFile > "$TEMPFILE"
		
		postFile "$URL/ws/$DBNAME/execute.html" "$TEMPFILE"
		
		RC=$?
	else
		while [ -n "$1" ]; do
			createPostFile "$1" > "$TEMPFILE"
			
			postFile "$URL/ws/$DBNAME/execute.html" "$TEMPFILE"
			
			RC=$?
			
			if [ $RC != 0 ]; then
				break
			fi
			
			shift
		done
	fi
	
	rm -f "$TEMPFILE"
fi

if [ $RC != 0 ]; then
	echo "Request failed" >&2
	exit $RC
fi

exit 0
