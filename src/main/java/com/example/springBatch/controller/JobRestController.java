package com.example.springBatch.controller;

import com.example.springBatch.BankTransactionItemAnalyticsProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// lancer un Job à travers une API Rest, c a d le declencher une fois on appel l url
@RestController
public class JobRestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private BankTransactionItemAnalyticsProcessor analyticsProcessor;

    // comment lancer un job
    @GetMapping("/startJob")
    public BatchStatus load() throws Exception{
        Map<String, JobParameter> params = new HashMap<>();// parametrer notre job
        params.put("time", new JobParameter(System.currentTimeMillis())); // date systeme
        // Job: [SimpleJob: [name=bank-data-loader-job]] completed with the following parameters: [{time=1577903961953}] and the following status: [COMPLETED] in 319ms
        JobParameters jobParameters = new JobParameters(params);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        while (jobExecution.isRunning()){
            System.out.println(".....");
        }
        return jobExecution.getStatus();
        //si le job s execute sans problme, on peut voir http://localhost:8080/startJob
        //on voie STATUS "COMPLETED"
        //devtools nous offre un acces direct à BDD H2 : localhost:8080/h2-console
    }

    // pour consulter le calcul de total credit et debit à traver le controlleur
    // il faut appeler http://localhost:8080/startJob pour activer le job à travers l appel de controlleur et faire la lecture ...
    //apres on fait appel http://localhost:8080/analytics
    @GetMapping("/analytics")
    public Map<String, Double> analytics(){
        Map<String, Double> map = new HashMap<>();
        map.put("Total Credit : ", analyticsProcessor.getTotalCredit());
        map.put("Total Débit : ", analyticsProcessor.getTotalDebit());
        return map;
    }
    // {
    //"Total Débit : ": 4001.2400000000002,
    //"Total Credit : ": 2000.87
    //}
}
