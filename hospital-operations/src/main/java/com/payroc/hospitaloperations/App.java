package com.payroc.hospitaloperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.payroc.hospitaloperations.job.OperationQueueJob;
import com.payroc.hospitaloperations.service.OperationService;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        OperationService operationService = context.getBean(OperationService.class);
        new OperationQueueJob(operationService).start();
    }
}