package com.ilwllc.sgerke.bme280_spark

/*
 * Arguments:
 * 		args(0) is Kafka Consumer topic
 * 		args(1) is Kafka Producer topic
 * 		args(2) is Kafka server:port
 * 		args(3) is windowDuration, the length of the window
 * 		args(4) is slideDuration, the interval at which the window will slide or move forward
 *
 * 		bme280-topic bme280-stats-topic localhost:9092 600 10
 *
 */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel

import Utilities._

import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder

import org.apache.kafka.clients.producer.{ProducerConfig, KafkaProducer, ProducerRecord}
import java.util.HashMap
import java.sql.Timestamp

object BME280_Statistics {

  val currentTimestamp = java.lang.System.currentTimeMillis().toString()

  def main(args: Array[String]) {

    println("Arguments:")
    println(args(0));
    println(args(1));
    println(args(2));
    println(args(3));
    println(args(4));


    val ssc = new StreamingContext("local[*]", "KafkaExample", Seconds(args(4).toLong))

    setupLogging()

    val kafkaParams = Map("metadata.broker.list" -> args(2))
    val consumerTopics = List(args(0)).toSet
    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, consumerTopics).map(_._2)

    val fahrenheitCurrent = lines.map(x => ({val arr = x.toString().split(", "); if (arr.size == 12) arr(6) else "[error]"},{val arr = x.toString().split(", "); if (arr.size == 12) arr(7) else "[error]"}))
    //fahrenheit.print

    val fahrenheitWindow = fahrenheitCurrent.window(Seconds(args(3).toLong), Seconds(args(4).toLong))

    val fahrenheitMin = fahrenheitWindow.map(x => (x._1, x._2)).reduceByKey((x, y) => (if (x < y) x else y))
    //fahrenheitMin.print

    val fahrenheitMax = fahrenheitWindow.map(x => (x._1, x._2)).reduceByKey((x, y) => (if (x > y) x else y))
    //fahrenheitMax.print

    val fahrenheitSum = fahrenheitCurrent.map(x => (x._1, x._2.toDouble)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(args(3).toLong), Seconds(args(4).toLong))
    val fahrenheitCount = fahrenheitCurrent.map(x => (x._1, 1.0)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(args(3).toLong), Seconds(args(4).toLong))
    val fahrenheitAverage = fahrenheitSum.join(fahrenheitCount).map(x => { (x._1, (x._2._1 / x._2._2)) })
    //fahrenheitAverage.print()

    val fahrenheitStatistics = fahrenheitCurrent.join(fahrenheitMin).join(fahrenheitMax).join(fahrenheitAverage)
    fahrenheitStatistics.print()

    fahrenheitStatistics.foreachRDD( rdd => {
      System.out.println("# events = " + rdd.count())

      rdd.foreachPartition( partition => {
        val producerTopic = args(1)
        val props = new HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, args(2))
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

        val producer = new KafkaProducer[String, String](props)
        partition.foreach( record => {
          val data = record.toString
          val message = new ProducerRecord[String, String](producerTopic, null, data)
          producer.send(message)
        })
        producer.close()
      })

    })

    ssc.checkpoint("/home/cloudera/spark/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }

}
