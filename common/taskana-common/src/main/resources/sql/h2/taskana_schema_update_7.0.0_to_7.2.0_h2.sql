-- this script updates the TASKANA database schema from version 7.0.0 to version 7.2.0.
SET SCHEMA %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (nextval('TASKANA_SCHEMA_VERSION_ID_SEQ'), '7.2.0', CURRENT_TIMESTAMP);

ALTER TABLE TASK
    ADD (
    `LOCK_EXPIRE` TIMESTAMP NULL
    )
