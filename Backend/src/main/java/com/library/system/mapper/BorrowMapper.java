package com.library.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.library.system.entity.BorrowRecord;

@Mapper
public interface BorrowMapper {
    @Select("SELECT * FROM borrow_record")
    List<BorrowRecord> findAll();

    @Select("SELECT * FROM borrow_record WHERE id = #{id}")
    BorrowRecord findById(Integer id);

    @Select("SELECT * FROM borrow_record WHERE user_id = #{userId}")
    List<BorrowRecord> findByUserId(Integer userId);

    @Insert("INSERT INTO borrow_record (user_id, book_id, borrow_date, return_date, status) VALUES (#{userId}, #{bookId}, #{borrowDate}, #{returnDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(BorrowRecord record);

    @Update("UPDATE borrow_record SET return_date = #{returnDate}, status = #{status} WHERE id = #{id}")
    void update(BorrowRecord record);

    @Delete("DELETE FROM borrow_record WHERE id = #{id}")
    void delete(Integer id);
}
