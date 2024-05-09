package com.example.demoReactiveGw.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.UUID;

@Slf4j
@ToString
public class DynamicBean implements InitializingBean {

    @Getter
    private final String id = UUID.randomUUID().toString().replace("-", "").substring(16);

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String value;

    @Override
    public void afterPropertiesSet() {
        log.info("DynamicBean afterPropertiesSet, name - {}, id - {}", name, id);
    }
}