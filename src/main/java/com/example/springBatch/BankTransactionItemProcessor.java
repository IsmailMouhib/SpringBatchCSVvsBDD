package com.example.springBatch;

import com.example.springBatch.dao.BankTransaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

// Pour qu il soit un composant spring afin qu il soit detecter au demarrage
// Maniere 2
//@Component
// on enleve @componenet car on va l instancie dans la classe de configuration comme bean pour l injecter dans le composite processeur
public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction, BankTransaction> {
    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        //parse StringDate to date
        // on modifie les donnees de notre objet pour montrer un exemple de traitement au niveau de processor
        bankTransaction.setTransactionDate(simpleDateFormat.parse(bankTransaction.getStrTransactionDate()));

        return bankTransaction;
    }
}
