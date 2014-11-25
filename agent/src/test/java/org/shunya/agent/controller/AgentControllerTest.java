package org.shunya.agent.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shunya.agent.services.TaskProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class AgentControllerTest {
    @Mock
    TaskProcessor taskProcessor;

    @InjectMocks
    AgentController controllerUnderTest;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(controllerUnderTest).build();
    }

    @Test
    public void testPing() throws Exception {
        this.mockMvc.perform(get("/agent/ping")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testGetMemoryLogs() throws Exception {
        when(taskProcessor.getMemoryLogs(anyLong(), anyLong())).thenReturn("test memory logs");
        this.mockMvc.perform(get("/agent/getMemoryLogs/100").param("start", "0"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("test memory logs"));
    }

    /* @Test
    public void simple() throws Exception {
        standaloneSetup(new AgentController()).build()
                .perform(get("/agent/ping"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(content().string("Hello world!"));
    }*/
}