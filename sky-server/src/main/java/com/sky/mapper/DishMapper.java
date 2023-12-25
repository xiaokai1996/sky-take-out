package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    // 如果只是单纯的插入,是无法获取到主键Id的,需要在sql中特殊定制
//    @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
//            "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value= OperationType.INSERT)
    void insert(Dish dish);

    @Select("select * from dish where name=#{name}")
    Dish getByName(String name);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);


    // @Delete("delete from dish where id in #{dishIds}")
    void deleteBatchByDishIds(List<Long> dishIds);

    // 空缺的字段可以自动填充
    @AutoFill(value=OperationType.UPDATE)
    void update(Dish dish);

    @Update("update dish set status=#{status} where id=#{id}")
    void setStatus(Long id, Integer status);
}
