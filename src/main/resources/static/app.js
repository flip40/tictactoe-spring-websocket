var stompClient = null;
var uid = randString(32);
var gid = null;
var gamestate = null;
var player = "X";

function setConnected(connected) {
	$("#disconnect").prop("disabled", !connected);
}

function connect() {
	var socket = new SockJS('/ttt-websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/ttt/gamestate/' + gid, function (data) {
			updateGamestate(JSON.parse(data.body));
		});
	});
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
	
	refresh();
	
	$("#menu").removeClass('hidden');
	$("#tictactoe").addClass('hidden');
	
	cleanGamestate();
	
	if (gid != null) {
		$.ajax({
			url: "/ttt/game",
			type: "patch",
			data: {
				id: gid,
				player: uid,
				disconnect: true
			}
		});
	}
}

function sendMove(x, y) {
	stompClient.send("/ttt/move/" + gid, {}, JSON.stringify({'player': uid, 'x': x, 'y': y}));
}

function refresh() {
	$.getJSON("/ttt/games/", function(data){
		$("#games").empty();
		
		for (var game in data) {
			game = data[game];
			$("#games").append("<tr><td><button id=\"" + game.id + "\" class=\"btn btn-primary btn-sm\">Join</button>&nbsp;" + game.name  + "</td></tr>");
		}
	});
}

function create() {
	var name = $("#gamename").val() || undefined;
	
	$.post({
		url: "/ttt/game",
//		type: "get",
		data: { 
			player: uid,
			name: name
		}
	}).done(function(data) {
		console.log("Created Game");
		
		player = "X";
		
		$("#menu").addClass('hidden');
		$("#tictactoe").removeClass('hidden');
		
		gid = data.id;
		connect();
		
		updateGamestate(data);
	});
}

function join(id) {
	$.ajax({
		url: "/ttt/game",
		type: "patch",
		data: {
			player: uid,
			id: id
		}
	}).done(function(data) {
		if (!data) {
			alert("Game is already full!", refresh);
			refresh();
			return;
		}
		
		console.log("Joined Game");
		
		player = "O";
		
		$("#menu").addClass('hidden');
		$("#tictactoe").removeClass('hidden');
		
		gid = data.id;
		connect();
		
		updateGamestate(data);
	});
}

function updateGamestate(data) {
	gamestate = data;
	
	gameStatus();
	drawBoard();
}

function cleanGamestate() {
	gamestate = null;
		
	for (var x = 0; x < 3; x++) {
		for (var y = 0; y < 3; y++) {
			$("#".concat(x).concat(y)).text("");
		}
	}
}

function drawBoard() {
	for (var x = 0; x < 3; x++) {
		for (var y = 0; y < 3; y++) {
			$("#".concat(x).concat(y)).text(gamestate.board[x][y]);
		}
	}
}

function gameStatus() {
	var status = "Both players are here! You are '" + player + "'.";
	
	if (!gamestate.started) {
		status = "Waiting for second player...";
	}
	else if (gamestate.disconnect) {
		status = "Other player has disconnected!"
	}
	else if (gamestate.winner) {
		if (gamestate.winner == uid) {
			status = "You win!";
		}
		else {
			status = "You lost!";
		}
	}
	else if (gamestate.draw) {
		status = "It's a draw!"
	}
	
	$("#status").text(status);
}

function randString(length) {
	var text = "";
	var alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	for (var i = 0; i < length; i++) {
		text += alphanum.charAt(Math.floor(Math.random() * alphanum.length));
	}

	return text;
}

$(function () {
	$("form").on('submit', function (e) {
		e.preventDefault();
	});
	
	$("#refresh").click(function() { refresh(); });
	$("#create").click(function() { create(); });
	$( "#disconnect" ).click(function() { disconnect(); });
	
	$("#games").on( "click", "button", function() {
		join( $(this).attr('id') );
	});
	
	$("#board").on( "click", "td", function() {
		sendMove($(this).attr('x'), $(this).attr('y'));
	});
	
	refresh();
});


window.onbeforeunload = disconnect;