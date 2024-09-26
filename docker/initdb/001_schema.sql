CREATE USER dbUser WITH PASSWORD 'dbPwd';
CREATE DATABASE userDB;
GRANT ALL PRIVILEGES ON DATABASE userDB TO dbUser;

CREATE SEQUENCE user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE project_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE tb_user
(
    id BIGINT NOT NULL DEFAULT nextval('user_seq') PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(129) NOT NULL,
    name VARCHAR(120),
    version BIGINT NOT NULL DEFAULT 1,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    CONSTRAINT uq_user_email UNIQUE (email)
);



CREATE TABLE tb_user_external_project
(
    id BIGINT NOT NULL DEFAULT nextval('project_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    modified_date DATE,
    created_by VARCHAR(255),
    modified_by VARCHAR(255),
    CONSTRAINT uq_external_project_name UNIQUE (name),
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES tb_user(id)
);


