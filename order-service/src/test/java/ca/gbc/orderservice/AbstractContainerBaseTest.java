package ca.gbc.orderservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:latest");
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url",
                POSTGRES_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username",
                POSTGRES_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password",
                POSTGRES_CONTAINER::getPassword);
    }

}
