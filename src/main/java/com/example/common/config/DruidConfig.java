package com.example.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;

//对应bean.xml
@Configuration
public class DruidConfig {
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    //后台监控:相于web.xml
    //因为springboot内置了servlet容器，所以用这种方式替代web.xml
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        //后台需要账密登录
        HashMap<String, String> initParameters = new HashMap();
        //增加配置
        initParameters.put("loginUsername", "admin");  //登录的key是固定的
        initParameters.put("loginPassword", "123");

        //允许谁能访问
        initParameters.put("allow", "");

        //禁止谁能访问
        initParameters.put("wade", "192.168.11.123");


        bean.setInitParameters(initParameters); //设置初始化参数

        return bean;

    }

    //filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        //设置过滤器配置
        HashMap<String, String> initParameters = new HashMap();

        //这些东西不进行统计
        initParameters.put("exclusion", "*.js, *.css, /druid/*");

        bean.setInitParameters(initParameters);

        return bean;
    }



}
