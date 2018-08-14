#!/bin/bash

. `dirname $0`/common.sh || exit 1

# show usage text
function usage
	{
	cat << EOT
Download table contents.

  Usage: $COMMAND [-F config] [-f format] [-w where] [-o orderby] dbname catalog.schema.table
         $COMMAND [-F config] -l dbname

  Arguments:
    -F  Specify an alternate configuration
    -f  Specify output format (see -l)
    -w  Specify WHERE clause to limit rows
    -o  Specify ORDER BY clause
    -l  List installed output formats
EOT
	showConfig
	}

# parse command line options
FORMAT=CSV
WHERE=
ORDERBY=
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
		-w)
			shift
			WHERE="$1"
			;;
		-o)
			shift
			ORDERBY="$1"
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

if [ -n "$LIST" -a -z "$1" ]; then
	usage >&2
	exit 1
elif [ -z "$LIST" -a -z "$2" ]; then
	usage >&2
	exit 1
fi

DBNAME="$1"
shift

CATALOG=${1%%.*}
SCHEMA=${1%.*}
SCHEMA=${SCHEMA#*.}
TABLE=${1##*.}

# prompt for credentials
if [ -z "$HTTP_USER" ]; then
	readLogin || exit 1
fi

# prepare and send data
if [ -n "$LIST" ]; then
	getFile "$URL/ws/$DBNAME/tbllinks.html"
else
	WHERE=`urlEncode "$WHERE"`
	ORDERBY=`urlEncode "$ORDERBY"`
	
	getFile "$URL/ws/$DBNAME/export.html?catalog=$CATALOG&schema=$SCHEMA&object=$TABLE&where=$WHERE&order=$ORDERBY&format=$FORMAT"
fi

RC=$?

if [ $RC != 0 ]; then
	echo "Request failed" >&2
	exit $RC
fi

exit 0
