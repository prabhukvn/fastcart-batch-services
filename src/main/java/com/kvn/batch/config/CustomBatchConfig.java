package com.kvn.batch.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@EnableTransactionManagement
public class CustomBatchConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomBatchConfig.class);
	
	@Autowired
	private CustomDataSourceConfig dataSourceProperties;

	@Bean
	@Primary
	public DataSource dataSource() {

		LOGGER.info("Creating the Data Source for {}", dataSourceProperties.getUsername());
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(Driver.class.getName());
		hikariConfig.setUsername(dataSourceProperties.getUsername());
		hikariConfig.setPassword(dataSourceProperties.getPassword());
		hikariConfig.setJdbcUrl(dataSourceProperties.getUrl());
		hikariConfig.setMaximumPoolSize(20);
		hikariConfig.setMinimumIdle(5);
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;
	}

	@Bean
	@Primary
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	


}
