-- this script updates the TASKANA database schema from version 7.0.0 to version 7.2.0.

SET search_path = %schemaName%;

INSERT INTO TASKANA_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (nextval('TASKANA_SCHEMA_VERSION_ID_SEQ'), '7.2.0', CURRENT_TIMESTAMP);

ALTER TABLE TASK
    ADD COLUMN LOCK_EXPIRE TIMESTAMP NULL;
