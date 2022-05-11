package com.example.workflow.mvc.delegates;


import com.example.workflow.mvc.entity.Client;
import com.example.workflow.mvc.entity.Loan;
import com.example.workflow.mvc.service.ClientService;
import com.example.workflow.mvc.service.LoanService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchClientDataDelegate implements JavaDelegate {

    @Autowired
    ClientService clientService;

    @Autowired
    LoanService loanService;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        Long clientId = ((Integer) delegateExecution.getVariable("clientId")).longValue();
        Client client = clientService.getClientById(clientId);
        List<Loan> loans = loanService.getLoansByClientId(clientId.intValue());
        Integer declaredIncome = Integer.valueOf(client.getDeclaredIncome());
        Integer debtsSum = Integer.valueOf(client.getDebt().getAmount());
        int loansSum = loans.stream().mapToInt(loan -> Integer.parseInt(loan.getAmount())).sum();

        delegateExecution.setVariable("declaredIncome", declaredIncome);
        delegateExecution.setVariable("debtAmountSum", debtsSum);
        delegateExecution.setVariable("loanAmountSum", loansSum);
    }
}