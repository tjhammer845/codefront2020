#!/bin/bash

# Warning: If you prepare this file on Windows, it must have Linux \n line ends and not windows \r\n
# Changes in this file may also have to be made to the entrypoint-debug.sh

echo "Processing entrypoint.sh file"

# variables
secret_file=/run/secrets/deco.json

deco validate $secret_file || exit 1
deco run $secret_file

#Start the App
/usr/local/tomcat/bin/catalina.sh run