package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //判断二者是否相同
        queryWrapper.eq(Employee::getUsername, employee.getUsername());

        //getOne() mybatis-plus 中的方法，
        //从数据库中查询结果

        Employee emp = employeeService.getOne(queryWrapper);
        //判断是否查询到结果
        if (emp==null){
            return R.error("登陆失败");
        }
        //比对密码
        if (!emp.getPassword().equals(password)){
            return R.error("登陆失败");
        }
        //查看员工状态 是否禁用
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        //登陆成功将员工id存入session并返回
        request.getSession().setAttribute("employee", emp.getId());
       // session.setAttribute("employee", emp.getId());
        return R.success(emp);

    }

    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }
    //add员工

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        //设置密码，MD5
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

       // Long empid = (Long)request.getSession().getAttribute("employee");
       // employee.setCreateUser(empid);
        //employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增员工成功");

    }
    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Employee> pageInfo=new Page<Employee>(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }
    //更新员工,根据id修改
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //employee.setUpdateTime(LocalDateTime.now());
        //Long empId=(Long)request.getSession().getAttribute("employee");

        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工休息修改成功");

    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if(emp !=null){
            return R.success(emp);
        }
        return R.error("没有查到员工信息");
    }



}
