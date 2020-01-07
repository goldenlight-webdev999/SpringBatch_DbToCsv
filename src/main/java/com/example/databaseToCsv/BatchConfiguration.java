package com.example.databaseToCsv;

import com.example.databaseToCsv.model.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private static final String WILL_BE_INJECTED = null;

    @Autowired
    public DataSource dataSource;

    @Bean
    @StepScope
    public JdbcCursorItemReader<User> reader(@Value("#{jobParameters['Count']}") String count){
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<User>();
        reader.setDataSource(dataSource);
        final int limit = 10000;
        final int offset = Integer.parseInt(count) * 10000;
        reader.setSql("SELECT * FROM `user` WHERE 1 LIMIT "+ offset + "," + limit);
        reader.setRowMapper(new UserRowMapper());

        return reader;
    }

    public class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            return user;
        }

    }

    @Bean
    public UserItemProcessor processor(){
        return new UserItemProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<User> writer(@Value("#{jobParameters['FileName']}") String fileName){
        final Resource outputResource = new FileSystemResource("output/" + fileName);
        FlatFileItemWriter<User> writer = new FlatFileItemWriter<User>();
        writer.setResource(outputResource);
        writer.setLineAggregator(new DelimitedLineAggregator<User>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<User>() {{
                setNames(new String[] { "id","name", "email" });
            }});
        }});

        return writer;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<User, User> chunk(10)
                .reader(reader(WILL_BE_INJECTED))
                .processor(processor())
                .writer(writer(WILL_BE_INJECTED))
                .build();
    }

    @Bean
    public Job exportUserJob() {
        return jobBuilderFactory.get("exportUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

}
