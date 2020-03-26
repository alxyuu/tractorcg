#!/bin/bash

java -cp client_bin:lib/slick.jar:lib/lwjgl.jar -Djava.library.path=lib/native/macosx tractor.client.Client
