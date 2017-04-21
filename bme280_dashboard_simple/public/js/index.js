/*jshint esversion: 6 */

const maxTotalIntervals = 3600;
var interval = 0;
var temperature = [];
var socket = io();


socket.on('connect', function () {
	console.log('Connected to server');
});


socket.on('disconnect', function () {
	console.log('Disconnected from server');
});


socket.on('newMessage', function (message) {
	console.log(message.value);
	interval++;
	var array = message.value.replace(/"/g, "").replace(/'/g, "").replace(/\(|\)/g, "").split(",");
	console.log(array);
	temperature.push([interval, Number(array[7])]);
	if (temperature.length > maxTotalIntervals ) {
		temperature.shift();
	}
	google.charts.load('current', {'packages':['corechart']});
	google.charts.setOnLoadCallback(drawChart);
	
	function drawChart() {
		data = new google.visualization.DataTable();
		data.addColumn('number', 'Interval');
		data.addColumn('number', 'Current');
		data.addRows(temperature);
		
		var options = {
			title: 'Temperature',
			curveType: 'function',
			legend: { position: 'bottom' },
			vAxis: { scaleType: 'linear' },
			series: {
	            0: { lineWidth: 2 },
	        },
			colors: ['#3949AB']
		};
	  
		var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
		chart.draw(data, options);
	}
});
