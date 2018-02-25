package dao;

import bean.OrderItem;
import service.ProductService;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "SELECT count(*) FROM orderitem WHERE deleteAt IS NULL";
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getTotalByProduct(int pid) {
        int total = 0;
        String sql = "SELECT count(*) FROM orderitem WHERE pid=? and deleteAt IS NULL";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(OrderItem bean) {
        String sql = "INSERT INTO orderitem (`oid`,`pid`,`number`,`sum`) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getOrder().getId());
            ps.setInt(2, bean.getProduct().getId());
            ps.setInt(3,bean.getNumber());
            ps.setBigDecimal(4,bean.getSum());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(OrderItem bean){
        String sql = "update orderitem set oid=?,pid=?,number=?,sum=? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getOrder().getId());
            ps.setInt(2, bean.getProduct().getId());
            ps.setInt(3,bean.getNumber());
            ps.setBigDecimal(4,bean.getSum());
            ps.setInt(5, bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void delete(int id){
        String sql = "update orderitem set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public OrderItem get(int id){
        OrderItem bean = null;
        String sql = "select * from orderitem where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new OrderItem();
                bean.setId(rs.getInt("id"));
                bean.setOrder(new OrderDAO().get(rs.getInt("oid")));
                bean.setProduct(new ProductDAO().get(rs.getInt("uid")));
                bean.setNumber(rs.getInt("number"));
                bean.setSum(rs.getBigDecimal("sum"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }


    public List<OrderItem> listByOrder(int oid, int start , int count){
        List<OrderItem> beans = new ArrayList<>();
        String sql = "select * from orderitem where oid = ? and deleteAt is null limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,oid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                OrderItem bean = new OrderItem();
                bean.setId(rs.getInt("id"));
                bean.setOrder(new OrderDAO().get(rs.getInt("oid")));
                bean.setProduct(new ProductService().get(rs.getInt("pid")));
                bean.setNumber(rs.getInt("number"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<OrderItem> listByProduct(int pid, int start , int count){
        List<OrderItem> beans = new ArrayList<>();
        String sql = "select * from orderitem where pid = ? and deleteAt is null limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                OrderItem bean = new OrderItem();
                bean.setId(rs.getInt("id"));
                bean.setOrder(new OrderDAO().get(rs.getInt("oid")));
                bean.setProduct(new ProductDAO().get(rs.getInt("uid")));
                bean.setNumber(rs.getInt("number"));
                bean.setSum(rs.getBigDecimal("sum"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }


}
