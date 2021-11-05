create database if not exists servicepoller;
use servicepoller;
CREATE TABLE if not exists servicepoller.url (
    id INT(10) NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    path VARCHAR(100) NOT NULL,
    creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id) USING BTREE,
    UNIQUE INDEX path (path) USING BTREE
    );
INSERT IGNORE INTO url(name, path) VALUES ('My First URL', '/my/first/url');
INSERT IGNORE INTO url(name, path) VALUES ('My Second URL', '/my/second/url');
INSERT IGNORE INTO url(name, path) VALUES ('My Third URL', '/my/third/url');