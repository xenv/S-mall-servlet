package servlet;

import bean.Category;
import bean.Property;
import service.CategoryService;
import service.PropertyService;
import util.Pagination;
import util.PaginationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PropertyServlet extends BaseServlet {
    private PropertyService service = new PropertyService();
    public String list(HttpServletRequest request, HttpServletResponse response){
        int cid = Integer.parseInt(request.getParameter("cid"));
        Pagination pagination = PaginationUtil.createPagination(request,service.getTotal(cid));
        List<Property> properties = service.list(cid,pagination.getStart(),pagination.getCount());
        request.setAttribute("props",properties);
        request.setAttribute("category",new CategoryService().get(cid));
        request.setAttribute("pagination",pagination);
        return "jsp/admin/listProperty.jsp";
    }
    public String addUpdate(HttpServletRequest request, HttpServletResponse response){
        int cid = Integer.parseInt(request.getParameter("cid"));
		String name = request.getParameter("name");
		String  ptidString = request.getParameter("ptid");
		if(ptidString != null){
			int ptid = Integer.parseInt(ptidString);
			Property property = service.get(ptid);
			property.setName(name);
			service.update(property);
		}else{
			Property property = new Property();
			property.setName(name);
			Category category = new CategoryService().get(cid);
			property.setCategory(category);
			service.add(property);
		}
        return "@/admin/property_list?cid="+cid;
    }
	
	public String edit(HttpServletRequest request, HttpServletResponse response){
	
		int cid = Integer.parseInt(request.getParameter("cid"));
		int ptid = Integer.parseInt(request.getParameter("ptid"));
		request.setAttribute("prop",service.get(ptid));
		request.setAttribute("category",new CategoryService().get(cid));
		return "jsp/admin/editProperty.jsp";
		
	}
	public String delete(HttpServletRequest request, HttpServletResponse response){
		int cid = Integer.parseInt(request.getParameter("cid"));
		int ptid = Integer.parseInt(request.getParameter("ptid"));
		service.delete(ptid);
		return "@/admin/property_list?cid="+cid;
	}
	
}
