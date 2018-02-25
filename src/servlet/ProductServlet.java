package servlet;

import bean.Category;
import bean.Product;
import bean.Property;
import bean.PropertyValue;
import org.apache.commons.lang3.StringUtils;
import service.CategoryService;
import service.ProductService;

import service.PropertyService;
import service.PropertyValueService;
import util.Pagination;
import util.PaginationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class ProductServlet extends BaseServlet {
    private ProductService service = new ProductService();

    public String list(HttpServletRequest request, HttpServletResponse response) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Pagination pagination = PaginationUtil.createPagination(request, service.getTotal(cid));
        List<Product> products = service.listByCategory(cid, pagination.getStart(), pagination.getCount());
        request.setAttribute("products", products);
        request.setAttribute("category", new CategoryService().get(cid));
        request.setAttribute("pagination", pagination);
        return "jsp/admin/listProduct.jsp";
    }

    public String addUpdate(HttpServletRequest request, HttpServletResponse response) {

        int cid = Integer.parseInt(request.getParameter("cid"));
        String name = request.getParameter("name");
        String subTitle = request.getParameter("subTitle");
        BigDecimal originalPrice = new BigDecimal(request.getParameter("originalPrice"));
        BigDecimal nowPrice = new BigDecimal(request.getParameter("nowPrice"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        String pidString = request.getParameter("pid");

        Product product = null;
        if (pidString != null) {
            int pid = Integer.parseInt(pidString);
            product = service.get(pid);
        } else {
            product = new Product();
        }

        product.setName(name);
        product.setSubTitle(subTitle);
        product.setOriginalPrice(originalPrice);
        product.setNowPrice(nowPrice);
        product.setStock(stock);
        product.setCreateDate(new Date());

        if (pidString != null) {
            service.update(product);
        }else{
            Category category = new CategoryService().get(cid);
            product.setCategory(category);
            service.add(product);
        }
        return "@/admin/product_list?cid=" + cid;
    }

    public String edit(HttpServletRequest request, HttpServletResponse response) {

        int cid = Integer.parseInt(request.getParameter("cid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        request.setAttribute("product", service.get(pid));
        request.setAttribute("category", new CategoryService().get(cid));
        return "jsp/admin/editProduct.jsp";

    }

    public String delete(HttpServletRequest request, HttpServletResponse response) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        int pid = Integer.parseInt(request.getParameter("pid"));
        service.delete(pid);
        return "@/admin/product_list?cid=" + cid;
    }

    public String editPropertyValue(HttpServletRequest request, HttpServletResponse response) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = service.get(pid);
        HashMap<Property,PropertyValue> propsMap = new PropertyService().list(product);
        request.setAttribute("propsMap",propsMap);
        request.setAttribute("product",product);
        return "jsp/admin/editPropertyValue.jsp";
    }
    public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response) {
        String pidString = request.getParameter("pid");
        int pid = Integer.parseInt(pidString);
        Enumeration<String> paramNames = request.getParameterNames();
        PropertyValueService propertyValueService = new PropertyValueService();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement();
            int ptid = 0;
            String value = null;
            try{
                ptid = Integer.parseInt(StringUtils.remove(paramName,"ptid_"));
                value = request.getParameter(paramName);
            }catch (NumberFormatException e){
                continue;
            }
            if(value != null){
                PropertyValue propertyValue = propertyValueService.get(ptid,pid);
                if(propertyValue == null){
                    propertyValue = new PropertyValue();
                    propertyValue.setProduct(new ProductService().get(pid));
                    propertyValue.setProperty(new PropertyService().get(ptid));
                    propertyValue.setValue(value);
                    propertyValueService.add(propertyValue);
                }else{
                    propertyValue.setValue(value);
                    propertyValueService.update(propertyValue);
                }
            }
        }
        return "@/admin/product_editPropertyValue?pid="+pidString;
    }
}
