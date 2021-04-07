package pl.speedlog.kafka.consumer;

import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="mailto:mariusz@wyszomierski.pl">mariusz@wyszomierski.pl</a>
 */
public class PropertiesVersionProvider implements CommandLine.IVersionProvider {

    public static final String VERSION_PROPERTY_NAME = "info.app.version";

    @Override
    public String[] getVersion() throws IOException {
        URL url = getClass().getResource("/application.properties");
        if (url == null) {
            return new String[] {"No application.properties file found in the classpath."};
        }
        Properties properties = new Properties();
        try (InputStream inputStream = url.openStream()){
            properties.load(inputStream);
            return new String[] {
                    "Version: " + properties.getProperty(VERSION_PROPERTY_NAME) + "\n" +
                    "See latest version: https://github.com/speedlog/kafka-console-consumer/releases"
            };
        }
    }
}
