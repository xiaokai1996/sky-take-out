package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;


public interface CategoryService {
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void update(CategoryDTO dto);

    void add(CategoryDTO dto);

    void deleteById(Long id);

    void changeStatus(Long id, Integer status);

    List<Category> getCategoryListByType(Integer type);

    Category getById(Long categoryId);
}
