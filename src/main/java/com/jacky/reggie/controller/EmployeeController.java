package com.jacky.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jacky.reggie.common.R;
import com.jacky.reggie.entity.Employee;
import com.jacky.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
//    @Resource
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1. 将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if(null == emp) {
            return R.error("登录失败Login failed");
        }

        if(!emp.getPassword().equals(password)) {
            return R.error("登录失败Login failed（password）");
        }

        if(0 == emp.getStatus()) {
            return R.error("账号已禁用Account inactive");
        }

        // 登录成功，将员工id存入session
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功Quit successfully");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工Add employee：{}", employee.toString());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 使用自动填充方式:MyMetaObjectHandler
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //try catch 可实现，但是推荐全局捕获异常方式
        /*try {
            employeeService.save(employee);
        } catch (Exception e) {
            R.error("新增员工失败Add employee failed.")
        }*/
        employeeService.save(employee);

        return R.success("新增员工成功Add employee successfully");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    //http://localhost:8080/employee/page?page=1&pageSize=10&name=asd
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={}, pageSize={}, name={}", page, pageSize, name);
        //构造枫叶构造器
        Page pageInfo = new Page(page, pageSize);

        //如果有name，构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据员工id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("employ: {}", employee.toString());

        // 使用自动填充方式:MyMetaObjectHandler
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}", id);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);

        return R.success("员工信息更新成功");
    }

    // http://localhost:8080/employee/1582595176137723906
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息。。。");
        Employee employee = employeeService.getById(id);
        if(employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
