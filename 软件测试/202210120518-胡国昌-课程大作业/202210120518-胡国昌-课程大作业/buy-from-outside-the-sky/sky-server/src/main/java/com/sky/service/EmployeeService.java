package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.math.BigInteger;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void insert(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return PageResult
     */
    PageResult limit(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据id修改员工状态
     * @param status
     * @param id
     */
    void updateByid(Integer status, long id);

    /**
     * 根据员工id查询员工数据
     * @param id
     * @return Employee
     */
    Employee findById(Integer id);

    void updateEmp(EmployeeDTO employeeDTO);
}

