# PayrocAudition - Hospital operations server
## Key notes
Take home audition for role at Payroc
I've created my solution with SpringBoot as a basis however I have tried to avoid using SpringBoot _magic_ as per request where I have been able.
## Server setup
The server port is 8080, this can be changed in `application.properties` if needed.

A file based H2 database is used for simple persistence, the dashboard can be accessed while the server is running via:  
`localhost:[server-port]/h2-console`  
    
JDBC url: `jdbc:h2:file:./data/hospitaldb`  
User: `sa`  

Flyway is used for database migrations, with migration files in:  
`hospital-operations\src\main\resources\db\migration`  
Tables are created on server initialisation.  
The database file can be safely deleted if a fresh database is required.  
The database is autopopulated with Patients:
```
[
    {
        "id": 1,
        "name": "Steve",
        "status": "ADMITTED"
    },
    {
        "id": 2,
        "name": "Ana",
        "status": "ADMITTED"
    },
    {
        "id": 3,
        "name": "Jim",
        "status": "ADMITTED"
    },
    {
        "id": 4,
        "name": "David",
        "status": "ADMITTED"
    },
    {
        "id": 5,
        "name": "Sarah",
        "status": "ADMITTED"
    }
]
```

### Running the server
Use JDK 17 or higher and maven:  
Navigate to `\hospital-operations`  
`mvn clean package`  
Followed by  
`mvn spring-boot:run`  
or  
`java -jar target/hospital-operations-0.0.1-SNAPSHOT.jar`  

## Rest endpoints
### GET
**Get all patients**  
localhost:8080/patients  
Returns all stored patients  
`curl --location 'http://localhost:8080/patients'`  

**Get patient by id**  
localhost:8080/patients/:patientId   
Returns patient with given id, or error if none exists  
`curl --location 'http://localhost:8080/patients/2'`  
### POST
Post requests must be sent with the header x-idempotency-key  
This header ensures that if a request is sent multiple times, it will only create one operation.  
On repeated requests with the same key, the client will receive the same response as the original request, but no additional operations will be created.  

**Discharge patient** 
localhost:8080/patients/:patientId/discharge  
Discharges a patient.  
```
curl --location --request POST 'http://localhost:8080/patients/2/discharge' \  
--header 'x-idempotency-key: abcde-12345'
```  

**Undo discharge patient**  
localhost:8080/patients/:patientId/discharge/undo  
Un-discharges a patient.  
```
curl --location --request POST 'http://localhost:8080/patients/2/discharge/undo' \  
--header 'x-idempotency-key: fghij-67890'
```

## Logic flow
### Operation queue job
Repeating job that attempts operations on a schedule
Currently the queue attempts to process one operation every 60 seconds.
This timing can be adjusted in the file `OperationQueueJob.java`, Line 31.
Potentially add logic to ensure the job will not attempt to process multiple operations simultaneously depending on available threading.  
  
Operations are performed exactly once, even if requested multiple times, or on server crash mid-operation.  
Operations can fail, eg. If a queued operation aims to Discharge an already Discharged patient - this is validated when the operation is processed.  
If an operation fails due to an error, that error is stored in the meta data of the operation.  


### Cleanup method - deals with operations in odd states (eg. after crash)
On server start, or on a timed interval, check for any operations that are 'orphaned', or in odd states.
With this server, on server restart, before initiating job we simply look for operations that are 'PROCESSING', and reset them to pending. 

### Operation queue logic
Pick oldest operation, or a batch of oldest operations in queue (for scalability you would remove or take ownership from a shared queue)
Set operation PROCESSING  
Start roll-back safe transaction  
Try to perform operation to discharge patient  
Set operation SUCCESSFUL  
Catch error - retry (depending on error)  
Catch error final - Set operation FAILURE (_Not implemented but could be implemented without much additional work_)   
Store error in operation queue meta column  

