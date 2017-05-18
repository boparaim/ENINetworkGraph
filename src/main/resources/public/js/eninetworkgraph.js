// @author mboparai
//
// eninetworkgraph.js

$(document).ready(function() {
	console.log("start: ENINetworkGraph");
	
	// first - read the network from server and present it to the user
	$.ajax({
		url: "http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout",
		type: "GET",
		data: {},
		dataType: "json",
		success: function(data) {
			console.log("success for http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout data: "+data);
			var graphString = JSON.stringify(data.payload)
									.replace(/\\"/g, '"')		// remove json encoding
									.replace(/{/g, '{ ')		// add some white space
									.replace(/\[/g, '[ ');
			graphString = graphString.substr(1, graphString.length - 2);	// strip quotes
			//console.log(graphString);
			console.log('string maniputation done');
			
			// read dot input in vis.js
			var parsedData = vis.network.convertDot(graphString);
			var options = parsedData.options;
			var data = {
				nodes: parsedData.nodes,
				edges: parsedData.edges
			}
			console.log('data is set');
			
			// now doing this on server
			/*data.nodes.forEach(function t(e, i, arr) {
				var pos = e.pos;
				var x = Number(pos.split(',')[0]);
				var y = Number(pos.split(',')[1]);
			});*/
			
			// we can extend the options like a normal JSON variable:
			options.nodes = {
				//color: 'red'
			}
			
			options.interaction = {
				hover: true,
				multiselect: true,
				navigationButtons: true,
				keyboard: {
					enabled: true
				}
			};
			
			options.manipulation = {
				enabled: true
			};
			
			// disable layout
			options.layout = {
				improvedLayout : false,
				hierarchical : {
					enabled : false
				}
			};
			
			// disable physics - we have already computed the final layout on server
			options.physics = {
				enabled : false,
				barnesHut : {
					avoidOverlap: 1
				},
				repulsion : {
					springLength : 300
				}
			};
			
			// present the results
			var container = document.getElementById('eniNetworkGraphContainer');
			
			// create a network
			var network = new vis.Network(container, data, options);
			console.log('network is on canvas');
		},
		error: function(xhr, status, error) {
			var err = eval("(" + xhr.responseText + ")");
			console.log("error in ajax call to http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout "+err.Message)
		}
	});
});