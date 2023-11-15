package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Api("employee CRUD")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("this is a health check, congratulations if you see this message!");
    }

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);
        log.info("current thread is: {}", Thread.currentThread());
        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        // Each request has a thread local, login and add are 2 different requests
        // BaseContext.setCurrentId(employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @ApiOperation("add a new employee")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO) {
        log.info("add a new employee: {}", employeeDTO);
        log.info("current thread is: {}", Thread.currentThread());
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @ApiOperation("query page of employee")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO dto) {
        log.info("page query: {}", dto);
        PageResult pageResult = employeeService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("enable/disable an employee")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        log.info("change employee {} status to {}", id, status);
        employeeService.updateStatus(id, status);
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("query an employee by id")
    public Result<Employee> getById(@PathVariable Integer id) {
        log.info("query an employee with id {}", id);
        Employee employee = employeeService.getById(id);
        employee.setPassword("****");       // hide the password not be seen directly
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation("update an employee")
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("update an employee");
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }


    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PutMapping("/editPassword")
    public Result<String> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        // the password is transferred in plain text ???!!!
        log.info("edit password, {} -> {}", passwordEditDTO.getOldPassword(), passwordEditDTO.getNewPassword());
        employeeService.updateEmployeePassword(passwordEditDTO);
        return Result.success();
    }

}
