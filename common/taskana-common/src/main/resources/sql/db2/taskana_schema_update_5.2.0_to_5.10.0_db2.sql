-- this script updates the TASKANA database schema from version 5.2.0 to version 5.10.0.
SET SCHEMA %schemaName%;

CREATE SEQUENCE TASKANA_SCHEMA_VERSION_ID_SEQ
    MINVALUE 1
    START WITH 100
    INCREMENT BY 1 CACHE 10;

ALTER TABLE TASKANA_SCHEMA_VERSION ALTER COLUMN ID DROP IDENTITY;

-- The VERSION value must be equal or higher then the value of TaskanaEngineImpl.MINIMAL_TASKANA_SCHEMA_VERSION
INSERT INTO TASKANA_SCHEMA_VERSION (ID, VERSION, CREATED)
VALUES (TASKANA_SCHEMA_VERSION_ID_SEQ.NEXTVAL,  '5.10.0', CURRENT_TIMESTAMP);

CREATE INDEX IDX_OBJECT_REFERE_PK_ID ON OBJECT_REFERENCE
    (ID ASC) ALLOW REVERSE SCANS COLLECT SAMPLED DETAILED STATISTICS;
COMMIT WORK ;
CREATE INDEX IDX_OBJECT_REFERE_FK_TASK_ID ON OBJECT_REFERENCE
    (TASK_ID ASC) ALLOW REVERSE SCANS COLLECT SAMPLED DETAILED STATISTICS;
COMMIT WORK ;
CREATE INDEX IDX_OBJECT_REFERE_ACCESS_LIST ON OBJECT_REFERENCE
    (VALUE ASC, TYPE ASC, SYSTEM_INSTANCE ASC, SYSTEM ASC, COMPANY ASC, ID ASC)
    ALLOW REVERSE SCANS COLLECT SAMPLED DETAILED STATISTICS;
COMMIT WORK ;
