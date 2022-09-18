package com.tao.filter;

import com.alibaba.fastjson.JSON;
import com.tao.common.BaseContext;
import com.tao.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求的URI
        String requestURI = request.getRequestURI();
        log.info("本次请求的URL {}" ,requestURI);
        //放行的URI
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
        };

        //判断请求是否需要处理
        boolean check = check(urls,requestURI);

        //直接放行
        if(check){
            log.info("直接放行");
            filterChain.doFilter(request,response);
            return;
        }

        //已登录
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录");
            //保存ID并存入ThreadLocal中
            Long empID = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);

            filterChain.doFilter(request,response);
            return;
        }

        //已登录
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录");
            //保存ID并存入ThreadLocal中
            Long userID = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userID);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("已拦截请求");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    public boolean check(String[] urls,String requestURI){
        for(String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
