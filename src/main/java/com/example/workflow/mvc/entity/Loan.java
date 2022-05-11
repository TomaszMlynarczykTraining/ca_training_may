package com.example.workflow.mvc.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Data
public class Loan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String amount;
    private String currency;
    private String amountOfInstallament;
    @Column(name = "client_id")
    private int clientId;

}
