use businesscloud;
DROP TABLE IF EXISTS DeploymentStack_Product;
DROP TABLE IF EXISTS TrialInstance_SEQ;
DROP TABLE IF EXISTS TrialRequestContact;
DROP TABLE IF EXISTS Product_AmiDescriptor;
DROP TABLE IF EXISTS Product_Ami;
DROP TABLE IF EXISTS TrialRequest_Product;
DROP TABLE IF EXISTS AwsProductAmiDesc_SEQ;

ALTER TABLE TrialEnvironment DROP FOREIGN KEY FKA86BE53D9CEA6F43;
ALTER TABLE TrialEnvironment DROP COLUMN product_id;

ALTER TABLE TrialInstance DROP FOREIGN KEY FK38AC1AB9CEA6F43;
ALTER TABLE TrialInstance DROP COLUMN product_id;

ALTER TABLE Product DROP COLUMN accessKey;
ALTER TABLE Product DROP COLUMN secretKey;
ALTER TABLE Product DROP COLUMN templateName;

ALTER TABLE TrialProductChild DROP FOREIGN KEY FK3EB4A7839757BBF6;
ALTER TABLE TrialProductChild DROP FOREIGN KEY FK3EB4A783AFD455A8;
ALTER TABLE TrialProductChild DROP COLUMN child_id;
ALTER TABLE TrialProductChild DROP COLUMN parent_id;