package org.shunya.server.services;

import org.shunya.server.Processor;
import org.shunya.server.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ScheduledProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledProcessor.class.getName());

    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    @Qualifier("asyncWorker")
    private Worker worker;

    @Scheduled(fixedDelay = 500 *1000)
    public void process() {
        logger.debug("processing next 10 at " + new Date());
        for (int i = 0; i < 5; i++) {
            worker.work(counter.incrementAndGet());
        }
    }
}