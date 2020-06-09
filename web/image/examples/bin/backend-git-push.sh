#!/bin/bash
#
# DBExplorer backend script for Git repository access with push after each commit
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
		git add "$3" &&
		git commit -m "$1 added $3" &&
		git push
	}

function updateQuery # (userName, oldName, tempName, finalName)
	{
	if [ "$2" != "$4" ]; then
		# double rename because direct rename is not always possible on case-insensitive file systems (Windows) 
		cat > "$2" &&
			git mv "$2" "$3" &&
			git mv "$3" "$4" &&
			git add "$4" &&
			git commit -m "$1 renamed $2 to $4" &&
			git push
	else
		cat > "$4" &&
			git add "$4" &&
			git commit -m "$1 updated $4" &&
			git push
	fi
	}

function removeQuery # (userName, oldName)
	{
	git rm "$2" &&
		git commit -m "$1 removed $2" &&
		git push
	}

function createDir # (username, dirName)
	{
	mkdir "$2"
	# Git does not track directories, so no add/commit here
	}

function showHistory # (limit, name)
	{
	echo "<log>" &&
		git log "-$1" --pretty='format:<logentry revision="%H"><author>%an</author><date>%ai</date><msg>%s</msg></logentry>' "$2" &&
		echo "</log>"
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
