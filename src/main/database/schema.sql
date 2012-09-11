create table cloudsuite.deployment (
  id bigint not null,
  awsKey varchar(255),
  customerId varchar(255),
  password varchar(255),
  url varchar(255),
  username varchar(255),
  product_id bigint,
  user_id bigint,
  primary key (id),
  foreign key FKEA6C96E59CEA6F43 (product_id) references product(id),
  foreign key FKEA6C96E544663291 (user_id) references user(id)
);

create table cloudsuite.deployment_seq (
  next_val bigint
);

create table cloudsuite.product (
  id bigint not null,
  createdAt datetime,
  name varchar(255) not null,
  shortName varchar(255) not null,
  updatedAt datetime,
  primary key (id)
);
create unique index shortName on cloudsuite.product (shortName);
create unique index name on cloudsuite.product (name);

create table cloudsuite.product_seq (
  next_val bigint
);

create table cloudsuite.trialenvironment (
  id bigint not null,
  password varchar(255),
  url varchar(255),
  username varchar(255),
  product_id bigint,
  primary key (id),
  foreign key FKA86BE53D9CEA6F43 (product_id) references product(id)
);

create table cloudsuite.trialenvironment_seq (
  next_val bigint
);

create table cloudsuite.trialinstance (
  id bigint not null,
  expirationDate datetime,
  password varchar(255),
  url varchar(255),
  username varchar(255),
  product_id bigint,
  user_id bigint,
  primary key (id),
  foreign key FK38AC1AB9CEA6F43 (product_id) references product(id),
  foreign key FK38AC1AB44663291 (user_id) references user(id)
);

create table cloudsuite.trialinstance_seq (
  next_val bigint
);

create table cloudsuite.user (
  id bigint not null,
  active bit,
  awsAccountNumber varchar(255),
  awsKey varchar(255),
  awsSecretKey varchar(255),
  companyName varchar(255),
  createdAt datetime not null,
  firstName varchar(255),
  inforAcctNumnber varchar(255),
  language int,
  lastName varchar(255),
  loginAttempts int,
  password varchar(255),
  updatedAt datetime,
  username varchar(255),
  primary key (id)
);
create unique index username on cloudsuite.user (username);

create table cloudsuite.user_seq (
  next_val bigint
);

create table cloudsuite.userproduct (
  launchAvailable bit,
  trialAvailable bit,
  product_id bigint not null default 0,
  user_id bigint not null default 0,
  deployment_id bigint,
  trialInstance_id bigint,
  primary key (product_id, user_id),
  foreign key FK3EF95684318F7311 (deployment_id) references deployment(id),
  foreign key FK3EF9568444663291 (user_id) references user(id),
  foreign key FK3EF956849CEA6F43 (product_id) references product(id),
  foreign key FK3EF95684B7ECF723 (trialInstance_id) references trialinstance(id)
);

create table cloudsuite.validation (
  id bigint not null,
  company varchar(255),
  createDate datetime not null,
  email varchar(255) not null,
  firstName varchar(255),
  lastName varchar(255),
  type varchar(255),
  validationKey varchar(255) not null,
  primary key (id)
);
create unique index email on cloudsuite.validation (email, type);

create table cloudsuite.validation_seq (
  next_val bigint
);

