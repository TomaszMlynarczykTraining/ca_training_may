package com.example.workflow;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.decisionService;
import static org.junit.Assert.assertEquals;

@Deployment(resources = {"processes/loan_decision_grupa3.dmn"})
public class Group3DMNTest extends AbstractProcessEngineRuleTest {

    @Test
    public void shouldExecuteHappyPath() {
        DmnDecisionTableResult decisionResult = decisionService()
                .evaluateDecisionTableByKey("LoanDecisionGrupa3",
                        Variables.createVariables()
                                .putValue("declaredIncome", 2500)
                                .putValue("debtAmountSum", 2500)
                                .putValue("loanAmountSum", 2500));

        assertEquals("SHORT", decisionResult.getFirstResult().get("loanOption"));
    }
}
