$(document).ready(function() {

	console.log("createing websocket!");

    ws = new WebSocket("ws://localhost:8080/websocket"); 
    ws.onopen = function(event) { 
	console.log("The WebSocket Connection Is Open."); 
    }
    ws.onmessage = function(event) { 
	console.log("Result= "+event.data); 
    }
    ws.onclose = function(event) { 
	console.log("The WebSocket Connection Has Been Closed."); 
    }
 
    $('#send').click(function() {
	    var msg = $('#msg').val();
	    console.log('sending: ' + msg);
	    ws.send(msg);
	});
});
