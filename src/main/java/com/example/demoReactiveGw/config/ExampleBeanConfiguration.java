package com.example.demoReactiveGw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class ExampleBeanConfiguration {
    @Bean(initMethod = "initMethod")
    public ExampleComponent exampleComponent(){
        return new ExampleComponent();
    }

    @Bean
    @ConfigurationProperties("example-bean")
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }

}
