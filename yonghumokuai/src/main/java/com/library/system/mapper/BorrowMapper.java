package com.library.system.mapper;

import com.library.system.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface BorrowMapper {
    @Select("SELECT * FROM borrow_record")
    List<BorrowRecord> findAll();

    @Select("SELECT * FROM borrow_record WHERE id = #{id}")
    BorrowRecord findById(Integer id);

    @Select("SELECT * FROM borrow_record WHERE user_id = #{userId}")
    List<BorrowRecord> findByUserId(Integer userId);

    @Insert("INSERT INTO borrow_record (user_id, book_id, borrow_date, return_date, status) VALUES (#{userId}, #{bookId}, #{borrowDate}, #{returnDate}, #{status})")
    void insert(BorrowRecord record);

    @Update("UPDATE borrow_record SET return_date = #{returnDate}, status = #{status} WHERE id = #{id}")
    void update(BorrowRecord record);

    @Delete("DELETE FROM borrow_record WHERE id = #{id}")
    void delete(Integer id);
}
