CREATE TABLE PUBLIC.USERS
(
  USERNAME VARCHAR(50) PRIMARY KEY NOT NULL,
  PASSWORD VARCHAR(50) NOT NULL,
  ENABLED BOOLEAN DEFAULT TRUE
);

CREATE TABLE PUBLIC.AUTHORITIES
(
  username varchar_ignorecase (50) NOT NULL,
  authority varchar_ignorecase (50) NOT NULL,
  constraint fk_authorities_users FOREIGN KEY (username) references PUBLIC.USERS(username)
);

CREATE UNIQUE INDEX ix_auth_username ON PUBLIC.AUTHORITIES (username, authority);