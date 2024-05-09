package com.example.demoReactiveGw;

import com.netflix.discovery.EurekaNamespace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.cloud.config.server.EnableConfigServer;
//import org.springframework.context.annotation.ComponentScan;

//@ComponentScan("com.example.demoReactiveGw")
@SpringBootApplication
@SpringBootConfiguration
@EnableDiscoveryClient
@EnableWebFlux
//@EnableConfigServer
public class DemoReactiveGwApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoReactiveGwApplication.class, args);
	}

}
