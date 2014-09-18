package org.shunya.shared;

import org.junit.Test;
import org.shunya.shared.model.TaskData;
import org.shunya.shared.model.TaskStepData;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;
import static org.shunya.shared.FieldPropertiesMap.parseStringMap;

public class TaskStepTest {
    @Test
    public void testExecute() throws Exception {
        TaskStepData stepData = new TaskStepData();
        stepData.setId(10);
        stepData.setClassName("org.shunya.shared.taskSteps.EchoTaskStep");
        stepData.setName("Echo Munish Chandel");
        Map<String, String> inMap = new HashMap<>();
        inMap.put("name", "Munish Chandel");
        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
        Map<String, String> outMap = new HashMap<>();
        outMap.put("outName", "echo");
        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
//        TaskData taskData = new TaskData();
//        taskData.setStepDataList(asList(stepData));
//        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<String, String>(inMap))));

//        assertThat(execute, is(Boolean.TRUE));
    }
}
