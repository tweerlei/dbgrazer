#!/bin/bash

SELF=`dirname "$0"`

COMMAND="$1"
shift

case "$COMMAND" in
	run-query)
		"$SELF/query.sh" "$@"
		;;
	run-sql)
		"$SELF/submit.sh" "$@"
		;;
	run-script)
		"$SELF/exec.sh" "$@"
		;;
	export-table)
		"$SELF/download.sh" "$@"
		;;
	compare-data)
		"$SELF/dml.sh" "$@"
		;;
	compare-ddl)
		"$SELF/dbcompare.sh" "$@"
		;;
	compare-src)
		"$SELF/srccompare.sh" "$@"
		;;
	*)
		cat << EOT >&2
Command line interface to the JDBC plugin.

  Usage: `basename $0` command arguments...

  Commands:
    run-query     Run a predefined query
    run-sql       Execute a single SQL statement
    run-script    Execute an SQL script
    export-table  Export table data
    compare-data  Compare table data
    compare-ddl   Compare table structure
    compare-src   Compare DB object sources

  Type '$0 command' to show supported arguments for each command.
EOT
		;;
esac
