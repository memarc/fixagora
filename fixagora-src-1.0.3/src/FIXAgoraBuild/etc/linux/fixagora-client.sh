#!/bin/sh

cd "$(dirname $0)" 
java -Xmx1024m -Xms1024m -XX:MaxPermSize=512m -classpath @lib@ net.sourceforge.fixagora.basis.client.control.BasisClient "$@"