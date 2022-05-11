package com.example.workflow.mvc.delegates;

import com.example.workflow.mvc.entity.Client;
import com.example.workflow.mvc.service.ClientService;
import com.example.workflow.mvc.service.LoanService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoadClientDataDelegate implements JavaDelegate {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    ClientService clientService;
    @Autowired
    LoanService loanService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("delegate: LoadClientDataDelegate");

        Long userIdentificationNumber = (Long) delegateExecution.getVariable("userIdentificationNumber");
        Optional<Client> optClient = clientService.findCientById(userIdentificationNumber);
        if (optClient.isPresent()) {
            Client client = optClient.get();

            delegateExecution.setVariable("client_user_income", Long.valueOf(client.getDeclaredIncome()));
            delegateExecution.setVariable("sum_of_debt_amount", Long.valueOf(123));
            delegateExecution.setVariable("sum_of_loan_amount", Long.valueOf(123));
        }
    }
}
