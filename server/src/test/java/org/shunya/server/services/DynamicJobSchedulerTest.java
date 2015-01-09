package org.shunya.server.services;

import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicJobSchedulerTest {

    @Test
    public void testPredict() throws Exception {
        DynamicJobScheduler jobScheduler = new DynamicJobScheduler();
        jobScheduler.predict("5 * * * * ?", 5);
        jobScheduler.predict("5 5 */10 * * MON-FRI", 5);
        jobScheduler.predict("* 15 9-17 * * MON-FRI", 5);
    }
}