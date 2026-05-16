-- 初始数据

INSERT IGNORE INTO `user` (username, password, role) VALUES
('admin', 'password', '1'),
('user1', 'user123', '0'),
('user2', 'user123', '0');

INSERT INTO book (title, author, isbn, publisher, quantity, available) VALUES
('Java Programming', 'Bruce Eckel', '9787111213826', 'Machinery Industry Press', 5, 5),
('Computer Systems', 'Randal E. Bryant', '9787111407010', 'Machinery Industry Press', 3, 3),
('Introduction to Algorithms', 'Thomas H. Cormen', '9787111407011', 'Machinery Industry Press', 4, 4),
('Data Structures', 'Mark Allen Weiss', '9787302233305', 'Tsinghua University Press', 2, 2),
('Computer Networks', 'Andrew S. Tanenbaum', '9787111427442', 'Machinery Industry Press', 3, 3);
