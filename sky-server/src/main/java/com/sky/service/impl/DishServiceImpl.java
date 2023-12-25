package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Integer status = dishPageQueryDTO.getStatus();
        log.info("status={}, judge={}", status, status != null);
        Page<DishVO> dishPage = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishPage.getTotal(), dishPage.getResult());
    }

    @Override
    public void addDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(dish.getId());
            dishFlavorService.add(dishFlavor);
        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long dishId) {
        // 这里其实可以直接多表join查询
        // mybatis的好处就是,如果你会写sql,非常容易写出高性能的sql
        Dish dish = dishMapper.getById(dishId);
        // 但是这里有个复杂的封装,flavor是一个列表,这超出了sql的范畴,所以使用java来封装
        // 这时候返回的值不需要categoryName,简单封装即可,数据传输应该遵循最小冗余原则
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(dishId);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Override
    public void deleteByIds(List<Long> dishIds) {
        // 1. 套餐中的不能删除
        // 2. 已经起售的不能删除
        // 3. 如果删除,口味要一起删掉

        for (Long id : dishIds) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            // 这里其实没有必要再查出entity,直接查出id就可以直接报错了,而且不要放在循环中
//            List<SetmealDish> setmealDishes = setmealDishMapper.getByDishId(dish.getId());
//            if (setmealDishes != null) {
//                log.error("cannot delete because already in setmeals");
//                continue;
//            }
//
//            // normal delete, 这样子删除有可能是会报错的
//            dishFlavorService.deleteByDishId(dish.getId());
        }

        // 这也是一个批量查询操作,因为返回值比较特殊,所以要放到xml里面去书写
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
        // 第一个判断是防止setmealIds本身是空的,造成NPE错误,第二个才是去判断内容
        if (setmealIds != null && !setmealIds.isEmpty()) {
            // 一旦出现错误,可以直接报错然后整个退出程序,并且报错的信息是一个已经定义好的常量
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 这两个如果要同时批量删除的话,就得去xml里面写foreach版本的in函数
        dishMapper.deleteBatchByDishIds(dishIds);
        dishFlavorMapper.deleteBatchByDishIds(dishIds);
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        // 更新原有的dish,然后删除原来的flavor,更新现在的flavor
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach((dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
                dishFlavorMapper.add(dishFlavor);
            }));
        }

    }

    @Override
    public void setStatus(Long id, Integer status) {
        dishMapper.setStatus(id, status);
    }

//    private DishVO toDishVO(Dish dish) {
//        DishVO dishVO = new DishVO();
//        BeanUtils.copyProperties(dish, dishVO);
//        Category category = categoryService.getById(dish.getCategoryId());
//        dishVO.setCategoryName(category.getName());
//        List<DishFlavor> dishFlavors = dishFlavorService.getFlavorByDishId(dish.getId());
//        dishVO.setFlavors(dishFlavors);
//        return dishVO;
//    }


}
