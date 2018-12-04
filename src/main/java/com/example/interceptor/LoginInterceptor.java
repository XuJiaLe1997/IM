package com.example.interceptor;

import com.example.entity.HttpResponse;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Description: 登录拦截器
 * Date: 2018/11/20
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    // 不需要验证登录的链接
    private String[] urlWithoutLogin = {
            "/user/login",
            "/user/register",
            "/static/",
            "/test/"
    };

    private boolean matchURL(String url) {
        for (String str : urlWithoutLogin) {
            if (url.contains(str))
                return true;
        }
        return false;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        String url = request.getRequestURI();
        if (matchURL(url)) {
            LoggerUtil.log("请求无需拦截，URL："+url);
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SystemConstant.ATTRIBUTE_IN_SESSION) == null) {
            response.getWriter().print(new JSONObject(
                    new HttpResponse(SystemConstant.FAIL, "未登录", null)));
            LoggerUtil.log("触发登录拦截，拒绝请求，URL"+url);
            return false;
        }

        LoggerUtil.log("用户" + session.getAttribute(SystemConstant.ATTRIBUTE_IN_SESSION) + "已登录");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(httpServletRequest, httpServletResponse, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {
        super.afterCompletion(httpServletRequest, httpServletResponse, handler, e);
    }


}
