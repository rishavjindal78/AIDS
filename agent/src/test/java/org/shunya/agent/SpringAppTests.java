package org.shunya.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.shunya.agent.services.RestClient;
import org.shunya.agent.services.TaskProcessor;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskStepDTO;
import org.shunya.shared.TaskStepRunDTO;
import org.shunya.shared.model.RunState;
import org.shunya.shared.model.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:mvc-dispatcher-servlet.xml")
public class SpringAppTests {
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
//        assertEquals(RunStatus.SUCCESS, taskContext.getTaskStepRunDTO().getRunStatus());
    }
}
