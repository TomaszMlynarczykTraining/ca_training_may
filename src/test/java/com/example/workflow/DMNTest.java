package com.example.workflow;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Deployment(resources = {"processes/diagram_1.dmn"})
public class DMNTest extends AbstractProcessEngineRuleTest {

    @Test
    public void shouldExecuteHappyPath() {

        DmnDecisionTableResult dmnDecisionRuleResults = processEngine.getDecisionService().
                evaluateDecisionTableByKey("Decision_0fjsi5x",
                        Variables.createVariables().putValue("pizzaType", "OTHER"));

        assertEquals("false", dmnDecisionRuleResults.getFirstResult().getSingleEntry());

    }
}
