# Development Commands

This document contains commonly used commands for developing, running,
testing, debugging, and operating the Uber Backend microservices locally.

> These commands are intended for local development and learning.

# Kafka

### Create a Kafka topic

```text
> docker exec uber-backend-kafka \
/opt/kafka/bin/kafka-topics.sh \
--create \
--topic trip-events \
--bootstrap-server localhost:9092 \
--partitions 1 \
--replication-factor 1
```

### List Kafka topics

```text
> docker exec uber-backend-kafka \
  /opt/kafka/bin/kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092
```

### Describe a topic

```text
> docker exec uber-backend-kafka \
  /opt/kafka/bin/kafka-topics.sh \
  --describe \
  --topic trip-events \
  --bootstrap-server localhost:9092
```

### Produce messages manually

```text
> docker exec -it uber-backend-kafka \
  /opt/kafka/bin/kafka-console-producer.sh \
  --topic trip-events \
  --bootstrap-server localhost:9092
```

### Consume messages manually

```text
> docker exec -it uber-backend-kafka \
  /opt/kafka/bin/kafka-console-consumer.sh \
  --topic trip-events \
  --bootstrap-server localhost:9092
```

### Consume messages from the beginning

```text
> docker exec -it uber-backend-kafka \
  /opt/kafka/bin/kafka-console-consumer.sh \
  --topic trip-events \
  --bootstrap-server localhost:9092 \
  --from-beginning
```
