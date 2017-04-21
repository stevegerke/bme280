# BME280

The Adafruit BME280 sensor is an environmental sensor with temperature, barometric pressure and humidity. For this IoT starter project, the sensor was connected to the Raspberry Pi3 I2C interface. The initial project reads the data from the bme280 sensor, calculates the maximum, minimum, and average temperature for the defined sliding window, and displays a the data points on a line graph.


BME280_Read.java reads the data from the bme280 sensor and displays the results to the screen. This is a good introduction to programming on a Raspberry Pi.

BME280_Producer.java reads the data from the bme280 sensor and sends it to Kafka.

SimpleConsumer.java gets the data from a Kafka topic and writes it to HDFS.

BME280_Statitics.scala gets the data from a Kafka topic, calculates the maximum, minimum, and average temperature, and sends it to a new Kafka topic.

BME280_dashboard uses Node.js to get the maximum, minimum, average, and current temperature from Kafka topic bme280-stats-topic and display it on a line chart using Google Charts. This chart updates based on the sliding window defined in the Spark program.

BME280_dashboard_simple uses Node.js to get the current temperature from Kafka topic bme280-topic and immediately display it on a line chart using Google Charts.
