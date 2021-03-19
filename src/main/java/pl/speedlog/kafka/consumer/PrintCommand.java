package pl.speedlog.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * @author <a href="mailto:mariusz@wyszomierski.pl">mariusz@wyszomierski.pl</a>
 */
@Component
@RequiredArgsConstructor
@CommandLine.Command(name = "./kafka-consumer", mixinStandardHelpOptions = true, description = "Read events from given topic")
public class PrintCommand implements Runnable {

    @CommandLine.Option(names = {"--topic"}, required = true, description = "Topic name")
    private String topicName;

    @CommandLine.Option(names = {"--timestamp"}, required = true, description = "Read events from given timestamp in milliseconds")
    private long timestampMs;

    @Override
    public void run() {

    }
}
