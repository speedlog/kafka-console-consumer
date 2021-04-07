# Kafka console consumer

Console application for reading events from given timestamp.  
May be useful for debugging.  

## Requirements
* java >= 8

## Build
`./mvn install`

## Usage examples

Download the latest release from [https://github.com/speedlog/kafka-console-consumer/releases](https://github.com/speedlog/kafka-console-consumer/releases).

### Configuration

You need to specific in `application.properties` file in the same directory as tool configuration to connect with kafka.  
Example configuration for SASL_PLAINTEXT:  
```
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.properties.security.protocol=SASL_PLAINTEXT
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="EXAMPLE_USERNAME" password="EXAMPLE_PASSWORD";
```

You can set how many events would be read from topic by setting property:
```
spring.kafka.consumer.max-poll-records=1000
```

For more properties you can read springboot documentation [https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-integration](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties-integration).  
Search `spring.kafka.*` properties.

### Run examples

#### Events from given timestamp
```
./kafka-consumer --topic TOPIC_NAME --timestamp TIMESTAMP_IN_MS
```

Example result
```
There is no message after given timestamp in partition 1
There is no message after given timestamp in partition 0
Partition: 2, offset: 1, event: {"name":"event","number":6}
```

## Motivation to create this tool

Command line (`kafka-console-consumer.sh`) distributed with kafka doesn't have features to read events from given timestamp.  

There are another tools to read events from given timestamp:  
* [https://github.com/edenhill/kafkacat](https://github.com/edenhill/kafkacat)

Above tools require installed specific OS package or docker to run.  
There wasn't such tool that requires only java - so now there it is :-)