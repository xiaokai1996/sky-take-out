package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Api("设置店铺状态")
@Slf4j
@RestController
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    // 这种用法就和spring的controller-service-mapper这样的framework很类似
    // 普通的数据走csm(controller-service-mapper)这种方式,spring通过注解去识别
    // 相同点:都需要导入相应的驱动坐标,连接信息,都需要加上注解
    // csm需要保证csm命名一致性,3个相关注解
    // redis需要额外配置到spring-config中,并且要设置为Bean属性
    @Autowired
    RedisTemplate redisTemplate;

    // /admin/shop/{status}
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status) {
        // 原来用的是service然后走到mapper最后到数据库,现在直接走redis的template
        // shopService.setStatus();
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus() {
        // opsForValue是operation for value的意思
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        return Result.success(status);
    }

}
