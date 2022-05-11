package com.example.workflow;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@Deployment(resources = {"processes/diagram_3.bpmn"})
public class DMNTest extends AbstractProcessEngineRuleTest {

    @Autowired
    private DecisionService decisionService;

    @Test
    public void shouldExecuteHappyPath() {
        DmnDecisionTableResult decisionResult = decisionService
                .evaluateDecisionTableByKey("DMN_001", Variables.createVariables()
                        .putValue("userType", "Worker")
                        .putValue("userName", "Tomasz"));

        assertEquals(true, decisionResult.getFirstResult().get("result"));
    }
}
