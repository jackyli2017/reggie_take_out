package com.jacky.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.jacky.reggie.common.BaseContext;
import com.jacky.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter" ,urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String requestURI = request.getRequestURI();

        //log.info("拦截到请求intercepted request: {})", requestURI);
        // 不需要被过滤的资源
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                // 静态资源直接放行
                "/backend/**",
                "/front/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login",      //移动端登录
        };

        boolean check = check(urls, requestURI);
        if(check) {
            // log.info("本次请求不需要处理no need to handle this request: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录The user logged in, id is: {}", request.getSession().getAttribute("employee"));

            //这里是为了自动填充字段功能来获取employeeId。
            //一次请求的线程id是一样的。
            long id = Thread.currentThread().getId();
            log.info("线程id为：{}", id);
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 移动用户登录
        if(request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录The user hasn't logged in");
        // 未登录则返回登录结果，通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;
    }

    /**
     * 检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for(String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return true;
            }
        }
        return false;
    }
}
