package com.example.springBatch;

import com.example.springBatch.dao.BankTransaction;
import com.example.springBatch.dao.BankTransactionRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankTransactionItemWriter implements ItemWriter<BankTransaction> {

    // pour acceder à la BDD, on passe par la couche DAO
    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Override
    public void write(List<? extends BankTransaction> list) throws Exception {
        bankTransactionRepository.saveAll(list);
    }
}
