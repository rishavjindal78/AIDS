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
        properties.put("names", "aids, agent, server");

        String replaceAll = tokenReplaceStep.replaceAll("this file contains names", properties);
        System.out.println("replaceAll = " + replaceAll);
    }
}