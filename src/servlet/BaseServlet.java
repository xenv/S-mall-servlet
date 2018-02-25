package servlet;

import util.Pagination;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public abstract class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = (String)req.getAttribute("method");
        try {
            //利用反射把url中的方法文本转化为方法进行调用
            req.setCharacterEncoding("utf-8");
            Method m = this.getClass().getMethod(method,HttpServletRequest.class,HttpServletResponse.class);
            String redirect = m.invoke(this,req,resp).toString();
            if(redirect.startsWith("@")){
                resp.sendRedirect(redirect.substring(1));
            }else if(redirect.startsWith("%"))
                resp.getWriter().print(redirect.substring(1));
            else
                req.getRequestDispatcher(redirect).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
