package com.example.demoReactiveGw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class ExampleComponent implements InitializingBean {

    public void initMethod(){
        log.info("ExampleComponent initMethod");
    }

    public void postConstruct(){
        log.info("ExampleComponent postConstruct");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("ExampleComponent afterPropertiesSet");
    }
}
