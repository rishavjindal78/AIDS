package org.shunya.server;

import config.TestContext;
import config.WebAppContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.shunya.shared.TaskContext;
import org.shunya.shared.TaskState;
import org.shunya.shared.model.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.logging.Logger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebAppContext.class, TestContext.class})
//@ContextConfiguration("classpath:WEB-INF/mvc-dispatcher-servlet.xml")
//@TransactionConfiguration(defaultRollback = true, transactionManager = "hibernatetransactionManager")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class AgentControllerTests {
    Logger logger = Logger.getLogger(AgentControllerTests.class.getName());

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected TaskService taskServiceMock;

    @Before
    public void setup() {
        Mockito.reset(taskServiceMock);
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void pingTest() throws Exception {
        mockMvc.perform(get("/agent/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void updateTaskStateTest() throws Exception {
        mockMvc.perform(post("/agent/updateTaskState/100")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(TaskState.COMPLETE)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testExecute() throws Exception {
        Agent agent = new Agent();
        agent.setName("Agent-1");
        TaskContext taskContext = new TaskContext();
//        taskConfig.setId(100);
//        taskConfig.setTaskData(new TaskData());
//        taskConfig.setAgent(agent);

        mockMvc.perform(post("/agent/execute")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(taskContext)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
