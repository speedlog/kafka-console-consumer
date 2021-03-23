package pl.speedlog.kafka.consumer.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author <a href="mailto:mariusz@wyszomierski.pl">mariusz@wyszomierski.pl</a>
 */
@Data
@AllArgsConstructor
public class ExampleEvent {

    private String name;
    private Integer number;

}
