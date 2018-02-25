package service;

import bean.Order;
import bean.OrderItem;
import dao.OrderDAO;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {
    public static class OrderType {
        public static final String WAIT_PAY = "waitPay";
        public static final String WAIT_DELIVERY = "waitDelivery";
        public static final String WAIT_CONFIRM = "waitConfirm";
        public static final String WAIT_REVIEW = "waitReview";
        public static final String FINISH = "finish";
    }
    private OrderDAO dao = new OrderDAO();
    public int getTotal() {return dao.getTotal();}
    public void add(Order bean) {
        dao.add(bean);
    }
    public void update(Order bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }

    private void fill(Order rawOrder){
        List<OrderItem> ois = new OrderItemService().listByOrder(rawOrder.getId());
        BigDecimal totalPrice = new BigDecimal(0);
        int totalNumber = 0;
        for(OrderItem oi:ois){
            totalNumber += oi.getNumber();
            BigDecimal thisPrice =  new BigDecimal(oi.getNumber()).multiply(oi.getProduct().getNowPrice());
            totalPrice = totalPrice.add(thisPrice);
        }
        rawOrder.setOrderItems(ois);
        rawOrder.setTotalNumber(totalNumber);
        rawOrder.setTotalPrice(totalPrice);

    }

    public Order get(int id){
        Order o =  dao.get(id);
        fill(o);
        return o;
    }

    public List<Order> list(int uid, int start , int count){
        List<Order> orderList =  dao.list(uid,start,count);
        for(Order o : orderList){
            fill(o);
        }
        return orderList;
    }

    public List<Order> list(int start , int count){
        List<Order> orderList =  dao.list(start,count);
        for(Order o : orderList){
            fill(o);
        }
        return orderList;
    }
    public List<Order> list(int uid){
        return this.list(uid,0,Short.MAX_VALUE);
    }
    public List<Order> list(){
        return dao.list(0,Short.MAX_VALUE);
    }
}
