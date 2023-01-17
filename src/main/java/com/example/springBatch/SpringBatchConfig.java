package com.example.springBatch;

import com.example.springBatch.dao.BankTransaction;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;


// Creer une classe de configuration pour spring Batch
@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    // Configurer un JOB
    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;

    // Ces trois variables pour qu ils soient interprete par le context, il faut les definier en :
    // 2 manieres à faire,
    // soit une methode directement dans cette classe qui retourne le bean
    // Ou bien on cree une classe qui implemente l intreface concerne mais avec l annotation Componenent
    @Autowired private ItemReader<BankTransaction> bankTransactionItemReader;
    //@Autowired private ItemProcessor<BankTransaction,BankTransaction> bankTransactionItemProcessor;
    @Autowired private ItemWriter<BankTransaction> bankTransactionItemWriter;

    @Bean
    public Job BankJob(){
        // Creer la step
        // batch.core.job.SimpleStepHandler     : Executing step: [step-load-data]
        Step step1 = stepBuilderFactory.get("step-load-data")
                .<BankTransaction, BankTransaction>chunk(100)
                .reader(bankTransactionItemReader)
                //.processor(bankTransactionItemProcessor)
                .processor(compositeItemProcessor())
                .writer(bankTransactionItemWriter)
                .build();
        // Retourner JOB
        // SimpleJobLauncher      : Job: [SimpleJob: [name=bank-data-loader-job]] launched with the following parameters: [{}]
        return jobBuilderFactory.get("bank-data-loader-job").start(step1).build();
    }

    // créer un pipeline de ItemProcessor en utiliser CompositeItemProcessor
    @Bean
    public ItemProcessor<BankTransaction, BankTransaction> compositeItemProcessor(){

        // Creer une liste de processor
        List<ItemProcessor<BankTransaction, BankTransaction>> itemProcessors = new ArrayList<>();
        // INJECTER LES PROCESSSOR
        itemProcessors.add(itemProcessor1());// formatter la date
        itemProcessors.add(itemProcessor2());// calcul total debit et credit

        // creer un pipe de processor
        CompositeItemProcessor<BankTransaction, BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;

    }

    // INJECTER LES PROCESSSOR
    @Bean
    BankTransactionItemProcessor itemProcessor1(){
        return new BankTransactionItemProcessor();
    }
    @Bean
    BankTransactionItemAnalyticsProcessor itemProcessor2(){
        return new BankTransactionItemAnalyticsProcessor();
    }

    //Maniere 1
    // le bean va etre cree au demarrage de l app
    // @Value on specifie le patch au niveau de application.properties
    @Bean
    public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource inputFile){
        // dans le cas de lire JSON il y a la l objet JsonItemReader
        FlatFileItemReader<BankTransaction> fileItemReader = new FlatFileItemReader<>(); // on cree l objet qui va lire notre fichier plat csv
        fileItemReader.setName("FFR1"); // renoumer le fichier
        fileItemReader.setLinesToSkip(1); // skip l entete de fichier, la premiere line
        fileItemReader.setResource(inputFile); // pointer sur le chemin de ficher
        fileItemReader.setLineMapper(lineMapper());// LineMapper est un autre objet qui se charge à traiter une ligne
        return fileItemReader;
    }

    @Bean
    public LineMapper<BankTransaction> lineMapper() {

        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(","); // specifier le delimiteur dans le fichier
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "accountID", "strTransactionDate", "transactionType", "amount");// copie coller les noms de notre entite
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(BankTransaction.class);// on specifie le type cible, qu on va lire une ligne, il faut la stocker dans un objet de type BankTransaction
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // Un Bean est un objet qui est instancié, assemblé et géré par Spring IoC Container.
    // Spring scanne toutes les classes. Dès lors qu’il trouve une annotation @Component, il l’enregistre automatiquement en tant que Bean.
    // @Bean and @Component in common, The essential thing both annotations help with is adding Spring Bean to the Spring Context
    // @Bean works in conjunction with a configuration class (with @Configuration)
    // @Component is used on our classes, so Spring knows that it should add it. With a component scan, Spring will scan
    // the entire classpath and will add all @Component annotated classes to the Spring Context (with adjustable Filtering).

}
