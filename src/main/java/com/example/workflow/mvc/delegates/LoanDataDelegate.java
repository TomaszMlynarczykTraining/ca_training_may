package com.example.workflow.mvc.delegates;

import com.example.workflow.mvc.entity.Client;
import com.example.workflow.mvc.entity.Loan;
import com.example.workflow.mvc.service.ClientService;
import com.example.workflow.mvc.service.LoanService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanDataDelegate implements JavaDelegate {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    ClientService clientService;

    @Autowired
    LoanService loanService;


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<Loan> loans = loanService.getLoansByClientId(3);

        Client client = clientService.getClientById(3L);

        Long declaredIncome = Long.valueOf(client.getDeclaredIncome());
        Long debtAmount = Long.valueOf(client.getDebt().getAmount());
        long loanAmount = loans.stream().map(Loan::getAmount)
                .mapToLong(Long::valueOf)
                .sum();

        delegateExecution.setVariable("declaredIncome", declaredIncome);
        delegateExecution.setVariable("debtAmount", debtAmount);
        delegateExecution.setVariable("loanAmount", loanAmount);
    }
}
