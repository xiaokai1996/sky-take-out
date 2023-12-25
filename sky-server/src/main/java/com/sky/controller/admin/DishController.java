package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("dish controller")
@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    DishService dishService;



    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("dish page query: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping()
    // 这里得＋requestParam,为什么?
    public Result<String> deleteByIds(@RequestParam List<Long> ids) {
        log.info("delete by ids: {}", ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }

    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO) {
        log.info("add a new dish {}", dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id) {
        log.info("get dish by id {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("update dish")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("update dish: {}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }

//    /admin/dish/status/{status}
    @ApiOperation("修改商品起售/停售状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(@RequestParam Long id, @PathVariable Integer status) {
        dishService.setStatus(id, status);
        return Result.success();
    }
}
