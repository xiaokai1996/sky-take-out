package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select * from setmeal_dish where dish_id=#{id}")
    // 如果有多个怎么办?
    List<SetmealDish> getByDishId(Long id);

    // 直接这么写是不能return Long的,需要再xml中手动制定
    // @Select("select id from setmeal_dish where dish_id in #{ids}")
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
