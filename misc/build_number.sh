#!/bin/bash

branch=$(git rev-parse --abbrev-ref HEAD);
count=$(git log --oneline | wc -l | gawk '{print $1}');
revision=$(git rev-parse --short HEAD);

dash=\&#8211\;
version=$branch\\$dash$count\\$dash$revision;

echo "Version: $version";

string_xml='../app/src/main/res/values/strings.xml';

sed -i "s/\(<string name=\"build_num\">\).*\(<\/string>\)/\1$version\2/g" "$string_xml";
