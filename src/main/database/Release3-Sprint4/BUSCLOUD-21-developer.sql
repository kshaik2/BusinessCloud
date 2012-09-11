use businesscloud;
ALTER TABLE businesscloud.Customer DROP FOREIGN KEY FK27FBE3FE9A8DE231 ;
ALTER TABLE businesscloud.User DROP FOREIGN KEY FK285FEB4F9D2231;
ALTER TABLE businesscloud.User DROP COLUMN customer_id ;
DROP TABLE IF EXISTS businesscloud.Customer ;
UPDATE businesscloud.Company_SEQ set next_val = (SELECT max(next_val) from businesscloud.Customer_SEQ);
DROP TABLE IF EXISTS businesscloud.Customer_SEQ;
