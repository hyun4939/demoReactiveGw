package com.example.demoReactiveGw.filter.config;


import com.example.demoReactiveGw.filter.CustomFilter;
import com.example.demoReactiveGw.filter.CustomGlobalFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig2 {
    @Bean
    public GlobalFilter customGlobalFilter(){
       return new CustomGlobalFilter();
   }


}
