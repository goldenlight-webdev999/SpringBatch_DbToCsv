package com.example.databaseToCsv;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class DatabaseToCsvApplication {

	private int count = 0;

	public static void main(String[] args) {
		SpringApplication.run(DatabaseToCsvApplication.class, args);
	}

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	@Scheduled(fixedRate = 5000)
	public void perform() throws Exception
	{
		if (count > 9) return;
		final String fileName = String.valueOf(count + 1) + ".csv";
		JobParameters params = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.addString("Count", String.valueOf(count))
				.addString("FileName", fileName)
				.toJobParameters();
		jobLauncher.run(job, params);
		count++;
	}

}
