package com.itheima.lease.common.mybatisplus;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.itheima.lease.web.*.mapper")
public class MybatisPlusConfiguration {

}

