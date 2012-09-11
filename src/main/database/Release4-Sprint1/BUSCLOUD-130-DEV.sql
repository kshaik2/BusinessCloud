use businesscloud_dev;

##BACKUP
DROP TABLE IF EXISTS DeploymentStackLog_BAK;
CREATE TABLE DeploymentStackLog_BAK AS SELECT * FROM DeploymentStackLog;

#####
##DeploymentStackLog
#####
ALTER TABLE DeploymentStackLog ADD COLUMN logActionInt INT;
UPDATE DeploymentStackLog SET logActionInt=logAction;

ALTER TABLE DeploymentStackLog DROP COLUMN logAction;
ALTER TABLE DeploymentStackLog ADD COLUMN logAction VARCHAR(50);

#functions to set values in string columns
#logActions

UPDATE DeploymentStackLog dsl SET dsl.logAction= ='CHANGE_IN_STATE' WHERE dsl.stateEnumInt =0;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='CHANGE_IN_STATUS' WHERE dsl.stateEnumInt =1;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='CHANGE_IN_STATE_STATUS' WHERE dsl.stateEnumInt =2;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='MESSAGE' WHERE dsl.stateEnumInt =3;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='UPDATE_ELASTIC_IP' WHERE dsl.stateEnumInt =4;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='SET_VPC_ID' WHERE dsl.stateEnumInt =5;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='CHANGE_IN_INSTANCE_STATUS' WHERE dsl.stateEnumInt =6;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='JANITOR_DISCOVERED' WHERE dsl.stateEnumInt =7;
UPDATE DeploymentStackLog dsl SET dsl.logAction= ='JANITOR_UPDATE' WHERE dsl.stateEnumInt =8;

#functions to remove temporary columns
ALTER TABLE DeploymentStackLog DROP COLUMN logActionInt;
