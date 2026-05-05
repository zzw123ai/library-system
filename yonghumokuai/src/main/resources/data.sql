-- 初始数据（H2）

INSERT INTO "user" (username, password, role) VALUES
('admin', 'admin123', '1'),
('user1', 'user123', '0'),
('user2', 'user123', '0');

INSERT INTO book (title, author, isbn, publisher, quantity, available) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', 5, 5),
('深入理解计算机系统', 'Randal E. Bryant', '9787111407010', '机械工业出版社', 3, 3),
('算法导论', 'Thomas H. Cormen', '9787111407010', '机械工业出版社', 4, 4),
('数据结构与算法分析', 'Mark Allen Weiss', '9787302233305', '清华大学出版社', 2, 2),
('计算机网络', 'Andrew S. Tanenbaum', '9787111427442', '机械工业出版社', 3, 3);
