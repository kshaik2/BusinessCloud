use businesscloud;

##BACKUP
CREATE TABLE DeploymentStack_BAK AS SELECT * FROM DeploymentStack;
CREATE TABLE DeploymentStackLog_BAK AS SELECT * FROM DeploymentStackLog;

######
##DeploymentStack
#####
##functions to move int enum records and save them to temporary columns
##and to remove the old columns, and re-create them as strings.

ALTER TABLE DeploymentStack ADD COLUMN stateEnumInt INT;
ALTER TABLE DeploymentStack ADD COLUMN statusEnumInt INT;
UPDATE DeploymentStack SET stateEnumInt=deploymentState, statusEnumInt=deploymentStatus;
ALTER TABLE DeploymentStack DROP COLUMN deploymentState;
ALTER TABLE DeploymentStack DROP COLUMN deploymentStatus;
ALTER TABLE DeploymentStack ADD COLUMN deploymentState VARCHAR(50);
ALTER TABLE DeploymentStack ADD COLUMN deploymentStatus VARCHAR(50);

#functions to set values in string columns
#states (set strings and transform)
UPDATE DeploymentStack SET deploymentState='UNKNOWN' WHERE stateEnumInt = 13; 
UPDATE DeploymentStack SET deploymentState='AVAILABLE' WHERE stateEnumInt = 3;
UPDATE DeploymentStack SET deploymentState='NOT_AVAILABLE' WHERE stateEnumInt IN (0,1,2,4,5,6,7,8,9,10,11);
UPDATE DeploymentStack SET deploymentState='DELETED' WHERE stateEnumInt=12;

#statuses (set strings)
UPDATE DeploymentStack SET deploymentStatus='DEPLOY_INITIATED' WHERE statusEnumInt=0;
UPDATE DeploymentStack SET deploymentStatus='DEPLOYING' WHERE statusEnumInt=1;
UPDATE DeploymentStack SET deploymentStatus='DEPLOYING_INSTANCES_CREATED' WHERE statusEnumInt=2;
UPDATE DeploymentStack SET deploymentStatus='DEPLOYING_INSTANCES_STARTED' WHERE statusEnumInt=3;
UPDATE DeploymentStack SET deploymentStatus='DEPLOYED' WHERE statusEnumInt=4;
UPDATE DeploymentStack SET deploymentStatus='TERMINATING' WHERE statusEnumInt=5;
UPDATE DeploymentStack SET deploymentStatus='TERMINATED' WHERE statusEnumInt=6;
UPDATE DeploymentStack SET deploymentStatus='UNKNOWN' WHERE statusEnumInt=7;
UPDATE DeploymentStack SET deploymentStatus='ROLLING_BACK' WHERE statusEnumInt=8;
UPDATE DeploymentStack SET deploymentStatus='ROLLED_BACK' WHERE statusEnumInt=9;
UPDATE DeploymentStack SET deploymentStatus='ROLLBACK_FAILED' WHERE statusEnumInt=10;
UPDATE DeploymentStack SET deploymentStatus='DEPLOYED_RDPDOWN' WHERE statusEnumInt=11;

#functions to remove temporary columns
ALTER TABLE DeploymentStack DROP COLUMN stateEnumInt;
ALTER TABLE DeploymentStack DROP COLUMN statusEnumInt;

#####
##DeploymentStackLog
#####
ALTER TABLE DeploymentStackLog ADD COLUMN stateEnumInt INT;
ALTER TABLE DeploymentStackLog ADD COLUMN statusEnumInt INT;
UPDATE DeploymentStackLog SET stateEnumInt=state, statusEnumInt=status;
ALTER TABLE DeploymentStackLog DROP COLUMN state;
ALTER TABLE DeploymentStackLog DROP COLUMN status;
ALTER TABLE DeploymentStackLog ADD COLUMN state VARCHAR(50);
ALTER TABLE DeploymentStackLog ADD COLUMN status VARCHAR(50);

#functions to set values in string columns
#states (set strings and transform)
UPDATE DeploymentStackLog dsl SET dsl.state='UNKNOWN' WHERE dsl.stateEnumInt =13; 
UPDATE DeploymentStackLog dsl SET dsl.state='AVAILABLE' WHERE dsl.stateEnumInt = 3;
UPDATE DeploymentStackLog dsl SET dsl.state='NOT_AVAILABLE' WHERE dsl.stateEnumInt IN (0,1,2,4,5,6,7,8,9,10,11);
UPDATE DeploymentStackLog dsl SET dsl.state='DELETED' WHERE dsl.stateEnumInt=12;

#statuses (set strings)
UPDATE DeploymentStackLog SET status='DEPLOY_INITIATED' WHERE statusEnumInt=0;
UPDATE DeploymentStackLog SET status='DEPLOYING' WHERE statusEnumInt=1;
UPDATE DeploymentStackLog SET status='DEPLOYING_INSTANCES_CREATED' WHERE statusEnumInt=2;
UPDATE DeploymentStackLog SET status='DEPLOYING_INSTANCES_STARTED' WHERE statusEnumInt=3;
UPDATE DeploymentStackLog SET status='DEPLOYED' WHERE statusEnumInt=4;
UPDATE DeploymentStackLog SET status='TERMINATING' WHERE statusEnumInt=5;
UPDATE DeploymentStackLog SET status='TERMINATED' WHERE statusEnumInt=6;
UPDATE DeploymentStackLog SET status='UNKNOWN' WHERE statusEnumInt=7;
UPDATE DeploymentStackLog SET status='ROLLING_BACK' WHERE statusEnumInt=8;
UPDATE DeploymentStackLog SET status='ROLLED_BACK' WHERE statusEnumInt=9;
UPDATE DeploymentStackLog SET status='ROLLBACK_FAILED' WHERE statusEnumInt=10;
UPDATE DeploymentStackLog SET status='DEPLOYED_RDPDOWN' WHERE statusEnumInt=11;

#functions to remove temporary columns
ALTER TABLE DeploymentStackLog DROP COLUMN stateEnumInt;
ALTER TABLE DeploymentStackLog DROP COLUMN statusEnumInt;