package com.library.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.library.system.entity.Book;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM book")
    List<Book> findAll();

    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findById(Integer id);

    @Select("SELECT * FROM book WHERE title LIKE CONCAT('%', #{title}, '%')")
    List<Book> findByTitle(String title);

    @Select("<script>" +
            "SELECT * FROM book WHERE 1=1" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='author != null and author != \"\"'> AND author LIKE CONCAT('%', #{author}, '%')</if>" +
            "<if test='isbn != null and isbn != \"\"'> AND isbn LIKE CONCAT('%', #{isbn}, '%')</if>" +
            "</script>")
    List<Book> search(@Param("title") String title, @Param("author") String author, @Param("isbn") String isbn);

    @Select("SELECT * FROM book WHERE available > 0")
    List<Book> findAvailable();

    @Update("UPDATE book SET available = available - 1 WHERE id = #{id} AND available > 0")
    int decreaseAvailable(Integer id);

    @Update("UPDATE book SET available = available + 1 WHERE id = #{id}")
    int increaseAvailable(Integer id);

    @Insert("INSERT INTO book (title, author, isbn, publisher, quantity, available) VALUES (#{title}, #{author}, #{isbn}, #{publisher}, #{quantity}, #{available})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Book book);

    @Update("UPDATE book SET title = #{title}, author = #{author}, isbn = #{isbn}, publisher = #{publisher}, quantity = #{quantity}, available = #{available} WHERE id = #{id}")
    void update(Book book);

    @Delete("DELETE FROM book WHERE id = #{id}")
    void delete(Integer id);
}