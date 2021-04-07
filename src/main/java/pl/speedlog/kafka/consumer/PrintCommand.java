package pl.speedlog.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used fragment from GIST:
 * https://gist.github.com/werneckpaiva/466e7c6bd1eca98ee4c004f37b544de9#file-kafkaseekbytimestamp-java
 *
 * @author <a href="mailto:mariusz@wyszomierski.pl">mariusz@wyszomierski.pl</a>
 */
@Component
@RequiredArgsConstructor
@CommandLine.Command(name = "./kafka-consumer",
        mixinStandardHelpOptions = true,
        description = "Read events from given topic",
        versionProvider = PropertiesVersionProvider.class
)
@Slf4j
public class PrintCommand implements Runnable {

    private final KafkaProperties kafkaProperties;

    @CommandLine.Option(names = {"--topic"}, required = true, description = "Topic name")
    private String topicName;

    @CommandLine.Option(names = {"--timestamp"}, required = true, description = "Read events from given timestamp in milliseconds")
    private long timestampMs;

    @Override
    public void run() {
        readFromTimestamp();
    }

    private void readFromTimestamp() {
        KafkaConsumer<String, String> consumer = createConsumer();
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);

        // Transform PartitionInfo into TopicPartition
        List<TopicPartition> topicPartitionList = getTopicPartitions(partitionInfos);

        // Assign the consumer to these partitions
        consumer.assign(topicPartitionList);

        // Look for offsets based on timestamp
        Map<TopicPartition, Long> partitionTimestampMap = searchPartitionsByOffset(topicPartitionList);
        Map<TopicPartition, OffsetAndTimestamp> partitionOffsetMap = consumer.offsetsForTimes(partitionTimestampMap);

        // Force the consumer to seek for those offsets
        partitionOffsetMap.forEach((tp, offsetAndTimestamp) -> {
            if (offsetAndTimestamp == null) {
                log.info("There is no message after given timestamp in partition {}", tp.partition());
            } else {
                consumer.seek(tp, offsetAndTimestamp.offset());
            }
        });
        ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(2));
        for(ConsumerRecord<String, String> consumerRecord : poll) {
            log.info("Partition: {}, offset: {}, event: {}", consumerRecord.partition(), consumerRecord.offset(), consumerRecord.value());
        }
    }

    private Map<TopicPartition, Long> searchPartitionsByOffset(List<TopicPartition> topicPartitionList) {
        return topicPartitionList.stream()
                .collect(Collectors.toMap(tp -> tp, tp -> timestampMs));
    }

    private List<TopicPartition> getTopicPartitions(List<PartitionInfo> partitionInfos) {
        return partitionInfos
                .stream()
                .map(info -> new TopicPartition(topicName, info.partition()))
                .collect(Collectors.toList());
    }

    private KafkaConsumer<String, String> createConsumer() {
        return new KafkaConsumer<>(kafkaProperties.buildConsumerProperties());
    }
}
