use businesscloud_dev;
insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL001_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL01_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL002_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL02_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL003_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL03_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL004_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL04_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL005_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL05_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL006_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL06_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';
 
insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL007_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL07_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';
 
insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL008_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL08_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL009_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL09_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

insert into TrialEnvironment  (id,available,environmentId,`password`, url, username, productVersion_id, region_id) select max(te.id)+1,true, 'HMS_TRIAL010_DEM', 'HMSr5U$ER', 'https://hms25.saas.infor.com/hmslogin.jsp?tenant=DC4TRIAL10_TRN', 'HMSUSER', pv.id, 1 FROM TrialEnvironment te, ProductVersion pv JOIN Product p ON pv.product_id=p.id AND p.shortName='Infor10 SoftBrands HMS';

UPDATE TrialEnvironment_SEQ set next_val=(select max(id)+100 from TrialEnvironment);

