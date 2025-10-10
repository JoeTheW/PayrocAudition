CREATE TABLE patients (
    id INT PRIMARY KEY NOT NULL auto_increment,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_admitted DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_discharged DATETIME,

    CHECK (status IN ('ADMITTED', 'DISCHARGED'))
);