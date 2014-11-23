package config;

import org.mockito.Mockito;
import org.shunya.server.services.TaskService;
import org.shunya.server.services.DBService;
import org.shunya.server.services.RestClient;
import org.shunya.server.services.MyJobScheduler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class TestContext {
    private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    @Bean
    public TaskService taskService() {
        return Mockito.mock(TaskService.class);
    }

    @Bean
    public RestClient agentWorker() {
        return Mockito.mock(RestClient.class);
    }

    @Bean
    public DBService agentService() {
        return Mockito.mock(DBService.class);
    }

    @Bean
    public MyJobScheduler myJobScheduler() {
        return Mockito.mock(MyJobScheduler.class);
    }

    @Bean
    public TaskExecutor myExecutor() {
        return Mockito.mock(TaskExecutor.class);
    }
}