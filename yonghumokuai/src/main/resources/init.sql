-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE library_system;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL -- 0 表示普通读者，1 表示管理员
);

-- 创建图书表
CREATE TABLE IF NOT EXISTS book (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    available INT NOT NULL
);

-- 插入初始数据
INSERT INTO user (username, password, role) VALUES
('admin', 'admin123', '1'),
('user1', 'user123', '0'),
('user2', 'user123', '0');

INSERT INTO book (title, author, isbn, publisher, quantity, available) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', 5, 5),
('深入理解计算机系统', 'Randal E. Bryant', '9787111407010', '机械工业出版社', 3, 3),
('算法导论', 'Thomas H. Cormen', '9787111407010', '机械工业出版社', 4, 4),
('数据结构与算法分析', 'Mark Allen Weiss', '9787302233305', '清华大学出版社', 2, 2),
('计算机网络', 'Andrew S. Tanenbaum', '9787111427442', '机械工业出版社', 3, 3);