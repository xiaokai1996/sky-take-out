package com.sky.service;

import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService {
    List<DishFlavor> getFlavorByDishId(Long id);

    void add(DishFlavor dishFlavor);

    void deleteByDishId(Long id);
}
