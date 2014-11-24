package org.shunya.server;

import org.junit.Test;
import org.shunya.server.model.Task;
import org.shunya.server.model.TaskStep;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;
import static org.shunya.shared.FieldPropertiesMap.parseStringMap;

public class TaskExecutionPlanTest {
    private TaskExecutionPlan taskExecutionPlan;

    @Test
    public void test1() throws JAXBException {
        Map<String, String> inMap = new HashMap<>();
        inMap.put("name", "Munish Chandel");

        Task taskData = new Task();
//        taskData.setStepDataList(asList(getTaskStep(inMap, 30), getTaskStep(inMap, 10), getTaskStep(inMap, 50), getTaskStep(inMap, 10)));
        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<>(inMap))));
        taskExecutionPlan = new TaskExecutionPlan(taskData);
        Map.Entry<Integer, List<TaskStep>> next = taskExecutionPlan.next();
        while(next !=null){
            System.out.println("Getting next Step");
            List<TaskStep> value = next.getValue();
            value.stream().forEach(tsd -> System.out.println(tsd.getSequence() + " --> " + tsd.getName()));
            next = taskExecutionPlan.next();
        }
    }

    private TaskStep getTaskStepData(Map<String, String> inMap, int sequence) throws JAXBException {
        TaskStep stepData = new TaskStep();
        stepData.setId(10);
        stepData.setSequence(sequence);
//        stepData.setClassName("org.shunya.shared.taskSteps.EchoTaskStep");
        stepData.setName("Echo Munish Chandel");
        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
        Map<String, String> outMap = new HashMap<>();
        outMap.put("outName", "echo");
        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
        return stepData;
    }

}