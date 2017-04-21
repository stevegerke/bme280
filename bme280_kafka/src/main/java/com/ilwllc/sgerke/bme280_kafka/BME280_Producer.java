package com.ilwllc.sgerke.bme280_kafka;

/* Read weather data from the BME280 sensor, print to standard output, and send to kafka
 *
 * Execute the following at the command line to run:
 * 		java -jar BME280_Stream.jar bme280-topic 5 kafka-server:9092
 *			Note that kafka-server must be defined in the /etc/hosts file
 */

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.ClientProtocolException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class BME280_Producer {
	public static void main(String[] args) throws ClientProtocolException, IOException {

		//Arguments must be passed for topic, sleep-time, and kafka-server:port
		if(args.length != 3){
			System.out.println("***** Missing arguments *****");
			System.out.println("***** Arguments must be passed for topic, sleep-time, and kafka-server:port *****");
			return;
		}

		String topicName = args[0].toString();

		Properties props = new Properties();
		//props.put("bootstrap.servers", "localhost:9092");
		//props.put("bootstrap.servers", "quickstart.cloudera:9092");
		props.put("bootstrap.servers", args[2]);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);

		int i = 1;

		while (true) {
			BME280_I2CBus bme280 = new BME280_I2CBus();
			HostAddress hostaddress = new HostAddress();
			try {
	            String bme280_value = "Host-Address, " + hostaddress.value() + ", " + bme280.value();
				System.out.println(bme280_value);

				Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i), bme280_value));
				RecordMetadata recordMetadata = future.get();

				i = i + 1;
				if (i == 2147483647) i = 1;

				TimeUnit.SECONDS.sleep(Integer.parseInt(args[1]));

			} catch(Exception exception) {
				System.out.println("***** exception *****");
				System.out.println(exception);
				producer.close();
				System.exit(1);
		    }
		}
	}
}
