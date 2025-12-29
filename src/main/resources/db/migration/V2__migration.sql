ALTER TABLE user_roles
DROP
CONSTRAINT fk_user_roles_on_user;

CREATE TABLE user_permissions
(
    user_pk     BIGINT NOT NULL,
    permissions SMALLINT
);

ALTER TABLE user_permissions
    ADD CONSTRAINT fk_user_permissions_on_user FOREIGN KEY (user_pk) REFERENCES users (pk);

DROP TABLE user_roles CASCADE;