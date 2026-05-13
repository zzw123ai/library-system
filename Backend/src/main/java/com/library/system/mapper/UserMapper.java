package com.library.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.library.system.entity.User;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户（用于登录）
    @Select("SELECT * FROM `user` WHERE username = #{username} LIMIT 1")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM `user`")
    List<User> findAll();

    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    @Select("SELECT * FROM `user` WHERE role = #{role}")
    List<User> findByRole(@Param("role") String role);

    @Insert("INSERT INTO `user`(username,password,role) VALUES(#{username},#{password},#{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE `user` SET username=#{username}, password=#{password}, role=#{role} WHERE id=#{id}")
    void update(User user);

    @Delete("DELETE FROM `user` WHERE id = #{id}")
    void delete(@Param("id") Integer id);

    @Select("<script>" +
            "SELECT * FROM `user` WHERE 1=1" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='role != null and role != \"\"'> AND role = #{role}</if>" +
            "</script>")
    List<User> search(@Param("username") String username, @Param("role") String role);
}