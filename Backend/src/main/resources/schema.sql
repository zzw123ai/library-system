-- 图书表
CREATE TABLE IF NOT EXISTS book (
    book_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    book_name VARCHAR(100) NOT NULL COMMENT '书名',
    book_author VARCHAR(50) NOT NULL COMMENT '作者',
    book_publisher VARCHAR(100) NOT NULL COMMENT '出版社',
    publish_date DATE NOT NULL COMMENT '出版日期',
    stock INT NOT NULL COMMENT '库存',
    CONSTRAINT chk_stock CHECK (stock >= 0)
) ENGINE=InnoDB COMMENT '图书信息表';

-- 读者表
CREATE TABLE IF NOT EXISTS reader (
    reader_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '读者ID',
    reader_name VARCHAR(50) NOT NULL COMMENT '姓名',
    sex ENUM('男','女') NOT NULL COMMENT '性别',
    age INT NOT NULL COMMENT '年龄',
    tel VARCHAR(20) NOT NULL COMMENT '手机号',
    reg_date DATE NOT NULL COMMENT '注册日期',
    CONSTRAINT uk_tel UNIQUE (tel)
) ENGINE=InnoDB COMMENT '读者信息表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow (
    borrow_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '借阅ID',
    book_id INT NOT NULL COMMENT '图书ID',
    reader_id INT NOT NULL COMMENT '读者ID',
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
    return_date DATETIME NULL COMMENT '归还日期',
    status ENUM('借出','已归还') DEFAULT '借出' COMMENT '状态',
    FOREIGN KEY (book_id) REFERENCES book(book_id),
    FOREIGN KEY (reader_id) REFERENCES reader(reader_id)
) ENGINE=InnoDB COMMENT '借阅记录表';