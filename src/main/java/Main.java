import Kafka.IKafkaConstants;
import Kafka.ProducerCreator;
import Consumer.ConsumerCreator;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.bson.Document;

import java.util.concurrent.ExecutionException;

/*
 *Created by Berkay KADAMLI
 */

public class Main {
    public static void main(String[] args) {
//        MongoClient mongoClient = new MongoClient("localhost", 2717);
//        MongoDatabase database = mongoClient.getDatabase("LOAN");
//        MongoCollection<Document> collection = database.getCollection("Loan");
//        Document myDoc = collection.find().first();
//        System.out.println(myDoc.toJson());
//        runConsumer();
        runProducer();

        //print value
//        MongoCursor<Document> cursor = collection.find().iterator();
//        int i = 0;
//        try {
//            while (cursor.hasNext()) {
//                System.out.println((i++) +" "+ cursor.next().toJson());
//            }
//        } finally {
//            cursor.close();
//        }
//                System.out.println(collection.count());
    }


    static void runProducer() {
        MongoClient mongoClient = new MongoClient("localhost", 2717);
        MongoDatabase database = mongoClient.getDatabase("LOAN");
        MongoCollection<Document> collection = database.getCollection("Loan");
        MongoCursor<Document> cursor = collection.find().iterator();
        Producer<Long, String> producer = ProducerCreator.createProducer();

        for (int index = 0; index < collection.count(); index++) {
            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_NAME, cursor.next().toJson());
            try {
                RecordMetadata metadata = producer.send(record).get();
                System.out.println("Record sent with key " + index + " to partition " + metadata.partition()
                        + " with offset " + metadata.offset());
            } catch (ExecutionException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            } catch (InterruptedException e) {
                System.out.println("Error in sending record");
                System.out.println(e);
            }
        }
    }

    static void runConsumer() {
        Consumer<Long, String> consumer = ConsumerCreator.createConsumer();

        int noMessageFound = 0;

        while (true) {
            ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
            // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() == 0) {
                noMessageFound++;
                if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
                    // If no message found count is reached to threshold exit loop.
                    break;
                else
                    continue;
            }

            //print each record.
            consumerRecords.forEach(record -> {
                System.out.println("Record partition " + record.partition());
                System.out.println("Record offset " + record.offset());
                System.out.println("Record value " + record.value());
                System.out.println("-------------------------------");
            });

            // commits the offset of record to broker.
            consumer.commitAsync();
        }
        consumer.close();
    }

}
