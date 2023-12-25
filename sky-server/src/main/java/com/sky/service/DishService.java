package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void addDishWithFlavor(DishDTO dishDTO);

    DishVO getByIdWithFlavor(Long id);

    void deleteByIds(List<Long> ids);

    void updateDish(DishDTO dishDTO);

    void setStatus(Long id, Integer status);
}
