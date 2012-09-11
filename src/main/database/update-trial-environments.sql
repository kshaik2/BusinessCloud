USE cloudsuite2;
/*
SELECT * INTO OUTFILE 'TrialEnvironment-PRE.txt' FIELDS TERMINATED BY '|'  LINES TERMINATED BY '\r\n' FROM TrialEnvironment LIMIT 0;
SELECT * INTO OUTFILE 'TrialInstance-PRE.txt'  FIELDS TERMINATED BY '\|'  LINES TERMINATED BY '\\r\\n'  FROM TrialInstance LIMIT 0;
SELECT * INTO OUTFILE 'ExpiredTrialInstances-PRE.txt'  FIELDS TERMINATED BY '|'  LINES TERMINATED BY '\r\n' FROM TrialInstance ti JOIN User u ON ti.user_id=u.id WHERE ti.expirationDate < CURDATE() LIMIT 0;
*/
/*create temporary table to hold updates*/
CREATE TEMPORARY TABLE IF NOT EXISTS TempTrialEnv ENGINE=MEMORY SELECT environmentId, url, username, password FROM TrialEnvironment LIMIT 0;

/*populate temporary table*/
LOAD DATA LOCAL INFILE 'c:/DATA/THE-LOAD-FILE.txt' REPLACE
INTO TABLE TempTrialEnv
FIELDS TERMINATED BY '|'
(environmentId,url,username,password) ;

/*update values in TrialEnvironment table from temp table*/
UPDATE TrialEnvironment te,TempTrialEnv temp SET te.url=temp.url, te.username=temp.username, te.password=temp.password WHERE te.environmentId=temp.environmentId;

/*update values in TrialInstance table from temp table*/
UPDATE TrialInstance ti, TempTrialEnv temp SET ti.url=temp.url, ti.username=temp.username, ti.password=temp.password WHERE ti.environmentId=temp.environmentId;

/*cleanup*/
DROP TEMPORARY TABLE  TempTrialEnv;

/*export data post update*/

/*
SELECT * INTO OUTFILE 'C:/DATA/TrialEnvironment-POST.txt' FIELDS TERMINATED BY '|'  LINES TERMINATED BY '\r\n' FROM TrialEnvironment LIMIT 0;
SELECT * INTO OUTFILE 'C:/DATA/TrialInstance-POST.txt'  FIELDS TERMINATED BY '|'  LINES TERMINATED BY '\r\n'  FROM TrialInstance LIMIT 0;
SELECT * INTO OUTFILE 'C:/DATA/ExpiredTrialInstances-POST.txt'  FIELDS TERMINATED BY '|'  LINES TERMINATED BY '\r\n' FROM TrialInstance ti JOIN User u ON ti.user_id=u.id WHERE ti.expirationDate < CURDATE() LIMIT 0;
*/