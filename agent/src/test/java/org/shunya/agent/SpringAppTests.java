package org.shunya.agent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.shunya.agent.services.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:mvc-dispatcher-servlet.xml")
public class SpringAppTests {
    @Autowired
    private RestClient restClient;

    @Test
    public void testSayHello() {
//        Assert.assertEquals("Hello world!", agentService.ping());
    }
}
