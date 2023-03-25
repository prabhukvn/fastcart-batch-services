package com.kvn.batch.process;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import com.kvn.batch.process.products.Product;
import com.kvn.batch.process.products.ProductItemProcessor;
import com.kvn.batch.process.products.ProductsRowMapper;

@Configuration
public class DatabaseBatchConfiguration {

	@Autowired
	@Qualifier("switchingA")
	private DataSource switchingADatasource;
	
	@Autowired
	private DataSource dataSource;
	
	  @Autowired
	  public StepBuilderFactory stepBuilderFactory;
	  

	String query = "SELECT PRODUCT_ID , DISPLAY_NAME, DESCRIPTION ,BRAND  FROM DCS_PRODUCT";

	@Bean
	JdbcCursorItemReader<Product> jdbcCursorItemReader() {
		return new JdbcCursorItemReaderBuilder<Product>()
				.dataSource(this.switchingADatasource)
				.name("ProductReader")
				.sql(query)
				.rowMapper(new ProductsRowMapper())
				.build();

	}
	
	@Bean
	ProductItemProcessor productItemProcessor() {
		return new ProductItemProcessor();
	}
	
	 @Bean
	  public JdbcBatchItemWriter<Product> jdbcItemWriter() {
	    return new JdbcBatchItemWriterBuilder<Product>()
	      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
	      .sql("INSERT INTO Products_Enriched (product_id,display_name, description,brand) VALUES (:productId, :displayName,:description,:brand)")
	      .dataSource(dataSource)
	      .build();
	  }

	 @Bean
	  public Step enrichProductsStep(TaskExecutor taskExecutor) {
	    return stepBuilderFactory.get("enrichProducts")
	      .<Product, Product> chunk(10)
	      .reader(jdbcCursorItemReader())
	      .processor(this.productItemProcessor())
	      .writer(this.jdbcItemWriter())
	      //.taskExecutor(taskExecutor)
	      .throttleLimit(20)
	      .build();
	  }
	 
}
