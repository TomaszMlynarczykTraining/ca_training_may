package com.example.workflow;

import com.example.workflow.mvc.delegates.PreviousLoansDelegate;
import com.example.workflow.mvc.entity.Loan;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.camunda.community.mockito.DelegateExpressions;
import org.camunda.community.mockito.ProcessExpressions;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = {
        "processes/ShortTermLoanProcess.bpmn"
})
public class ShortTermLoanProcessGroup3Test extends AbstractProcessEngineRuleTest {

    @Mock
    PreviousLoansDelegate previousLoansDelegate;

    @Test
    public void shouldExecuteHappyPath() {
        // given
        registerMocks();

        // when
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("SHORT_TERM_LOAN_PROCESS",
                Map.of("clientId", "1"));

        // then process is started
        assertThat(processInstance).isStarted();

        // and passes VerificationTask
        assertThat(processInstance).task().hasDefinitionKey("VerificationTask");
        Task task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task, Map.of("isApplicable", true));
        assertThat(processInstance).hasPassed("VerificationTask");

        // and passes TimerEvent
        execute(job("TimerEvent"));
        assertThat(processInstance).hasPassed("TimerEvent");

        // and passes ConfirmationTask
        assertThat(processInstance).task().hasDefinitionKey("ConfirmationTask");
        task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task, Map.of("isConfirmed", true));
        assertThat(processInstance).hasPassed("ConfirmationTask");

        // and passes UserTask2
        assertThat(processInstance).task().hasDefinitionKey("UserTask2");
        task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task);
        assertThat(processInstance).hasPassed("UserTask2");

        // and finishes successfully
        assertThat(processInstance).isEnded();
    }

    @Test
    public void shouldExecuteAlternativePath() {
        // given
        registerMocks();

        // when
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("SHORT_TERM_LOAN_PROCESS",
                Map.of("clientId", "1"));

        // then process is started
        assertThat(processInstance).isStarted();

        // and passes VerificationTask
        assertThat(processInstance).task().hasDefinitionKey("VerificationTask");
        Task task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task, Map.of("isApplicable", false));
        assertThat(processInstance).hasPassed("VerificationTask");

        // and passes TimerEvent
        execute(job("TimerEvent"));
        assertThat(processInstance).hasPassed("TimerEvent");

        // and passes VerificationMessageEvent
        runtimeService().correlateMessage("ADDITIONAL_VERIFICATION_MESSAGE");
        assertThat(processInstance).hasPassed("VerificationMessageEvent");

        // and passes SignalEvent
        runtimeService().signalEventReceived("SIGNAL_EVENT");
        assertThat(processInstance).hasPassed("SignalEvent");

        // and passes UserTask1
        assertThat(processInstance).task().hasDefinitionKey("UserTask1");
        task = taskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        BpmnAwareTests.complete(task);
        assertThat(processInstance).hasPassed("UserTask1");

        // and finishes successfully
        assertThat(processInstance).isEnded();
    }

    private void registerMocks() {
        MockitoAnnotations.openMocks(this);

        DelegateExpressions.registerJavaDelegateMock("previousLoansDelegate")
                .onExecutionSetVariable("loans", List.of(Loan.builder()
                        .id(1)
                        .amount("1")
                        .currency("USD")
                        .amountOfInstallament("1")
                        .clientId(1)
                        .build()));

        ProcessExpressions.registerCallActivityMock("BITCOIN_RATE_PROCESS")
                .onExecutionSetVariables(Variables.putValue("rates", 123.4))
                .deploy(processEngine);
    }

}
