package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api(tags = "员工控制层接口")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
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


    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("用户退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.insert(employeeDTO);
        log.info("新增员工，参数：{}", employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return Result
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> pagination(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult = employeeService.limit(employeePageQueryDTO);
        log.info("员工分页查询，参数：{}", employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * @param status 员工状态
     * @param id     员工id
     * @return
     */
    @ApiOperation("切换员工状态")
    @PostMapping("/status/{status}")
    public Result changeState(@PathVariable Integer status, long id) {
        employeeService.updateByid(status, id);
        log.info("启用禁用员工账号：{}，{}", status, id);
        return Result.success();
    }

    /**
     *
     * @param id
     * @return Result
     */
    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Employee employee = employeeService.findById(id);
        return Result.success(employee);
    }
    @ApiOperation("根据id修改员工信息")
    @PutMapping
    public Result changeEmp(@RequestBody EmployeeDTO employeeDTO){
        log.info("根据id修改员工信息：{}",employeeDTO);
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }
}
