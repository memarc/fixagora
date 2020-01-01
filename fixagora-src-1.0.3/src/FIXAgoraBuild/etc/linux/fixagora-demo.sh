#!/bin/sh

cd "$(dirname $0)"
cd ./fixagora-simulator 
java -Xmx1024m -Xms1024m -XX:MaxPermSize=512m -classpath @lib@ net.sourceforge.agora.simulator.view.FIXAgoraSimulator linux_demo.txt