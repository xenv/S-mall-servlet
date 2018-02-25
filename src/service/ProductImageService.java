package service;

import bean.ProductImage;
import dao.ProductImageDAO;

import java.util.List;

public class ProductImageService {
    public static class ImageType {
        public static final String TYPE_TOP = "type_top";
        public static final String TYPE_DETAIL = "type_detail";
    }

    private ProductImageDAO dao = new ProductImageDAO();
    public void add(ProductImage bean) {
        dao.add(bean);
    }
    public void update(ProductImage bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public ProductImage get(int id){
        return dao.get(id);
    }
    public ProductImage getFirstImage(int pid){
        ProductImage productImage= null;
        List<ProductImage> productImageList= dao.list(pid,ImageType.TYPE_TOP,0,1);
        if(productImageList.size()>0){
            productImage = productImageList.get(0);
        }
        return productImage;
    }
    public List<ProductImage> listTopImage(int pid, int start , int count){
        return dao.list(pid,ImageType.TYPE_TOP,start,count);
    }
    public List<ProductImage> listDetailImage(int pid, int start , int count){
        return dao.list(pid,ImageType.TYPE_DETAIL,start,count);
    }
}
