use businesscloud_dev;

insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (3, 'us-west-1', 'USW1', 'USWEST1', 'ec2.us-west-1.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (4, 'us-east-1', 'USE1', 'USEAST1', 'ec2.us-east-1.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (5, 'us-west-2' ,'USW2', 'USWEST2', 'ec2.us-west-2.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (6, 'sa-east-1' ,'SAE1', 'SAEAST2', 'ec2.sa-east-1.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (7, 'eu-west-1' ,'EUW1', 'EUWEST1', 'ec2.eu-west-1.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (8, 'ap-southeast-1' ,'APSE1', 'APSOUTHE1', 'ec2.ap-southeast-1.amazonaws.com', 'AWS');
insert into Region (id, name, shortName, cloudAlias, endPoint, regionType) values (9, 'ap-northeast-1' ,'APNE1', 'APNORTHE1', 'ec2.ap-northeast-1.amazonaws.com', 'AWS');
update Region reg set reg.regionType = 'INFOR24' where reg.id in (1,2);

update ProductVersion pv set pv.region_id = 4;
update AmiDescriptor ami set ami.region_id = 4;
update DeploymentStack ds set ds.region_id = 4;
