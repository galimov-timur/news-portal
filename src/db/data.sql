DROP DATABASE IF EXISTS newsportal;

CREATE DATABASE newsportal
    LC_COLLATE 'ru_RU.utf8' LC_CTYPE 'ru_RU.utf8'
    TEMPLATE template0;

\c newsportal;

DROP TABLE IF EXISTS news;

CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL NOT NULL,
    title VARCHAR(500) NOT NULL,
    createdDetails TIMESTAMPTZ NOT NULL,
    brief VARCHAR(1000) NOT NULL,
    content VARCHAR(5000) NOT NULL,
    CONSTRAINT pk_news PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
   id BIGSERIAL NOT NULL,
   email VARCHAR(128) NOT NULL,
   password VARCHAR(256) NOT NULL,
   userName VARCHAR(128) NOT NULL,
   isAccountNonExpired BOOLEAN NOT NULL,
   isAccountNonLocked BOOLEAN NOT NULL,
   isCredentialsNonExpired BOOLEAN NOT NULL,
   isEnabled BOOLEAN NOT NULL,
   CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL NOT NULL,
    name VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

INSERT INTO users(email, password, userName, isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled) VALUES('test@test.ru', 'test', 'TestUserName', true, true, true, true);