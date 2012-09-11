use businesscloud;
UPDATE businesscloud.Company_SEQ set next_val = (SELECT max(next_val) from businesscloud.Customer_SEQ);
DROP TABLE IF EXISTS businesscloud.Customer_SEQ;