-- H2 兼容的建表脚本（来自 init.sql，移除 CREATE DATABASE / USE）

CREATE TABLE IF NOT EXISTS "user" (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS book (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    available INT NOT NULL
);

CREATE TABLE IF NOT EXISTS borrow_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP DEFAULT NULL,
    status VARCHAR(20) NOT NULL
);
