#!/bin/bash

if [ -f /root/.dbgrazer-init/init.sh ]; then
	echo "Running init.sh"
	source /root/.dbgrazer-init/init.sh
fi

exec catalina.sh run
