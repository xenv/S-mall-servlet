package util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class PaginationUtil {
    public static Pagination createPagination(HttpServletRequest request,int total){
        //获取Pagination本身的两个参数
        int start = 0;
        try{
            start = Integer.parseInt(request.getParameter("pageStart"));
        }catch (Exception ignored){

        }
        int count = 5;
        try{
            count = Integer.parseInt(request.getParameter("pageCount"));
        }catch (Exception ignored){

        }
        //预定义一个param
        StringBuilder param = new StringBuilder();

        //非Pagination的参数，生成param参数
        Enumeration enu=request.getParameterNames();
        while(enu.hasMoreElements()){
            String paraName=(String)enu.nextElement();
            //start写在jsp里面
            if(!paraName.equals("pageStart")) {
                param.append('&').append(paraName).append("=").append(request.getParameter(paraName));
            }
        }

        Pagination pagination = new Pagination(start,count,total);
        pagination.setParam(param.toString());
        return pagination;
    }
}
