CREATE TABLE request_cache (
    request_cache_id INT PRIMARY KEY AUTO_INCREMENT,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    response CLOB,
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE operation (
    operation_id INT PRIMARY KEY AUTO_INCREMENT,
    operation_type VARCHAR(50) NOT NULL,
    operation_data CLOB,
    operation_status VARCHAR(20) NOT NULL,
    date_submitted TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_queued TIMESTAMP,
    date_processed TIMESTAMP,
    metadata CLOB,
    request_cache_id INT,
    FOREIGN KEY (request_cache_id) REFERENCES request_cache(request_cache_id),

    CHECK (operation_type IN ('DISCHARGE_PATIENT', 'UNDO_DISCHARGE_PATIENT')),
    CHECK (operation_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);
