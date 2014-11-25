CREATE TABLE if not exists PUBLIC.USERS
(
  USERNAME VARCHAR(50) PRIMARY KEY NOT NULL,
  PASSWORD VARCHAR(50) NOT NULL,
  ENABLED BOOLEAN DEFAULT TRUE
);

CREATE TABLE if not exists PUBLIC.AUTHORITIES
(
  username varchar_ignorecase (50) NOT NULL,
  authority varchar_ignorecase (50) NOT NULL,
  constraint fk_authorities_users FOREIGN KEY (username) references PUBLIC.USERS(username)
);

CREATE UNIQUE INDEX ix_auth_username ON PUBLIC.AUTHORITIES (username, authority);

create table if not exists persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)