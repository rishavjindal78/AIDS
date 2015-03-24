package org.shunya.shared.taskSteps;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class TokenReplaceStepTest {

    @Test
    public void testReplaceAll() throws Exception {
        TokenReplaceStep tokenReplaceStep = new TokenReplaceStep();
        Properties properties = new Properties();
        properties.put("file", "c:/abc.tx");
        properties.put("remote:localhost", "remote:endevtdm05");
        properties.put("localhost:8080", "127.0.0.1:9090");

        String replaceAll = tokenReplaceStep.replaceAll("orient.db.url = remote:localhost/df", properties);
        System.out.println("replaceAll String = " + replaceAll);

        replaceAll = tokenReplaceStep.replaceAll("db.url = http://localhost:8080/mf", properties);
        System.out.println("replaceAll String = " + replaceAll);
    }
}