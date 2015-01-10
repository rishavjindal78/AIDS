package org.shunya.shared;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractStepTest {
    @Test
    public void testExecute() throws Exception {
//        TaskStepData stepData = new TaskStepData();
//        stepData.setId(10);
////        stepData.setClassName("org.shunya.shared.taskSteps.EchoTaskStep");
//        stepData.setName("Echo Munish Chandel");
//        Map<String, String> inMap = new HashMap<>();
//        inMap.put("name", "Munish Chandel");
//        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
//        Map<String, String> outMap = new HashMap<>();
//        outMap.put("outName", "echo");
//        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
////        TaskData taskData = new TaskData();
////        taskData.setStepDataList(asList(stepData));
////        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<String, String>(inMap))));
//
////        assertThat(execute, is(Boolean.TRUE));
    }

    @Test
    public void testVariable() throws Exception {
        List<String> variablesFromString = AbstractStep.getVariablesFromString("munish copy #{munish} #{path}");
        for (String s : variablesFromString) {
            System.out.println("var = " + s);
        }

        List<String> variablesFromTemplate = AbstractStep.getVariablesFromTemplate("munish copy #{munish} #{path} tdm_home = #env{TDM_HOME}");
        variablesFromTemplate.forEach(s -> System.out.println(s));

        Map<String, String> varibales = new HashMap<>();
        varibales.put("path", "<Test Path :>");
        varibales.put("munish", "<Munish Chandel>");
        varibales.put("TDM_HOME", "<TDM Home>");

        String substituteVariables = AbstractStep.substituteVariables("munish copy #{munish} #{path} tdm_home = #env{TDM_HOME}", varibales, "\\#\\{(.+?)\\}");
        System.out.println("substituteVariables = " + substituteVariables);
    }
}
