#!/bin/bash

javac -d client_bin/ -cp lib/slick.jar -sourcepath src/main/java src/main/java/tractor/client/JarRunner.java
cp lib/native/*.zip client_bin/tractor/client/
unzip lib/slick.jar -o -d client_bin
unzip lib/lwjgl.jar -o -d client_bin
cd client_bin
jar -cvfm client.jar ../client.manifest ./ ../images
cd ..
