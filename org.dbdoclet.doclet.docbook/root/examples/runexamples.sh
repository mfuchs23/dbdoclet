#!/bin/bash

# /*
# 
# %%% Copyright (C) 2001-2003 Michael Fuchs %%%
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2, or (at your option)
# any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the
# Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# Boston, MA 02111-1307, USA.
#	
# Author: Michael Fuchs
# E-Mail: mfuchs@unico-consulting.com
#
# RCS Information:
# ---------------
# Id.........: $Id: runexamples.sh,v 1.1.1.1 2004/12/21 14:01:35 mfuchs Exp $
# Author.....: $Author: mfuchs $
# Date.......: $Date: 2004/12/21 14:01:35 $
# Revision...: $Revision: 1.1.1.1 $
# State......: $State: Exp $
#
# */

#
# CONFIGURATION
#
DBDOCLET_HOME=${DBDOCLET_HOME:=/usr/share/dbdoclet}
LOG=runexamples.log

function usage() {

    MSG=$1

    [ "$MSG" != "" ] && echo "Error: $1"
    exit 255
}

##################################################
### OUPUT FUNCTIONS                            ###
##################################################

LINE=".............................................."

function msg_status {

    LABEL="$1"
    TMP=${LINE:${#LABEL}}
    echo -n "$LABEL$TMP...: "
    echo -n "$LABEL$TMP...: " >> $LOG
}

function msg_println {

    LABEL="$1"
    VALUE="$2"

    msg_status "$LABEL"

    echo "$VALUE"
    echo "$VALUE" >> $LOG
}

function msg_done {

    green=`tput setf 2`
    norm=`tput sgr0`

    echo "${norm}${green}done${norm}"
    echo "done" >> $LOG
}

function msg_fatal {

    red=`tput setf 4`
    norm=`tput sgr0`

    echo "${norm}${red}fatal error${norm} $1"
    echo "fatal error" >> $LOG
    exit 1
}

function msg_check {

    RC=$1

    [ $RC -eq 0 ] && msg_done
    [ $RC -ne 0 ] && msg_fatal "$2"
}

function mkHtml {

    msg_status "Creating HTML"
    msg_check $?
}

function example1 {

    msg_status "Running example #1: Generating PDF with default properties."
    dbdoclet \
	-sourcepath java \
	java/Thought.java \
	java/ThoughtException.java >> $LOG 2>&1

    [ $? -ne 0 ] && msg_check 255
    
    $JAVA_BIN \
        -classpath "$CLASSPATH" \
        org.apache.fop.apps.Fop \
        -xml Reference.xml \
        -xsl fo.xsl \
        -pdf Reference.pdf >> $LOG 2>&1

    msg_check $?
}

function example2
{
    msg_status "Running example #2: Destination directory"
    $JAVADOC_BIN \
	-doclet org.dbdoclet.doclet.docbook.DocBookDoclet \
	-docletpath ${DBDOCLET_HOME}/jars/dbdoclet.jar \
        -authorfirstname Michael \
        -authorsurname Fuchs \
        -authoremail michael.fuchs@unico-group.com \
        -corporation "UNICO Media GmbH" \
	-sourcepath java \
        -private \
        -author \
        -version \
        -verbose \
	-d Example_2 \
	java/Thought.java \
	java/ThoughtException.java >> $LOG 2>&1

    msg_check $?
}

function example3
{
    msg_status "Running example #3: Classic"
    $JAVADOC_BIN \
	-doclet org.dbdoclet.doclet.docbook.DocBookDoclet \
	-docletpath ${DBDOCLET_HOME}/jars/dbdoclet.jar \
	-d Example_3 \
	-sourcepath java \
        -style org.dbdoclet.doclet.docbook.StyleOne \
        -private \
        -author \
        -version \
        -verbose \
	-properties dbdoclet.properties \
	java/Thought.java \
	java/ThoughtException.java >> $LOG 2>&1
    msg_check $?
}

function example4
{
    msg_status "Running example #4: Strict DocBook XML"
    $JAVADOC_BIN \
	$JAVADOC_OPTS \
	-doclet org.dbdoclet.doclet.docbook.DocBookDoclet \
	-docletpath ${DBDOCLET_HOME}/jars/dbdoclet.jar \
	-sourcepath java \
	-d Example_4 \
        -private \
        -author \
        -version \
        -verbose \
	-properties dbdoclet-strict.properties \
	java/Thought.java \
	java/ThoughtException.java >> $LOG 2>&1
    msg_check $?

    cd Example_4
    # mkHtml
    cd ..
}

function example5
{
    msg_status "Running example #5: SGML Properties"
    $JAVADOC_BIN \
	-doclet org.dbdoclet.doclet.docbook.DocBookDoclet \
	-docletpath ${DBDOCLET_HOME}/jars/dbdoclet.jar \
	-sourcepath java \
	-d Example_5 \
        -f Reference.sgml \
        -private \
        -author \
        -version \
        -verbose \
	-properties dbdoclet-sgml.properties \
	java/Thought.java \
	java/ThoughtException.java >> $LOG 2>&1
    msg_check $?
}

# =============================
# MAIN
# =============================

EXAMPLE="all"

PROG="$0"

EXAMPLE_DIR=`dirname $PROG`
[ "$EXAMLPE_DIR" = "" ] && EXAMPLE_DIR=`pwd`

DBDOCLET_HOME=`(cd $EXAMPLE_DIR/..; pwd)`

while getopts "n:" arg
do
  case $arg in
      n) EXAMPLE=$OPTARG;;
  esac
done

echo
echo "dbdoclet examples"
echo "================="
echo

msg_println "dbdoclet home" "$DBDOCLET_HOME"
msg_println "Programm" "$PROG"
msg_println "Examples directory" "$EXAMPLE_DIR"
msg_println "Log" "$LOG"

DBDOCLET_JAR=${DBDOCLET_HOME}/jars/dbdoclet.jar
if [ ! -f ${DBDOCLET_JAR} ] ; then
    cat <<EOF
ERROR 
=====
The jar archive "$DBDOCLET_JAR" can't be found! The variable
DBDOCLET_HOME must point to the installation directory of dbdoclet. The default
value is /opt/dbdoclet. The current value of DBDOCLET_HOME is "$DBDOCLET_HOME",
but there seems to be no dbdoclet installed.

EOF
    exit 255
fi

JAVA_BIN=`type -p java 2>/dev/null`
if [ "$JAVA_BIN" = "" ] ; then 
    [ -x "$JAVA_BINDIR/java" ] && JAVA_BIN="$JAVA_BINDIR/java"
    [ -x "$JAVA_ROOT/bin/java" ] && JAVA_BIN="$JAVA_ROOT/bin/java"
    [ -x "$JAVA_HOME/bin/java" ] && JAVA_BIN="$JAVA_HOME/bin/java"
fi

if [ "$JAVA_BIN" = "" ] ; then
    cat<<EOF
ERROR
=====
Can't find java executable!
EOF
    exit 255
fi

VERSION=`$JAVA_BIN -version 2>&1`
$JAVA_BIN -version 2>&1 | egrep "1.2|1.3|1.4" > /dev/null
if [ $? -ne 0 ] ; then
    cat <<EOF
ERROR
=====
Found unsupported java version.
$VERSION
EOF
    exit 255
else
    msg_status "Found java version"
    echo `echo $VERSION | awk '{ print $3 ; exit }'`
fi

JAR_DIR=$DBDOCLET_HOME/jars

CLASSPATH=""

for JAR in $JAR_DIR/*.jar
do
  CLASSPATH="$JAR:$CLASSPATH"
done

CLASSPATH=${CLASSPATH%:}
# echo "Using classpath $CLASSPATH"

BOOTCLASSPATH=\
$JAR_DIR/xml-apis.jar:\
$JAR_DIR/xercesImpl.jar:\
$JAR_DIR/xalan.jar

$JAVA_BIN -version 2>&1 | egrep "1.4" > /dev/null
if [ $? -eq 0 ] ; then
    JAVADOC_OPTS="-J-Xbootclasspath/p:$BOOTCLASSPATH"
else
    JAVADOC_OPTS="-J-Xbootclasspath/p:$BOOTCLASSPATH"
fi

JAVADOC_BIN=`type -p javadoc 2>/dev/null`
if [ "$JAVADOC_BIN" = "" ] ; then 
    [ -x "$JAVA_BINDIR/javadoc" ] && JAVADOC_BIN="$JAVA_BINDIR/javadoc"
    [ -x "$JAVA_ROOT/bin/javadoc" ] && JAVADOC_BIN="$JAVA_ROOT/bin/javadoc"
    [ -x "$JAVA_HOME/bin/javadoc" ] && JAVADOC_BIN="$JAVA_HOME/bin/javadoc"
fi

if [ "$JAVADOC_BIN" = "" ] ; then
    cat<<EOF
ERROR
=====
Can't find executable javadoc!
EOF
    exit 255
fi


rm -f $LOG

if [ "$EXAMPLE" = "all" ] ; then

  rm -rf Example_*

  example1
  example2
  example3
  example4
  example5

else

  rm -rf Example_$EXAMPLE
  example$EXAMPLE

fi
