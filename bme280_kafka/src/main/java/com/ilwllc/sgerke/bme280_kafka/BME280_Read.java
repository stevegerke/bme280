package com.ilwllc.sgerke.bme280_kafka;

/* Read weather data from the BME280 sensor and print to standard output
 * 
 * Execute the following at the command line to run: 
 * 		java -jar BME280_Stream.jar 5
 */

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.ClientProtocolException;

public class BME280_Read {
	public static void main(String[] args) throws ClientProtocolException, IOException {

		//Argument must be passed for sleep-time
		if(args.length != 1){
			System.out.println("***** Missing argument *****");
			System.out.println("***** Argument must be passed for sleep-time *****");
			return;
		}
				
		while (true) {
			BME280_I2CBus bme280 = new BME280_I2CBus();
			try {
	            System.out.println(bme280.value());
				TimeUnit.SECONDS.sleep(Integer.parseInt(args[0]));
				
			} catch(Exception exception) {
				System.out.println("***** exception *****");
				System.out.println(exception);
				System.exit(1);
		    }
		}
	}
}