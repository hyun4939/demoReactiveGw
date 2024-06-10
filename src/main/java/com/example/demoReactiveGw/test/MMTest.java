package com.example.demoReactiveGw.test;

import reactor.core.publisher.Mono;

public class MMTest {
    public static void main(String[] args ){
        var test = "";
        var result = Mono.just("ttt rcv");
        result.subscribe(System.out::println);

    }
}
