package pl.speedlog.kafka.consumer;

import com.github.blindpirate.extensions.CaptureSystemOutput;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;

class CommandsTest {

	private final Commands commands = new Commands(mock(PrintCommand.class));

	@Test
	@CaptureSystemOutput
	void errorWhenNoOptionsGiven(CaptureSystemOutput.OutputCapture outputCapture) {
		outputCapture.expect(containsString("Missing required options: '--topic=<topicName>', '--timestamp=<timestampMs>"));
		commands.run();
	}

	@Test
	@CaptureSystemOutput
	void invalidTimestamp(CaptureSystemOutput.OutputCapture outputCapture) {
		outputCapture.expect(containsString("Invalid value for option '--timestamp': 'invalid' is not a long"));
		commands.run("--topic=example-topic", "--timestamp=invalid");
	}

}
