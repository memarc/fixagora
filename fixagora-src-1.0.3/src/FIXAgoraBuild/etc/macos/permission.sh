#!/bin/bash

ln -s /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub FIX\ Agora\ Server.app/Contents/MacOS/JavaApplicationStub

ln -s /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub FIX\ Agora\ Client.app/Contents/MacOS/JavaApplicationStub

ln -s /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub FIX\ Agora\ Simulator.app/Contents/MacOS/JavaApplicationStub

chmod a+rx FIX\ Agora\ Server.app/Contents/MacOS/JavaApplicationStub

chmod -R a+rw FIX\ Agora\ Server.app/Contents/Resources/Java/log

chmod -R a+rw FIX\ Agora\ Server.app/Contents/Resources/Java/conf

chmod -R a+rw FIX\ Agora\ Server.app/Contents/Resources/Java/derby

chmod a+rx FIX\ Agora\ Client.app/Contents/MacOS/JavaApplicationStub

chmod -R a+rw FIX\ Agora\ Client.app/Contents/Resources/Java/log

chmod -R a+rw FIX\ Agora\ Client.app/Contents/Resources/Java/conf

chmod a+rx FIX\ Agora\ Simulator.app/Contents/MacOS/JavaApplicationStub

chmod -R a+rw FIX\ Agora\ Simulator.app/Contents/Resources/Java/log

chmod -R a+rw FIX\ Agora\ Simulator.app/Contents/Resources/Java/conf