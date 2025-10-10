package com.payroc.hospitaloperations.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.enumeration.OperationStatusEnum;
import com.payroc.hospitaloperations.service.OperationService;
import com.payroc.hospitaloperations.service.RequestCacheService;

public class OperationQueueJob {
	private static final Logger logger = LoggerFactory.getLogger(OperationQueueJob.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final OperationService operationService;

    public OperationQueueJob(OperationService operationService) {
        this.operationService = operationService;
    }

    public void start() {
    	resetInProgressOperations();
    	logger.info("Starting operation queue job");
        scheduler.scheduleAtFixedRate(this::processNextOperation, 0, 60, TimeUnit.SECONDS);
    }
    
    private void resetInProgressOperations() 
    {
    	List<Operation> inProgressOperations = 
    			operationService.getOperationsWithStatus( OperationStatusEnum.PROCESSING );
    	
    	if ( !inProgressOperations.isEmpty() )
    	{
    		logger.info(String.format("Resetting %s orphaned operations", inProgressOperations.size() ));
    		
    		for ( Operation op : inProgressOperations )
    		{
    			op.setOperationStatus(OperationStatusEnum.PENDING);
    			operationService.updateOperation(op);
    		}
    	}
    }

    private void processNextOperation() {
    	logger.info("Looking for pending operations");
    	// Fetch oldest PENDING operation from DB
    	Operation oldestPending = operationService.getOldestOperationWithStatus( OperationStatusEnum.PENDING );
    	if ( oldestPending == null )
    	{
    		logger.info("No operations to process.");
    		return;
    	}
    	logger.info("Processing operation " + oldestPending.toString() );
    	processOperation( oldestPending );
    }
    
    private void processOperation(Operation operation) {
        try {
            // Set operation to IN_PROGRESS
            operation.setOperationStatus(OperationStatusEnum.PROCESSING);
            operationService.updateOperation(operation);
    
            // Start transaction and perform discharge logic
            boolean success = operationService.performOperation(operation);
    
            if (success) {
                operation.setOperationStatus(OperationStatusEnum.COMPLETED);
                operation.setDateProcessed(new Date());
                logger.info("Operation processed successfully: " + operation.getOperationId());
            } else {
                operation.setOperationStatus(OperationStatusEnum.FAILED);
                operation.setDateProcessed(new Date());
                logger.error("Operation failed: " + operation.getOperationId());
            }
            operationService.updateOperation(operation);
        } catch (Exception ex) {
            // Final error handling
            operation.setOperationStatus(OperationStatusEnum.FAILED);
            operation.setMetadata(ex.getMessage());
            operationService.updateOperation(operation);
            logger.error("Exception processing operation " + operation.getOperationId(), ex);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}