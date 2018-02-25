package service;

import bean.Category;
import bean.Product;
import dao.CategoryDAO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CategoryService {
    private CategoryDAO dao = new CategoryDAO();
    public int getTotal() {return dao.getTotal();}
    public void add(Category bean) {
        dao.add(bean);
    }
    public void update(Category bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public Category get(int id){
        return dao.get(id);
    }
    public List<Category> list(int start , int count){
        return dao.list(start,count);
    }
    public List<Category> list(){
        return dao.list(0,Short.MAX_VALUE);
    }
    public List<Category> listInHome(){
        List<Category> categories = this.list();
        for(Category c:categories){
            List<Product> products = new ProductService().listByCategory(c.getId(),0,64);
            for(Product product:products){
                String subTitle = product.getSubTitle();
                String shortTitle = StringUtils.substringBetween(subTitle," ");
                if(shortTitle==null){
                    shortTitle = subTitle;
                }
                product.setSubTitle(shortTitle);
            }
            c.setProducts(products);
        }
        return categories;
    }
}
