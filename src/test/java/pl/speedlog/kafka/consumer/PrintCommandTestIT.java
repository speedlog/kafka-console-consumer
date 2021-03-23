package pl.speedlog.kafka.consumer;

import com.github.blindpirate.extensions.CaptureSystemOutput;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.speedlog.kafka.consumer.event.ExampleEvent;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Testcontainers
@Import(PrintCommandTestIT.KafkaConfiguration.class)
@ActiveProfiles("test")
class PrintCommandTestIT {

	@Container
	private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
	private static final String TOPIC_NAME = "events.example";
	public static final int PARTITION_NUMBER = 3;

	@Autowired
	private Commands commands;

	private static long beforeSecondSendTimestampMs;
	private static long beforeLastEventSendTimestampMs;

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
	}

	@BeforeAll
	static void beforeAll(@Autowired KafkaTemplate<String, ExampleEvent> kafkaTemplate) throws InterruptedException {
		// send 3 events - each to another partition
		kafkaTemplate.send(TOPIC_NAME, 0, null, new ExampleEvent("event", 1));
		kafkaTemplate.send(TOPIC_NAME, 1, null, new ExampleEvent("event", 2));
		kafkaTemplate.send(TOPIC_NAME, 2, null, new ExampleEvent("event", 3));

		// sleep 4 seconds
		TimeUnit.SECONDS.sleep(2);

		// record current timestamp
		beforeSecondSendTimestampMs = System.currentTimeMillis();

		// send 3 events - each to another partition
		kafkaTemplate.send(TOPIC_NAME,0, null, new ExampleEvent("event", 4));
		kafkaTemplate.send(TOPIC_NAME,1, null, new ExampleEvent("event", 5));
		TimeUnit.SECONDS.sleep(1);
		beforeLastEventSendTimestampMs = System.currentTimeMillis();
		kafkaTemplate.send(TOPIC_NAME,2, null, new ExampleEvent("event", 6));
	}

	@Test
	@CaptureSystemOutput
	void shouldReadEventsFromGivenTimestamp(CaptureSystemOutput.OutputCapture outputCapture) {
		String expectedOutput =
		"Partition: 2, offset: 1, event: {\"name\":\"event\",\"number\":6}\n" +
		"Partition: 1, offset: 1, event: {\"name\":\"event\",\"number\":5}\n" +
		"Partition: 0, offset: 1, event: {\"name\":\"event\",\"number\":4}\n";
		outputCapture.expect(equalTo(expectedOutput));
		commands.run("--topic=" + TOPIC_NAME, "--timestamp=" + beforeSecondSendTimestampMs);
	}

	@Test
	@CaptureSystemOutput
	void shouldInformWhenThereIsNoEventsWithGivenTimestampInPartition(CaptureSystemOutput.OutputCapture outputCapture) {
		String expectedOutput =
				"There is no message after given timestamp in partition 1\n" +
				"There is no message after given timestamp in partition 0\n" +
				"Partition: 2, offset: 1, event: {\"name\":\"event\",\"number\":6}\n";
		outputCapture.expect(equalTo(expectedOutput));
		commands.run("--topic=" + TOPIC_NAME, "--timestamp=" + beforeLastEventSendTimestampMs);
	}

	@TestConfiguration
	static class KafkaConfiguration {

		@Bean
		public NewTopic exampleTopic() {
			return new NewTopic(TOPIC_NAME, PARTITION_NUMBER, (short) 1);
		}

	}

}
