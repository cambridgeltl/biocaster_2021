#!/bin/sh
SRLGUI_HOME=`pwd`

java -cp $SRLGUI_HOME/dist/lib/appframework-1.0.3.jar:$SRLGUI_HOME/dist/lib/lucene-core-2.2.0.jar:$SRLGUI_HOME/dist/lib/swing-layout-1.0.3.jar:$SRLGUI_HOME/swing-worker-1.1.jar:$SRLGUI_HOME/dist/lib/lucene-analyzers-2.3.2.jar -jar $SRLGUI_HOME/dist/SRLGUI.jar
