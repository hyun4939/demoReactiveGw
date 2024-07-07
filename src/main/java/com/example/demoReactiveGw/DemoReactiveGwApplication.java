package com.example.demoReactiveGw;

import com.netflix.discovery.EurekaNamespace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.cloud.config.server.EnableConfigServer;
//import org.springframework.context.annotation.ComponentScan;

//@ComponentScan("com.example.demoReactiveGw")
@SpringBootApplication
@SpringBootConfiguration
@EnableDiscoveryClient
@EnableWebFlux
@Slf4j
@ComponentScan(basePackages = "com.example.demoReactiveGw")
//@EnableConfigServer
public class DemoReactiveGwApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(DemoReactiveGwApplication.class, args);
		}catch (Throwable t){
			log.error("",t);
			throw t;
		}
	}

}
