package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);

    @Insert("insert into dish_flavor (dish_id, name, value) values (#{dishId}, #{name}, #{value})")
    void add(DishFlavor dishFlavor);

    @Delete("delete from dish_flavor where dish_id=#{id}")
    void deleteByDishId(Long id);

    // in method 只能出现在复杂的sql语法中,否则会出现对象解析失败的错误
    // @Delete("delete from dish_flavor where dish_id in #{dishIds}")
    void deleteBatchByDishIds(List<Long> dishIds);
}
