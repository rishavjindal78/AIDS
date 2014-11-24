package org.shunya.server;

import org.junit.Test;
import org.shunya.shared.TaskContext;
import org.shunya.server.model.Agent;
import org.shunya.server.model.Task;
import org.shunya.server.model.TaskStep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;
import static org.shunya.shared.FieldPropertiesMap.parseStringMap;

public class AgentControllerTest1 {
    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testExecute() throws Exception {
        TaskStep stepData = new TaskStep();
        stepData.setId(10);
//        stepData.setClassName("org.shunya.shared.taskSteps.EchoTaskStep");
        stepData.setName("Echo Munish Chandel");
        Map<String, String> inMap = new HashMap<>();
        inMap.put("name", "Munish Chandel");
        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
        Map<String, String> outMap = new HashMap<>();
        outMap.put("outName", "echo");
        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
        Task taskData = new Task();
//        taskData.setStepDataList(asList(stepData));
        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<>(inMap))));

        Agent agent = new Agent();
        agent.setName("Agent-1");
        TaskContext taskContext = new TaskContext();
//        taskConfig.setId(100);
//        taskConfig.setTask(taskData);
//        taskConfig.setAgent(agent);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:9290/rest/agent/execute", taskContext, String.class);
        String body = responseEntity.getBody();
        System.out.println("body = " + body);
    }

    @Test
    public void testDiscSpaceExecute() throws Exception {
        TaskStep stepData = new TaskStep();
        stepData.setId(10);
//        stepData.setClassName("org.shunya.shared.taskSteps.DiscSpaceTaskStep");
        stepData.setName("Get Disc Space");
        Map<String, String> inMap = new HashMap<>();
        inMap.put("drive", "c:/");
        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
        Map<String, String> outMap = new HashMap<>();
        outMap.put("percentFree", "percentFree");
        outMap.put("totalSpace", "totalSpace");
        outMap.put("freeSpace", "freeSpace");
        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
        Task taskData = new Task();
//        taskData.setStepDataList(asList(stepData));
        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<>(inMap))));

        Agent agent = new Agent();
        agent.setName("Agent-1");
        TaskContext taskContext = new TaskContext();
//        taskConfig.setId(100);
//        taskConfig.setTask(taskData);
//        taskConfig.setAgent(agent);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:9290/rest/agent/execute", taskContext, String.class);
        String body = responseEntity.getBody();
        System.out.println("body = " + body);
    }
}
