package com.example.springBatch.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
    @Id
    private Long id;
    private long accountID; // numero du compte
    private Date transactionDate;
    // Pour eviter le probleme de formattege de la date, definir un autre champ strTransactionDate
    // ItemReader lorsque il va lire la date, il va le stocker dans ce champ de type string
    // Apres on le transforme et on le stocke dans transactionDate de type date
    // on va pas le persister ds la bdd
    @Transient
    private String strTransactionDate;
    private String transactionType;
    private double amount;
}
