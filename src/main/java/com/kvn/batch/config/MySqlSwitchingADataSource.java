package com.kvn.batch.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(name = "spring.datasource.mysql.switchinga.url")
public class MySqlSwitchingADataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlSwitchingADataSource.class);

	@Autowired
	private SwitchingAConfig switchingAConfig;

	@Bean
	@Qualifier("switchingA")
	public DataSource switchingADatasource() {

		LOGGER.info("Creating the Data Source for {}", switchingAConfig.getUsername());
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(Driver.class.getName());
		hikariConfig.setUsername(switchingAConfig.getUsername());
		hikariConfig.setPassword(switchingAConfig.getPassword());
		hikariConfig.setJdbcUrl(switchingAConfig.getUrl());
		hikariConfig.setMaximumPoolSize(20);
		hikariConfig.setMinimumIdle(5);
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		return dataSource;
	}

	@Bean
	@Qualifier("switchingATemplate")
	public JdbcTemplate switchingAJDBCTemplate() {
		return new JdbcTemplate(switchingADatasource());
	}

}
