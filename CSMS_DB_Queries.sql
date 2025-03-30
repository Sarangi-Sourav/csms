show databases;
create database CSMS; -- creating a new database for the Charging Station Management System (CSMS) service
use CSMS;
show tables;
-- We need a table for authentication of the charging point when they want to establish a websocket connection with the CSMS
CREATE TABLE charge_point_cred (
    id INT NOT NULL auto_increment PRIMARY KEY,     
    charger_id VARCHAR(50) UNIQUE NOT NULL,
    pass_key VARCHAR(255) NOT NULL,     -- Stores the hashed key 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
select * from charger;
drop table charge_point_cred;
-- We also need a table for Charge Point details and it's current status
-- Can have a trigger for creating the record of a new charger once it is added to the cred table for the first time
-- on the first connection in TLS env 
-- can update the table by adding new fields in future
CREATE TABLE charger (
    id INT NOT NULL auto_increment PRIMARY KEY,  
    charger_id VARCHAR(50) UNIQUE NOT NULL, 
    status ENUM('Available',
                'Preparing',
                'Charging',
                'SuspendedEVSE',
                'SuspendedEV',
                'Finishing',
                'Reserved',
                'Unavailable',
                'Faulted') NOT NULL,
    last_heartbeat TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table for logging the charging Transactions
-- for now w eare string the entire charging_profile as JSON but it should be normalized with proper tables in future
CREATE TABLE charge_transaction (
    id INT NOT NULL auto_increment PRIMARY KEY,
    id_tag VARCHAR(20) NOT NULL,
    charger_id VARCHAR(50) NOT NULL, -- need this as per the charge_id 
    connector_id INT,
    charging_profile JSON, -- Stores the entire nested chargingProfile object
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
select * from charge_transaction;
truncate table charger;
drop table charge_transaction;

-- manually inserted the charger cred for testing
INSERT INTO charge_point_cred (charger_id, pass_key) 
VALUES ('CHARIdG26', '$2a$10$sL/nnsl5uDIuSTepBFT.G.JX1U3bZMjefYJIyTAxAHoRs6Enz.el6');

INSERT INTO charge_point_cred (charger_id, pass_key) 
VALUES ('CHARIdG25', '$2a$10$QlHaApKLhD9xqh0txG886u0K.ZZfgjMNB.A4wdYEGV4aDSLQLFtgC');

select pass_key from charge_point_cred where charger_id = 'CHARIdG25';

-- manually inserting the charger in the charger table for testing
INSERT INTO charger (charger_id, status, last_heartbeat) 
VALUES ('CHARIdG25', 'Available', NOW());

