package org.shunya.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.shunya.agent.services.RestClient;
import org.shunya.agent.services.TaskProcessor;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.TaskStepRunDTO;
import org.shunya.shared.model.RunState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringAppTests {
    @Configuration
    static class SpringAppTestsTestContextConfiguration {
        @Bean
        public TaskProcessor taskProcessor() {
            return new TaskProcessor();
        }

        @Bean
        public RestClient restClient() {
            return Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        }

        @Bean
        public TaskExecutor taskExecutor() {
            return new ConcurrentTaskExecutor();
        }
    }

    @Autowired
    private RestClient restClient;

    @Autowired
    private TaskProcessor taskProcessor;

    @Test
    public void testSayHello() throws InterruptedException {
        TaskContext taskContext = new TaskContext();
        TaskStepDTO taskStepDTO = new TaskStepDTO();
        taskStepDTO.setTaskClass("org.shunya.shared.taskSteps.EchoStep");
        taskStepDTO.setDescription("Description");
        taskStepDTO.setName("Name");
        taskContext.setStepDTO(taskStepDTO);
        TaskStepRunDTO taskStepRunDTO = new TaskStepRunDTO();
        taskStepRunDTO.setId(100);
        taskContext.setTaskStepRunDTO(taskStepRunDTO);
        taskContext.setSessionMap(new HashMap<>());
        taskProcessor.executeTask(taskContext);
        Thread.sleep(500);
        assertEquals(RunState.COMPLETED, taskContext.getTaskStepRunDTO().getRunState());
    }
}
