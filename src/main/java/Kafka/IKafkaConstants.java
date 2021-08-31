package Kafka;

/*
 *Created by Berkay KADAMLI
 */
public interface IKafkaConstants {
    public static String KAFKA_BROKERS = "localhost:29092";

    public static String CLIENT_ID = "client1";

    public static String TOPIC_NAME = "2";

    public static String GROUP_ID_CONFIG = "consumerGroup1";

    public static Integer MAX_NO_MESSAGE_FOUND_COUNT = 100;

    public static String OFFSET_RESET_EARLIER = "earliest";

    public static Integer MAX_POLL_RECORDS = 1;
}
