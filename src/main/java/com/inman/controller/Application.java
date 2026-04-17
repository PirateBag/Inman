package com.inman.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
@Configuration
@EnableConfigurationProperties
@ComponentScan( "com.inman")
@EnableJpaRepositories( {"com.inman.repository" } )
@EntityScan( basePackages = { "com.inman" } )
public class Application {
	public static  boolean isPrepared = false;
    public static String testName;
	
    public static void main( String[] args ) {
        	SpringApplication.run( Application.class, args);
    }
    
    public synchronized static void setIsPrepared( boolean value ) {
    	isPrepared = value;
    }
    public synchronized static boolean isPrepared(  ) {
    	return isPrepared;
    }

    public static void setTestName(String currentTest) {
        testName = currentTest;
    }
    public static String getTestName() {
        return testName;
    }
    public static boolean isTestName( String theTestInProgress )
    {
        return testName.equals( theTestInProgress );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void executeBashScriptAfterStartup() {
        String workingDirectory = "src/test/curl";
        String scriptName = "regressionApiDriver.sh";
        System.out.println("[INFO] Application started. Executing script: " + scriptName + " in directory: " + workingDirectory);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", scriptName);
            processBuilder.directory(new File(workingDirectory));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[SCRIPT OUTPUT] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("[INFO] Script executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("[ERROR] Failed to execute script: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

	
