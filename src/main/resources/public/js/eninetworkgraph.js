// @author mboparai
//
// eninetworkgraph.js

$(document).ready(function() {
	console.log("start: ENINetworkGraph");
	
	var network;
	
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
			network = new vis.Network(container, data, options);
			console.log('network is on canvas');
			
			
			
			
			
			
			
			
			/*var clusterIndex = 0;
		    var clusters = [];
		    var lastClusterZoomLevel = 0;
		    var clusterFactor = 0.9;
			
			// set the first initial zoom level
		    network.once('initRedraw', function() {
		        if (lastClusterZoomLevel === 0) {
		            lastClusterZoomLevel = network.getScale() + 10;
		        }
		    });

		    // we use the zoom event for our clustering
		    network.on('zoom', function (params) {
		        if (params.direction == '-') {
		            if (params.scale < lastClusterZoomLevel*clusterFactor) {
		                makeClusters(params.scale);
		                lastClusterZoomLevel = params.scale;
		            }
		        }
		        else {
		            openClusters(params.scale);
		        }
		    });
			
			// if we click on a node, we want to open it up!
		    network.on("selectNode", function (params) {
		        if (params.nodes.length == 1) {
		            if (network.isCluster(params.nodes[0]) == true) {
		                network.openCluster(params.nodes[0])
		            }
		        }
		    });


		    // make the clusters
		    function makeClusters(scale) {
		        var clusterOptionsByData = {
		            processProperties: function (clusterOptions, childNodes) {
		                clusterIndex = clusterIndex + 1;
		                var childrenCount = 0;
		                for (var i = 0; i < childNodes.length; i++) {
		                    childrenCount += childNodes[i].childrenCount || 1;
		                }
		                clusterOptions.childrenCount = childrenCount;
		                clusterOptions.label = "# " + childrenCount + "";
		                clusterOptions.font = {size: childrenCount*5+30}
		                clusterOptions.id = 'cluster:' + clusterIndex;
		                clusters.push({id:'cluster:' + clusterIndex, scale:scale});
		                return clusterOptions;
		            },
		            clusterNodeProperties: {borderWidth: 3, shape: 'database', font: {size: 30}}
		        }
		        network.clusterOutliers(clusterOptionsByData);
		        //if (document.getElementById('stabilizeCheckbox').checked === true) {
		            // since we use the scale as a unique identifier, we do NOT want to fit after the stabilization
		            network.setOptions({physics:{stabilization:{fit: false}}});
		            network.stabilize();
		        //}
		    }

		    // open them back up!
		    function openClusters(scale) {
		        var newClusters = [];
		        var declustered = false;
		        for (var i = 0; i < clusters.length; i++) {
		            if (clusters[i].scale < scale) {
		                network.openCluster(clusters[i].id);
		                lastClusterZoomLevel = scale;
		                declustered = true;
		            }
		            else {
		                newClusters.push(clusters[i])
		            }
		        }
		        clusters = newClusters;
		        //if (declustered === true && document.getElementById('stabilizeCheckbox').checked === true) {
		            // since we use the scale as a unique identifier, we do NOT want to fit after the stabilization
		            network.setOptions({physics:{stabilization:{fit: false}}});
		            network.stabilize();
		        //}
		    }*/
		},
		error: function(xhr, status, error) {
			var err = eval("(" + xhr.responseText + ")");
			console.log("error in ajax call to http://127.0.0.1:4567/ENINetworkGraph/get/graph/layout "+err.Message)
		}
	});
			

	$("#toolbar #layoutname")
		.textinput({
			clearBtn: true
		})
		.keyup(function(event) {
			//console.log(event);
			//console.log($("#toolbar #layoutname").val());
			if ($("#toolbar #layoutname").val().length > 5) {
				$("#toolbar savebutton").button("option", "disabled", false);
			}
			else {
				$("#toolbar savebutton").button("option", "disabled", true);
			}
		});
	
	$("#toolbar savebutton")
		.button({
			disabled: true
		})
		.click(function(){
			if (network && network.nodesHandler && network.nodesHandler.body 
					&& network.nodesHandler.body.data
					&& network.nodesHandler.body.data.nodes
					&& network.nodesHandler.body.data.edges ) {
				console.log('we have a network on canvas');
				
				// create a DataSet
				/*var options = {};
				var data = new vis.DataSet(options);

				data.add([
					  {id: 1, text: 'item 1', date: new Date(2013, 6, 20), group: 1, first: true},
					  {id: 2, text: 'item 2', date: '2013-06-23', group: 2}
					]);

				var nodes = network.getPositions();
				console.log(nodes);
				console.log(network.nodesHandler.body.data.nodes);*/
				
				if (network.nodesHandler.body.data.nodes._data 
						&& network.nodesHandler.body.data.nodes.length > 0
						&& network.nodesHandler.body.data.edges._data 
						&& network.nodesHandler.body.data.edges.length > 0) {
					
					
					network.storePositions();
					var layoutName = $("#toolbar #layoutname").val();
					var nodes = network.nodesHandler.body.data.nodes._data;
					var edges = network.nodesHandler.body.data.edges._data;
					
					var nodesArray = $.map(nodes, function(value, index) {
					    return [value];
					});
					var edgesArray = $.map(edges, function(value, index) {
					    return [value];
					});
					/*for (var name in nodes) {
						if (nodes.hasOwnProperty(name)) {
							nodesArray
						}
					}*/
					
					//console.log(nodes);
					//console.log(edges);
					
					if (localStorage) {
						console.log('storing '+layoutName+' as new layout in local storage');
						
						localStorage.removeItem(layoutName+'Nodes');
						localStorage.removeItem(layoutName+'Edges');

						localStorage.setItem(layoutName+'Nodes', JSON.stringify(nodesArray));
						localStorage.setItem(layoutName+'Edges', JSON.stringify(edgesArray));
						
						//console.log(JSON.parse(localStorage.getItem(layoutName+'Nodes')));
						getStoredLayouts();
					}
				}
			}
		});
	
	$("#toolbar #layout")
		.selectmenu({
			disabled: true
		});
	
	$.extend({
	    distinct : function(anArray) {
	       var result = [];
	       $.each(anArray, function(i,v){
	           if ($.inArray(v, result) == -1) result.push(v);
	       });
	       return result;
	    }
	});

	var storedLayouts = new Array();
	function getStoredLayouts() {
		storedLayouts = new Array();
		for ( var i = 0, len = localStorage.length; i < len; ++i ) {
			var key = localStorage.key(i).replace('Nodes', '').replace('Edges', '');
			console.log(key);
			storedLayouts.push(key);		
		}
		storedLayouts = $.distinct(storedLayouts);
		$("#toolbar #layout").empty();
		$.each(storedLayouts, function(i, value) {
			//console.log('key: '+value);
			//$("#toolbar #layout").selectmenu('option', value, true);
			$('<option/>').val(i).text(value).appendTo("#toolbar #layout");
		});
		//console.log($("#toolbar #layout"));
		$("#toolbar #layout").selectmenu('refresh');
	}
	getStoredLayouts();
		
	$("#toolbar deletebutton")
		.button({
			disabled: true
		})
		.click(function(){
			var layoutName = $( "#toolbar #layout option:selected" ).text();
			if (localStorage && layoutName) {
				localStorage.removeItem(layoutName+'Nodes');
				localStorage.removeItem(layoutName+'Edges');
				getStoredLayouts();
			}
		});
	
	$("#toolbar loadbutton")
		.button({
			disabled: true
		})
		.click(function(){
			var layoutName = $( "#toolbar #layout option:selected" ).text();
			if (localStorage && layoutName) {
				console.log('loading '+layoutName);
				var nodes = JSON.parse(localStorage.getItem(layoutName+'Nodes'));
				var edges = JSON.parse(localStorage.getItem(layoutName+'Edges'));
				//console.log(nodes);
				
				var options = {};
				var data = {
						nodes: nodes,
						edges: edges
					}
				
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
				
				var container = document.getElementById('eniNetworkGraphContainer');
				
				// create a network
				network = new vis.Network(container, data, options);
				console.log('loaded network is on canvas');
			}
		});
	
	if (storedLayouts.length > 0 ) {
		$("#toolbar #layout").selectmenu("option", "disabled", false);
		$("#toolbar loadbutton").button("option", "disabled", false);
		$("#toolbar deletebutton").button("option", "disabled", false);
	}
});