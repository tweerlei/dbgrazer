#!/bin/bash

if [ -x /root/init.sh ]; then
	echo "Executing init.sh"
	/root/init.sh
fi

exec catalina.sh run
