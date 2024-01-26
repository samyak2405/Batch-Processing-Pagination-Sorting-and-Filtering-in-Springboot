package com.javahunter.BatchProcessing.config;

import com.javahunter.BatchProcessing.entity.Product;
import com.javahunter.BatchProcessing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final ProductRepository productRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public FlatFileItemReader<Product> itemReader(){
        FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/DemoProductList.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(20);
        return asyncTaskExecutor;
    }

    @Bean
    public ProductProcessor processor(){
        return new ProductProcessor();
    }

    @Bean
    public RepositoryItemWriter<Product> writer(){
        RepositoryItemWriter<Product> writer = new RepositoryItemWriter<>();
        writer.setRepository(productRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step importStep(){
        return new StepBuilder("csvImport",jobRepository)
                .<Product,Product>chunk(100,platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(100000)
                .processorNonTransactional()
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(){
        return new JobBuilder("importProduct",jobRepository)
                .start(importStep())
                .build();
    }

    private LineMapper<Product> lineMapper() {
        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("ProductName","ProductDescription","Price","Quantity");
        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    //Alternative way
//    @Bean
//    public Job bookReaderJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
//        return new JobBuilder("bookReadJob",jobRepository).incrementer(new RunIdIncrementer())
//                .start(chunkStep(jobRepository,platformTransactionManager))
//                .build();
//    }
//
//    @Bean
//    public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
//        return new StepBuilder("bookReaderStep",jobRepository).<BookEntity, BookEntity>chunk(10,platformTransactionManager)
//                .reader(reader())
//                .processor(processor())
//                .writer(writer())
//                .build();
//    }
//
//    @Bean
//    public ItemWriter<BookEntity> writer(){
//        return new BookWriter();
//    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemReader<BookEntity> reader(){
//        return new FlatFileItemReaderBuilder<BookEntity>()
//                .name("bookReader")
//                .resource(new ClassPathResource("book_data.csv"))
//                .delimited()
//                .names(new String[]{"title","author","year_of_publishing"})
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){{
//                    setTargetType(BookEntity.class);
//                }})
//                .build();
//    }
//
//    @Bean
//    public ItemProcessor<BookEntity,BookEntity> processor(){
//        CompositeItemProcessor<BookEntity,BookEntity> processor = new CompositeItemProcessor<>();
    //Multiple processor configuration
//        processor.setDelegates(List.of(new BookTitleProcessor(),new BookAuthorProcessor()));
//        return processor;
//    }

}
