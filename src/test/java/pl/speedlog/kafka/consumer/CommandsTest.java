package pl.speedlog.kafka.consumer;

import com.github.blindpirate.extensions.CaptureSystemOutput;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;

class CommandsTest {

	@Test
	@CaptureSystemOutput
	void errorWhenNoOptionsGiven(CaptureSystemOutput.OutputCapture outputCapture) {
		outputCapture.expect(containsString("Missing required options: '--topic=<topicName>', '--timestamp=<timestampMs>"));
		KafkaConsumerApplication.main(new String[]{});
	}

	@Test
	@CaptureSystemOutput
	void invalidTimestamp(CaptureSystemOutput.OutputCapture outputCapture) {
		outputCapture.expect(containsString("Invalid value for option '--timestamp': 'invalid' is not a long"));
		KafkaConsumerApplication.main(new String[]{ "--topic=example-topic", "--timestamp=invalid"});
	}

}
