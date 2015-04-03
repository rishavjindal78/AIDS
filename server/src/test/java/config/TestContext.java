package config;

import org.shunya.server.services.TelegramStatusObserver;
import org.shunya.server.services.*;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;

import static org.mockito.Mockito.mock;

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
    public PropertySourcesPlaceholderConfigurer propertySource(){
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocations(new ClassPathResource("aids-test.properties"));
        return configurer;
    }

    @Bean
    public TaskService taskService() {
        return mock(TaskService.class);
    }

    @Bean
    public RestClient agentWorker() {
        return mock(RestClient.class);
    }

    @Bean
    public DBService dbService() {
        return mock(DBService.class);
    }

    @Bean
    public AidsJobScheduler myJobScheduler() {
        return mock(AidsJobScheduler.class);
    }

    @Bean
    public TaskExecutor myExecutor() {
        return mock(TaskExecutor.class);
    }

    @Bean
    public TelegramService telegramService() {
        return mock(TelegramService.class);
    }

    @Bean
    public TelegramStatusObserver telegramStatusObserver() {
        return mock(TelegramStatusObserver.class);
    }

    @Bean
    public AgentStatusService agentStatusService() {
        return mock(AgentStatusService.class);
    }

    @Bean
    public DocumentService documentService() {
        return mock(DocumentService.class);
    }
}