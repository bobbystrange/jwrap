#!/usr/bin/env bash

cd $HOME/Downloads
windows_ip=192.168.1.10

# convert all files the diretory
ls -lh | grep "^-" | awk '{print $9}' | sort -r | while read office_name
do
    curl -v -XPOST -o output/${office_name%.*}.pdf \
        --form file=@$office_name \
        http://$windows_ip:8080/wps/convert
done


# convert specified files
while read office_name
do
    curl -v -XPOST -o output/${office_name%.*}.pdf \
        --form file=@$office_name \
        http://$windows_ip:8080/wps/convert
done <<EOF
some.pptx
EOF
