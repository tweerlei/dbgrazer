#!/bin/bash

. `dirname $0`/common.sh || exit 1

# show usage text
function usage
	{
	cat << EOT
Compare and possibly modify table structures.

  Usage: $COMMAND [-F config] [-m mode] dbname1 catalog1.schema1.table1 dbname2 catalog2.schema2.table2
         $COMMAND [-F config] -l dbname

  Arguments:
    -F  Specify an alternate configuration
    -m  Specify execution mode
    -l  List installed execution modes
EOT
	showConfig
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

if [ -n "$LIST" -a -z "$1" ]; then
	usage >&2
	exit 1
elif [ -z "$LIST" -a -z "$4" ]; then
	usage >&2
	exit 1
fi

DBNAME="$1"
shift

CATALOG=${1%%.*}
SCHEMA=${1%.*}
SCHEMA=${SCHEMA#*.}
TABLE=${1##*.}
shift

DBNAME2="$1"
shift

CATALOG2=${1%%.*}
SCHEMA2=${1%.*}
SCHEMA2=${SCHEMA2#*.}
TABLE2=${1##*.}

# prompt for credentials
if [ -z "$HTTP_USER" ]; then
	readLogin || exit 1
fi

# prepare and send data
if [ -n "$LIST" ]; then
	getFile "$URL/ws/$DBNAME/runmodes.html"
else
	OBJTYPE=`urlEncode "$OBJTYPE"`
	
	getFile "$URL/ws/$DBNAME/dbcompare.html?catalog=$CATALOG&schema=$SCHEMA&filter=$TABLE&connection2=$DBNAME2&catalog2=$CATALOG2&schema2=$SCHEMA2&mode=$MODE"
fi

RC=$?

if [ $RC != 0 ]; then
	echo "Request failed" >&2
	exit $RC
fi

exit 0
