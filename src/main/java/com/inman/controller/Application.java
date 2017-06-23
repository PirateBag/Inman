package com.inman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan( "com.inman")
@EnableJpaRepositories( {"com.inman.repository" } )
@EntityScan( basePackages = { "com.inman" } )
public class Application {
	
	public static  boolean isPrepared = false;
	
    public static void main( String[] args ) {
    	
        	SpringApplication.run( Application.class, args);
    }
    
    public synchronized static void setIsPrepared( boolean value ) {
    	isPrepared = value;
    }
    public synchronized static boolean isPrepared(  ) {
    	return isPrepared;
    }
}

	
