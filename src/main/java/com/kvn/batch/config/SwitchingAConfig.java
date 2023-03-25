package com.kvn.batch.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource.mysql.switchinga")
public class SwitchingAConfig extends DataSourceProperties{

}
