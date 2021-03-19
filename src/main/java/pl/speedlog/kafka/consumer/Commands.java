package pl.speedlog.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * @author <a href="mailto:mariusz@wyszomierski.pl">mariusz@wyszomierski.pl</a>
 */
@Component
@RequiredArgsConstructor
public class Commands implements CommandLineRunner {

    private final PrintCommand printCommand;

    @Override
    public void run(String... args) {
        new CommandLine(printCommand).execute(args);
    }
}
