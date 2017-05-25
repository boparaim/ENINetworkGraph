This application reads arbitrary network information from provided sources and normalize it to DOT format.
This resulting graph is then handed over to Graphviz which return the graph with augmented layout information.
At final step, a user can demand this graph in a browser. Vis.js is used for presentation of the graph.

Credits: 
1. http://www.graphviz.org/
2. http://visjs.org/
3. https://github.com/spring-projects/spring-framework
4. https://openclipart.org/

License:
1. Graphviz is released under Eclipse Public License - v 1.0. http://www.graphviz.org/License.php
2. Vis.js is dual licensed under both Apache 2.0 and MIT. http://visjs.org/index.html#licenses
3. The Spring Framework is released under version 2.0 of the Apache License. http://www.apache.org/licenses/LICENSE-2.0
4. Images - https://openclipart.org/share

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
1. clustering
2. show progress in browser
3. export topology to a file
4. allow small multiple graphs instead of one huge graph

Benchmarks:
Each result is average of three runs.
				nodes	levels	creation	connection		computation		post-processing
I>	windows 7 64bit 32GB i7 2x2.9GHz
	1. circo
		i.  	52		7		2ms			6ms				45ms			10ms
		ii. 	511		7		3ms			53ms			131ms			106ms
		iii.	5,101	7		11ms		1,321ms			873ms			503ms
		iv.		20,410	7		35ms		11,357ms		1,258ms			750ms
		v.		34,810	7		80ms		95,100ms		84,900ms		3,000ms				<- vis.js starts crashing here on zoom in
		iv. 	51,010	7		89ms		179,939ms		

Build the project:
1. Set JAVA_HOME in gradlew
2. Run gradle build
Create the release zip:
1. Run ./gradlew task createReleaseZip