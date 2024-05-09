package com.example.demoReactiveGw.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ExampleBean {

    private List<PropertyData> list;

    @Getter
    @Setter
    static class PropertyData {

        private String name;

        private String value;
    }
}