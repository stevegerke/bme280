/*jshint esversion: 6 */

const path = require('path');
const http = require('http');
const express = require('express');
const socketIO = require('socket.io');
const kafka = require('kafka-node');

const topic = 'bme280-topic';
const partition = 0;
const port = process.env.PORT || 3001;
const publicPath = path.join(__dirname, '../public');

var app = express();
var server = http.createServer(app);
var io = socketIO(server);

app.use(express.static(publicPath));

var Consumer = kafka.Consumer,
client = new kafka.Client('kafka-server:2181'),
	consumer = new Consumer(client, [{
		topic: topic, partition: partition}], {autoCommit: false}
	);

io.on('connection', function (socket) {
	console.log('*** New user connected ***');

	consumer.on('message', function (message) {
		console.log(message.value);
		socket.emit('newMessage', {value: message.value});
	});

	socket.on('disconnect', function () {
		console.log('*** User was disconnected ***');
  });
});

server.listen(port, function () {
	console.log(`Server is up on ${port}`);
});