## Error handling
Depending on errors handled in the system, return custom error codes via a convenient enum with helpful info.  
Return these custom errors to frontend.  
Example error:  
```
{
    "code": 4001,
    "message": "Patient already discharged"
}
```
  
This makes troubleshooting issues easier when a handled exception is returned, improves visibility of errors, and avoids sending error data to the frontend.  
Frontend can implement handlers to react differently depending on error returned, also can help with internationalisation of errors down the line if required.  

## Decision explanations
### Schema design
Status columns are defined as enums rather than FK to patient_status lookup table for simplicity.  
Some workarounds have been used in order to work with H2 limitations and time constraints  
Operation Data is stored on operation as a Json string, with CLOB column type  
The data is mapped to and from DTOs on the server.  
With further time separate tables and logic would be created and foreign keyed eg. operation data column.  
### Idempotency logic
With further time - Checking for idempotency and storing responses would be further abstracted away to simplify future development.  
Similarly, code duplication exists when handling the discharge and undo-discharge requests, much of this should be simplified / abstracted away.  
### Operation queue
In a properly scalable system the queue would exist in a full message/queue system eg. Kafka, available to all instances of the server, each instance takes ownership of an operation from the queue to process without blocking each other.  
For simplicity the queue here exists within the server database.  
Currently the queue processes a single operation at a time, this can be easily adjusted to work in batches, however for right now it is kept at one operation at a time for easier testing of edge cases.  
### Examples / tests

You will find in the repository a postman request collection for the endpoints described above.  
`HospitalOperations.postman_collection.json`  

Otherwise, the curl for the requests are alongside those endpoint definitions.  

**Automated testing**  
I didn't manage to find the time to build a suite of automated tests in this instance, or work with TDD, however given more time with the system, tests would be extremely useful.  


## Further suggestions
### Offline app implementation  
When a user attempts to submit eg. a discharge but their device is offline:  
Make it clear to the user if needed that this action is submitted (locally) but not ‘synchronised’ with the server.  
Store operation locally on device (eg. localstorage, idb) with the idempotency key, and any required data to submit operation, as well as date of initial ‘submission’ (when the user hit the submit button) for auditing.  
When app reconnects to server, submit operations generated while offline    
Potentially warn user (in prominent location) about unsynchronised operations  
As any operation is stored unsynchronised  
Or based on how many operations are unsynchronised  
Or based on how old the oldest unsynchronised operation is  
Or based on the priority of the operations stored  

The operation table currently already includes the column date_submitted however for now that is just assumed as the moment the request was made.  
### Scalability
Store idempotency log in an accessible cache like redis rather than a local database to ensure the operation can be checked across instances.  
Ensure instances can take ownership of operations immediately (ie. 2 instances cannot claim ownership at the same instant)    

Idempotency cache request log probably wants to be cleared after a set amount of time.  
For example, a job can be run that scans cached requests created before a certain date, and deletes them if not needed for auditing purposes.  
Potentially check whether the operation has completed/failed before removal, and flag old requests whose operations are in an unexpected state.

### Operation in progress indication  
If an operation is time-sensitive (eg. a patient discharge), we could add a front-end indication to the user that it is ‘in-progress’ rather than completed as soon as they submit the operation.  

The user has hit the discharge button, the operation has been added to the queue, the discharge is successfully submitted, but not complete yet, potentially we could add logic to allow polling the operation, and improve the UX for the user performing the discharge.  

Essentially, rather than telling them that the discharge is a success because it has been queued, in case the queue has a backlog, add an endpoint that allows the front-end to poll the status of the operation, and while the operation is not successfully complete, display an ‘in progress’ indicator.
Also prevent other interaction eg. resubmit discharge, unless that interaction is to potentially submit an undo of this operation.  
### Logging
Important operations / exceptions should be output at a minimum to an accessible log file to make it easier to spot/follow up on actions taken / errors encountered by the server.  
