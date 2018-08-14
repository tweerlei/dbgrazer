# configuration
COMMAND=`basename $0`
USER_CONFIG=~/.dbgrazer/config
URL="http://127.0.0.1:8080/dbgrazer"
HTTP_USER=
HTTP_PASS=
BOUNDARY="next-part-a44e2ce25c3b28ff598b628bf3f9227e"

# pass user's locale as Accept-Language header, default to 'en'
#ACCEPT_LANGUAGE=`locale -f | cut -d _ -f 1`
ACCEPT_LANGUAGE=`locale | grep LC_MESSAGES | sed 's/^.*="\([a-zA-Z_]*\).*$/\1/'`
if [ -z "$ACCEPT_LANGUAGE" ]; then
	ACCEPT_LANGUAGE=en
fi

#WGET_OPTS="--quiet --content-on-error"
WGET_OPTS="--quiet"

# read user configuration
function readConfig
	{
	if [ -f "$USER_CONFIG" ]; then
		. "$USER_CONFIG"
	fi
	}

# print user configuration
function showConfig
	{
	cat << EOT

  User configuration is read from $USER_CONFIG
  If the user configuration does not contain HTTP_USER, credentials have to be entered on each invocation.
  The effective settings for this instance are:

    URL=$URL
    HTTP_USER=$HTTP_USER
    HTTP_PASS=${HTTP_PASS//?/*}
EOT
	}

# write a MIME multipart part for a simple value
function multipartValue # name value
	{
	echo -ne "--$BOUNDARY\r\n"
	echo -ne "Content-Disposition: form-data; name=\"$1\"\r\n\r\n"
	echo -n "$2"
	echo -ne "\r\n"
	}

# write a MIME multipart part for a value read from stdin
function multipartStream # name [file ...]
	{
	echo -ne "--$BOUNDARY\r\n"
	echo -ne "Content-Disposition: form-data; name=\"$1\"\r\n\r\n"
	shift
	cat "$@"
	echo -ne "\r\n"
	}

# write a MIME multipart part for a file read from stdin
function multipartFile # name [file ...]
	{
	echo -ne "--$BOUNDARY\r\n"
	echo -ne "Content-Disposition: form-data; name=\"$1\"; filename=\"$1\"\r\n"
	echo -ne "Content-Type: application/octet-stream\r\n\r\n"
	shift
	cat "$@"
	echo -ne "\r\n"
	}

# end a MIME multipart
function multipartEnd
	{
	echo -ne "--$BOUNDARY--\r\n"
	}

# post a file using wget
function postFile # url file
	{
	wget $WGET_OPTS\
		--output-document=- \
		--http-user="$HTTP_USER" \
		--http-passwd="$HTTP_PASS" \
		--header="Accept-Language: $ACCEPT_LANGUAGE" \
		--header="Content-Type: multipart/form-data; boundary=$BOUNDARY" \
		--post-file="$2" \
		"$1"
	}

# URL-encode a string
function urlEncode # string
	{
	echo -n "$1" | perl -pe 's/([^A-Za-z0-9])/sprintf("%%%02X", ord($1))/seg'
	}

# get a file using wget
function getFile # url
	{
	wget $WGET_OPTS\
		--output-document=- \
		--http-user="$HTTP_USER" \
		--http-passwd="$HTTP_PASS" \
		--header="Accept-Language: $ACCEPT_LANGUAGE" \
		"$1"
	}

# prompt for login
function readLogin
	{
	read -p "Username: " HTTP_USER || return 1
	read -s -p "Password: " HTTP_PASS || return 1
	echo >&2
	return 0
	}
