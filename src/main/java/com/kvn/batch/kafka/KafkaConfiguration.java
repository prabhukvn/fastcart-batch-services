package com.kvn.batch.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.kvn.batch.manager.Constants;

@Configuration
public class KafkaConfiguration {
   // @Bean
    public NewTopic topic() {
        return TopicBuilder.name(Constants.TOPIC_NAME)
                .partitions(Constants.TOPIC_PARTITION_COUNT)
                .build();
    }
}
