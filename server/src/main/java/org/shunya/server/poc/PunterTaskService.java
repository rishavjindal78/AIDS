package org.shunya.server.poc;

import org.reflections.Reflections;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.model.TaskData;
import org.shunya.shared.model.TaskStepData;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.shunya.shared.FieldPropertiesMap.convertObjectToXml;
import static org.shunya.shared.FieldPropertiesMap.parseStringMap;

@Service
public class PunterTaskService {
    public static void main(String[] args) throws JAXBException {
        TaskStepData stepData = new TaskStepData();
        stepData.setId(10);
//        stepData.setClassName("org.shunya.shared.taskSteps.EchoTaskStep");
        stepData.setName("Echo Munish Chandel");
        Map<String, String> inMap = new HashMap<>();
        inMap.put("name", "Munish Chandel");
        stepData.setInputParams(convertObjectToXml(parseStringMap(inMap)));
        Map<String, String> outMap = new HashMap<>();
        outMap.put("outName", "echo");
        stepData.setOutputParams(convertObjectToXml(parseStringMap(outMap)));
        TaskData taskData = new TaskData();
//        taskData.setStepDataList(asList(stepData));
//        taskData.setInputParams(convertObjectToXml(parseStringMap(new HashMap<>(inMap))));
    }

    @Scheduled(cron = "*/5 0 * * * ?")
    public void demoServiceMethod() {
        System.out.println("Method executed at every 5 seconds. Current time is :: " + new Date());
    }

    @PostConstruct
    public void initializeDbTasks(){
        Reflections reflections = new Reflections("org.shunya.shared.taskSteps");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(PunterTask.class);

        for (Class<?> tasks : annotated) {
            PunterTask request = tasks.getAnnotation(PunterTask.class);
            String mapping = request.name();
            System.out.println("TaskStep = " + mapping);
        }
    }
}
