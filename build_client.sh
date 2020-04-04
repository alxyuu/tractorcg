#!/bin/bash

javac -d client_bin/ -cp lib/slick.jar -sourcepath src/main/java src/main/java/tractor/client/JarRunner.java
cp lib/native/*.zip client_bin/tractor/client/
unzip -o lib/slick.jar -d client_bin
unzip -o lib/lwjgl.jar -d client_bin
cd client_bin
jar -cvfm client.jar ../client.manifest ./ ../images
cd ..
