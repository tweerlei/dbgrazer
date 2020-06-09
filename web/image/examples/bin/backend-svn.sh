#!/bin/bash
#
# DBExplorer backend script for Subversion repository access
#

function usage
	{
	cat << EOT >&2
usage: $0 create userName tempName finalName < data
       $0 update userName oldName tempName finalName < data
       $0 remove userName oldName
       $0 mkdir userName dirName
       $0 history limit name
EOT
	}

function createQuery # (userName, tempName, finalName)
	{
	cat > "$3" &&
		svn add "$3" &&
		svn commit -m "$1 added $3"
	}

function updateQuery # (userName, oldName, tempName, finalName)
	{
	if [ "$2" != "$4" ]; then
		# double rename because direct rename is not always possible on case-insensitive file systems (Windows) 
		cat > "$2" &&
			svn mv "$2" "$3" &&
			svn mv "$3" "$4" &&
			svn commit -m "$1 renamed $2 to $4"
	else
		cat > "$4" &&
			svn commit -m "$1 updated $4"
	fi
	}

function removeQuery # (userName, oldName)
	{
	svn rm "$2" &&
		svn commit -m "$1 removed $2"
	}

function createDir # (username, dirName)
	{
	svn mkdir "$2" &&
		svn commit -m "$1 created subdir $2"
	}

function showHistory # (limit, name)
	{
	svn log --xml --limit "$1" "$2"
	}

case "$1" in
	create)
		createQuery "$2" "$3" "$4"
		RC=$?
		;;
	update)
		updateQuery "$2" "$3" "$4" "$5"
		RC=$?
		;;
	remove)
		removeQuery "$2" "$3"
		RC=$?
		;;
	mkdir)
		createDir "$2" "$3"
		RC="?
		;;
	history)
		showHistory "$2" "$3"
		RC=$?
		;;
	*)
		usage
		RC=1
		;;
esac

exit $RC
