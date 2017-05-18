This application reads arbitrary network information from provided sources and normalize it to DOT format.
This resulting graph is then handed over to Graphviz which return the graph with augmented layout information.
At final step, a user can demand this graph in a browser. Vis.js is used for presentation of the graph.

Credits: 
1. http://www.graphviz.org/
2. http://visjs.org/
3. https://github.com/spring-projects/spring-framework

License:
1. Graphviz is released under Eclipse Public License - v 1.0. http://www.graphviz.org/License.php
2. Vis.js is dual licensed under both Apache 2.0 and MIT. http://visjs.org/index.html#licenses
3. The Spring Framework is released under version 2.0 of the Apache License. http://www.apache.org/licenses/LICENSE-2.0

How to get started:
1. Download and install appropriate Graphviz for your system from http://www.graphviz.org/Download..php
2. Download the zip file - https://github.com/boparaim/ENINetworkGraph/tree/enidell/zips
3. Unzip the downloaded file
4. cd to unzipped directory
5. Update value for eni.nms.graph.grpahviz.path in conf/application.properties to location of your Graphviz installation
6. Run ./bin/graph.[cmd|sh]
7. Open browser at http://127.0.0.1:4567/

Configuration files:
1. Node map can be defined in conf/config.json
2. Application settings are in conf/application.properties
3. Application logs settings are in conf/log4j2.properties

TODOs:
1. Allow user to load and store the changed layout.

Build the project:
1. Set JAVA_HOME in gradlew
2. Run gradle build
Create the release zip:
1. Run ./gradlew task createReleaseZip