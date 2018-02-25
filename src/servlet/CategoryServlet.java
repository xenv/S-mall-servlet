package servlet;

import bean.Category;
import service.CategoryService;
import util.PaginationUtil;
import util.Pagination;
import util.ParseUploadUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CategoryServlet extends BaseServlet {
    private CategoryService service = new CategoryService();
    public String list(HttpServletRequest request,HttpServletResponse response)
    {
        Pagination pagination = PaginationUtil.createPagination(request,service.getTotal());
        List<Category> categories = service.list(pagination.getStart(),pagination.getCount());
        request.setAttribute("categories",categories);
        request.setAttribute("pagination",pagination);
        return "jsp/admin/listCategory.jsp";
    }
    public String addUpdate(HttpServletRequest request,HttpServletResponse response) {
        Map<String, String> params = new HashMap<>();
        InputStream is = ParseUploadUtil.parseUpload(request, params);
        String name = params.get("name");
        String id = params.get("id");
        int recommend = Integer.parseInt(params.get("recommend"));
        Category c = null;

        if (id != null) {
            //编辑模式
            c = service.get(Integer.parseInt(id));
            c.setName(name);
            c.setRecommend(recommend);
            service.update(c);
        } else {
            c = new Category();
            c.setName(name);
            c.setRecommend(recommend);
            service.add(c);
        }

        File imageFolder = new File(request.getSession().getServletContext().getRealPath("pictures/category/"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        file.getParentFile().mkdirs();
        try {
            if (is.available() > 0) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte b[] = new byte[1024 * 1024 * 10];
                    int length = 0;
                    while ((length = is.read(b)) != -1) {
                        fos.write(b, 0, length);
                    }
                    fos.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "@/admin/category_list";
    }


    public String delete(HttpServletRequest request , HttpServletResponse response){
        int id = Integer.parseInt(request.getParameter("id"));
        service.delete(id);
        return "@/admin/category_list";
    }
    public String edit(HttpServletRequest request,HttpServletResponse response){
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = service.get(cid);
        request.setAttribute("c", c);
        return "jsp/admin/editCategory.jsp";
    }
}
