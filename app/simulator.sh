#!/bin/bash
# utility script to start the simulator

# script directory
script=$(readlink -f "$0")
mypath=$(dirname "$script")
javapath=java
mainclass=ca.empowered.nms.graph.Main
classpath="$mypath/../conf/:$mypath/../libs/*"
jvmopts="-Dlog4j.debug"

$javapath $jvmopts -cp $classpath $mainclass