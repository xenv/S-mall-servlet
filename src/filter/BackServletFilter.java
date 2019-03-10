package filter;


import bean.User;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


/**
 * 本类会处理所有请求，在此分发Servlet方法
 */
public class BackServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取根容器目录
        String contextPath= request.getServletContext().getContextPath();
        //获取uri
        String uri  = request.getRequestURI();
        //uri中消除根目录
        uri = StringUtils.remove(uri,contextPath);

        //获取登录信息
        User user = (User) request.getSession().getAttribute("user");

        //如果以/admin为前缀则是后台管理
        if(uri.equals("/admin")){
            response.sendRedirect(contextPath+ "/admin/");
            return;
        }
        //配置超级管理员权限的方法
        String[] superAdminPage = new String[]{
                "delete",
                "addUpdate",
                "updatePropertyValue",
        };
        // 访问admin目录下
        if(uri.startsWith("/admin/")){
            String servletName = StringUtils.substringBetween(uri,"/admin","_");
            servletName = servletName == null?"category":servletName;
            String method = StringUtils.substringAfterLast(uri,"_");
            method = method.equals("")?"list":method;
            String servletPath = String.format("/%s.servlet",servletName);
            request.setAttribute("method",method);
            boolean flag = false;
            for(String page : superAdminPage){
                if(page.equals(method)){
                    flag = true;
                    break;
                }
            }
            if(user!=null && !flag) {
                if (user.getGroup()!=null && (user.getGroup().equals("admin") || user.getGroup().equals("superAdmin"))) {
                    request.getRequestDispatcher(servletPath).forward(request, response);
                    return;
                } else {
                    throw new ServletException("权限不足，请确保有admin或者superAdmin权限");
                }
            }
            if(user!=null && user.getGroup()!=null && flag){
                if (user.getGroup()!=null && user.getGroup().equals("superAdmin")){
                    request.getRequestDispatcher(servletPath).forward(request, response);
                    return;
                }else {
                    throw new ServletException("权限不足，请确保有superAdmin权限");
                }
            }
            response.sendRedirect("/login?refer=admin");
            return;
        }

        //配置不需登录即可访问的方法：
        String[] noNeedAuthPage = new String[]{
                "home",
                "checkLogin",
                "register",
                "login",
                "product",
                "category",
                "loginIn",
                "registerAdd",
                "search"};
        //如果uri根目录不含 "." (非资源)则统一交由前台处理
        if(!(uri.contains(".") || (uri.lastIndexOf('/')>0))){
            String servletPath = "front.servlet";
            String method = uri.substring(1);
            if(method.equals("")) method = "home"; //缺省首页
            request.setAttribute("method",method);
            boolean flag = false;
            for(String noNeedPage : noNeedAuthPage){
                if(noNeedPage.equals(method)){
                    flag = true;
                    break;
                }
            }
            if(flag || user != null){
                request.getRequestDispatcher(servletPath).forward(request,response);
            }else{
                response.sendRedirect("login");
            }
            return;
        }

        //其他请求放行
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
