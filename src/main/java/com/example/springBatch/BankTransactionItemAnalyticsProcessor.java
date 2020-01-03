package com.example.springBatch;

import com.example.springBatch.dao.BankTransaction;
import lombok.Getter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

// Pour qu il soit un composant spring afin qu il soit detecter au demarrage
// Maniere 2
// on cree un autre traitement cote proceseur
//@Component// on enleve @componenet car on va l instancie dans la classe de configuration comme bean pour l injecter dans le composite processeur
public class BankTransactionItemAnalyticsProcessor implements ItemProcessor<BankTransaction, BankTransaction> {

    //calculrer total debit et credit
    @Getter private double totalDebit;
    @Getter private double totalCredit;

    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {
        if(bankTransaction.getTransactionType().equals("D")) totalDebit += bankTransaction.getAmount();
        else if(bankTransaction.getTransactionType().equals("C")) totalCredit += bankTransaction.getAmount();
        return bankTransaction;
    }
}
