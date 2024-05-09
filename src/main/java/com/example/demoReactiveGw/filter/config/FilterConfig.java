package com.example.demoReactiveGw.filter.config;


import lombok.Getter;
import lombok.Setter;

public class FilterConfig {
    @Setter
    @Getter
    boolean preLogger;

    @Setter
    @Getter
    boolean postLogger;

    @Setter
    @Getter
    String baseMessage;
}
