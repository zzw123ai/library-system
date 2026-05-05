package com.library.system.mapper;

import com.library.system.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM book")
    List<Book> findAll();

    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findById(Integer id);

    @Select("SELECT * FROM book WHERE title LIKE CONCAT('%', #{title}, '%')")
    List<Book> findByTitle(String title);

    @Insert("INSERT INTO book (title, author, isbn, publisher, quantity, available) VALUES (#{title}, #{author}, #{isbn}, #{publisher}, #{quantity}, #{quantity})")
    void insert(Book book);

    @Update("UPDATE book SET title = #{title}, author = #{author}, isbn = #{isbn}, publisher = #{publisher}, quantity = #{quantity}, available = #{available} WHERE id = #{id}")
    void update(Book book);

    @Delete("DELETE FROM book WHERE id = #{id}")
    void delete(Integer id);
}