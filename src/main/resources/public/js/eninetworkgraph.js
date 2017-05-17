//

$(document).ready(function() {
	console.log("start: ENINetworkGraph");
	
	$.ajax({
		url: "http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout",
		type: "GET",
		data: {},
		dataType: "json",
		success: function(data) {
			console.log("success for http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout data: "+data);
			//console.log(data.message.replace(/\\"/g, '"'));
			var graphString = JSON.stringify(data.message)
									.replace(/\\"/g, '"')
									.replace(/{/g, '{ ')
									.replace(/\[/g, '[ ');
			graphString = graphString.substr(1, graphString.length - 2);
			//console.log(graphString);
			var parsedData = vis.network.convertDot(graphString);
			
			/*console.log(typeof parsedData.edges);
			console.log(parsedData.nodes);
			console.log(parsedData.edges);*/
			
			var data = {
				nodes: parsedData.nodes,
				edges: parsedData.edges
			}
			
			data.nodes.forEach(function t(e, i, arr) {
				//console.log('this element in nodes - '+i);
				var pos = e.pos;
				var x = Number(pos.split(',')[0]);
				var y = Number(pos.split(',')[1]);
				//console.log(x+' '+y+' ('+pos+')');
				/*e.x = x;
				e.y = y;*/
				//console.log(e);
			});
			

			var options = parsedData.options;

			// you can extend the options like a normal JSON variable:
			options.nodes = {
				//color: 'red'
			}
			
			options.layout = {
				improvedLayout : false,
				hierarchical : {
					enabled : false
				}
			};
			
			options.physics = {
				enabled : false,
				barnesHut : {
					avoidOverlap: 1
				},
				repulsion : {
					springLength : 300
				}
			};
			
			var container = document.getElementById('eniNetworkGraphContainer');

			// create a network
			var network = new vis.Network(container, data, options);
		},
		error: function(xhr, status, error) {
			var err = eval("(" + xhr.responseText + ")");
			console.log("error in ajax call to http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout "+err.Message)
		}
	});
});