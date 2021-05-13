#!/bin/bash

if [ -f /opt/dbgrazer-init/init.sh ]; then
	echo "Running init.sh"
	source /opt/dbgrazer-init/init.sh
fi

exec catalina.sh run
