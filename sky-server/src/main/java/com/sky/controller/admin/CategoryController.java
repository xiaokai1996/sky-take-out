package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
@Api("category controller")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @ApiOperation("page query")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("page query. get page param: {}", categoryPageQueryDTO);
        PageResult page = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(page);
    }

    @PostMapping
    @ApiOperation("new a category")
    // if it is a dto, must add a RequestBody
    public Result add(@RequestBody CategoryDTO dto) {
        log.info("add a new category");
        categoryService.add(dto);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("update category")
    // why need RequestBody here?
    public Result update(@RequestBody CategoryDTO dto) {
        log.info("update category dto: {}", dto);
        categoryService.update(dto);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("delete a category")
    public Result delete(Long id) {
        log.info("will delete a category where id={}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("change status of a category")
    // although it is a post interface, but the param is transferred through url
    public Result changeStatus(@PathVariable Integer status, Long id) {
        log.info("change status of id {} -> {}",id, status);
        categoryService.changeStatus(id, status);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("list all the categories by type")
    public Result<List<Category>> getCategoryListByType(Integer type) {
        log.info("query all the categories by type");
        List<Category> categories = categoryService.getCategoryListByType(type);
        return Result.success(categories);
    }

}
