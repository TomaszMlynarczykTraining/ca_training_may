package com.example.workflow;

import com.example.workflow.mvc.delegates.MockDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.camunda.community.mockito.DelegateExpressions;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = {"processes/diagram_3.bpmn"})
public class SimpleProcessTest extends AbstractProcessEngineRuleTest {

    @Mock
    MockDelegate mockDelegate;

    @Test
    public void shouldExecuteHappyPath() {
        // given
        MockitoAnnotations.openMocks(this);

        DelegateExpressions.registerJavaDelegateMock("mockDelegate").
                onExecutionSetVariable("key", "value");

        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("PR_003_USER_TASK");

        assertThat(processInstance).isStarted()
                .task()
                .hasDefinitionKey("FirstTask");

        Task task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();


        BpmnAwareTests.complete(task);
        assertThat(processInstance).hasPassed("FirstTask");

        task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task);
        assertThat(processInstance).hasPassed("SecondTask");
        assertThat(processInstance).isEnded();

    }

}
