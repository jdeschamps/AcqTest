#!/bin/bash

# get MM2 home
MM2_HOME=$1


if ! [ -d "$MM2_HOME" ]; then
	echo "[$MM2_HOME] is not a directory."
else
	# tests if mvn is installed
	command -v mvn >/dev/null 2>&1 || { echo >&2 "Failed to call mvn, are you sure Maven is installed?";}
	
	# tests if the Micro-Manager jars are present and deploy them
	MMJ="$MM2_HOME/plugins/Micro-Manager/MMJ_.jar"
	MMAcqEngine="$MM2_HOME/plugins/Micro-Manager/MMAcqEngine.jar"
	AcqEngJ="$MM2_HOME/plugins/Micro-Manager/AcqEngJ-0.12.0.jar"
	MMCoreJ="$MM2_HOME/plugins/Micro-Manager/MMCoreJ.jar"
	MM2_PLUGINS_HOME="$MM2_HOME/mmplugins"
	
	if [ -f "$MMJ" ] && [ -f "$MMAcqEngine" ] && [ -f "$MMCoreJ" ]; then
		# deploy MM2 jars
		mvn install:install-file -Dfile="$MMJ" -DgroupId=org.micromanager  -DartifactId=MMJ_ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MMAcqEngine" -DgroupId=org.micromanager  -DartifactId=MMAcqEngine -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$AcqEngJ" -DgroupId=org.micromanager  -DartifactId=acqj -Dversion=0.8.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MMCoreJ" -DgroupId=org.micromanager  -DartifactId=MMCoreJ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		
		# compile project
		mvn clean install
	
		# deploy to MM2
		cp "target/AcqPlugin-0.0.1.jar" "$MM2_PLUGINS_HOME/AcqPlugin.jar"
	else
		echo "Could not find MMJ_.jar, MMAcqEngine.jar or MMCoreJ.jar. Did you input the correct directory?"
	fi
fi
