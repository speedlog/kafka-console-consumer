package pl.speedlog.kafka.consumer;

import com.github.blindpirate.extensions.CaptureSystemOutput;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
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

	@Test
	@CaptureSystemOutput
	void shouldPrintApplicationVersion(CaptureSystemOutput.OutputCapture outputCapture) {
		outputCapture.expect(matchesPattern("Version: \\d+\\.\\d+\\.\\d+.*\\n.*\\n"));
		commands.run("--version");
	}

}
