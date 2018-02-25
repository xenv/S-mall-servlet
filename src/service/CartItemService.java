package service;

import bean.CartItem;
import dao.CartItemDAO;

import java.util.List;

public class CartItemService {
    private CartItemDAO dao = new CartItemDAO();
    public int getTotal(int uid){return dao.getTotal(uid);}
    public void add(CartItem bean) {
        dao.add(bean);
    }
    public void update(CartItem bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public CartItem get(int id){
        return dao.get(id);
    }
    public List<CartItem> listByUser(int uid, int start , int count){
        return dao.listByUser(uid,start,count);
    }
    public List<CartItem> listByUser(int uid){
        return dao.listByUser(uid,0,Short.MAX_VALUE);
    }
}
