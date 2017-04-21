package com.ilwllc.sgerke.bme280_spark;

/* Execute the following at the command line to run:
 * 		java -Xmx512M -jar BME280_Statistics.jar bme280-topic bme280-stats-topic localhost:9092 600 10
 * 
 * Arguments:
 * 		args(0) is Kafka topic
 * 		args(1) is Kafka server:port
 * 		args(2) is windowDuration, the length of the window
 * 		args(3) is slideDuration, the interval at which the window will slide or move forward
 *
 */

class BME280_StatisticsMain  {
    public static void main(String[] args) {
    	BME280_Statistics.main(args);
    }
}
