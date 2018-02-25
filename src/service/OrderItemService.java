package service;

import bean.OrderItem;
import dao.OrderItemDAO;

import java.util.List;

public class OrderItemService {
    private OrderItemDAO dao = new OrderItemDAO();
    public int getTotalByProduct(int pid){
        return dao.getTotalByProduct(pid);
    }
    public void add(OrderItem bean) {
        dao.add(bean);
    }
    public void update(OrderItem bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public OrderItem get(int id){
        return dao.get(id);
    }
    public List<OrderItem> listByOrder(int oid, int start , int count){
        return dao.listByOrder(oid,start,count);
    }
    public List<OrderItem> listByOrder(int oid){
        return dao.listByOrder(oid,0,Short.MAX_VALUE);
    }
    public List<OrderItem> listByProduct(int pid, int start , int count){
        return dao.listByProduct(pid,start,count);
    }
    public List<OrderItem> listByProduct(int pid){
        return dao.listByProduct(pid,0,Short.MAX_VALUE);
    }
}
