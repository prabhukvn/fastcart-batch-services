package com.kvn.batch.process;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.DataFieldMaxValueJobParametersIncrementer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.oracle.jrockit.jfr.DataType;

@Configuration
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;
  

  @Autowired
  private DataSource dataSource;

 

  @Bean
  public FlatFileItemReader<Person> reader() {
    return new FlatFileItemReaderBuilder<Person>()
      .name("personItemReader")
      .resource(new ClassPathResource("sample-data.csv"))
      .delimited()
      .names(new String[]{"firstName", "lastName"})
      .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
        setTargetType(Person.class);
      }})
      .build();
  }

  @Bean
  public PersonItemProcessor processor() {
    return new PersonItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Person>()
      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
      .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
      .dataSource(dataSource)
      .build();
  }
  
 // @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
	  DataFieldMaxValueJobParametersIncrementer dataFieldMaxValueJobParametersIncrementer = new DataFieldMaxValueJobParametersIncrementer(new DefaultDataFieldMaxValueIncrementerFactory(dataSource).getIncrementer(DatabaseType.MYSQL.name(), DatabaseType.MYSQL.getProductName()));
    return jobBuilderFactory.get("importUserJob")
      .incrementer(dataFieldMaxValueJobParametersIncrementer)
      .listener(listener)
      .flow(step1)
      .end()
      .build();
  }

  @Bean
  public Step step1(JdbcBatchItemWriter<Person> writer,TaskExecutor taskExecutor) {
    return stepBuilderFactory.get("step1")
      .<Person, Person> chunk(10)
      .reader(reader())
      .processor(processor())
      .writer(writer)
      .taskExecutor(taskExecutor)
      .throttleLimit(10)
      
      .build();
  }
  
  @Bean
  public TaskExecutor taskExecutor() {
      return new SimpleAsyncTaskExecutor("spring_batch");
  }

}