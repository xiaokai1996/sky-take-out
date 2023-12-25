package com.sky.service.impl;

import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl implements DishFlavorService {

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Override
    public List<DishFlavor> getFlavorByDishId(Long id) {
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        return dishFlavors;
    }

    @Override
    public void add(DishFlavor dishFlavor) {
        dishFlavorMapper.add(dishFlavor);
    }

    @Override
    public void deleteByDishId(Long id) {
        dishFlavorMapper.deleteByDishId(id);
    }
}
