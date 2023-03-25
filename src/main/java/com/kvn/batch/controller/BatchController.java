package com.kvn.batch.controller;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.DataFieldMaxValueJobParametersIncrementer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kvn.batch.process.JobCompletionNotificationListener;

@RestController
@RequestMapping("/batch/jobs")
public class BatchController {
	
	  @Autowired
	  public JobBuilderFactory jobBuilderFactory;
	  @Autowired
	  JobCompletionNotificationListener listener;
	  

	  @Autowired
	  private DataSource dataSource;
	  
	  @Autowired
	  private Step step1;
	  
	  @Autowired(required = false)
	  @Qualifier("batch")
	  private Step partitionerStep;
	  
	  @Autowired
	  private JobRepository jobRepository;
	  
	  @Autowired
	  private Step enrichProductsStep;
	  
	  
	  @Autowired
	  JobLauncher jobLauncher;
	  
	  @PostMapping("/run/{jobName}")
	  public ResponseEntity<String> runJob(@PathVariable String jobName){
		  DataFieldMaxValueJobParametersIncrementer dataFieldMaxValueJobParametersIncrementer = new DataFieldMaxValueJobParametersIncrementer(new DefaultDataFieldMaxValueIncrementerFactory(dataSource).getIncrementer(DatabaseType.MYSQL.name(), DatabaseType.MYSQL.getProductName()));

		  Job job = jobBuilderFactory.get(jobName)
				  .incrementer(dataFieldMaxValueJobParametersIncrementer)
				  
			      .listener(listener)
			      .flow(step1)
			      .end()
			      .build();
		  
		  Job job1 = new JobBuilder(jobName)
				  .repository(jobRepository)
				  
				  .listener(listener)
				  .incrementer(new RunIdIncrementer())
				  .start(step1)
				  .build();
		  
		  JobParameters jobParams = new JobParametersBuilder().addLong("Time", System.currentTimeMillis()).toJobParameters();
		  
		  try {
			JobExecution jobExecution = jobLauncher.run(job1, jobParams);
			jobExecution.getExecutionContext().putString("User", "Prabhu KVN");
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return ResponseEntity.ok("Job Started");
		  
	  }
	  
	  @PostMapping("/run/products/{jobName}")
	  public ResponseEntity<String> runProductsJob(@PathVariable String jobName){

		  Job job1 = new JobBuilder(jobName)
				  .repository(jobRepository)
				  .listener(listener)
				  .incrementer(new RunIdIncrementer())
				  .start(enrichProductsStep)
				  .build();
		  
		  JobParameters jobParams = new JobParametersBuilder().addLong("Time", System.currentTimeMillis()).toJobParameters();
		  
		  try {
			JobExecution jobExecution = jobLauncher.run(job1, jobParams);
			jobExecution.getExecutionContext().putString("User", "Prabhu KVN");
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return ResponseEntity.ok("Job Started");
		  
	  }
	  
	  @PostMapping("/run/distributed/{jobName}")
	  @Profile("manager")
	  public ResponseEntity<String> runDistributedJob(@PathVariable String jobName){
		  
		  Job job = jobBuilderFactory.get(jobName)
			      .incrementer(new RunIdIncrementer())
			      .listener(listener)
			      .flow(partitionerStep)
			      .end()
			      .build();
		  JobParameters jobParams = new JobParametersBuilder().addLong("Time", System.currentTimeMillis()).toJobParameters();
		  try {
			jobLauncher.run(job, jobParams);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return ResponseEntity.ok("Job Started");
		  
	  }
	  
	  
	  
	  

}
