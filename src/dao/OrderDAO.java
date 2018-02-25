package dao;

import bean.Order;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "SELECT count(*) FROM order_ WHERE deleteAt IS NULL";
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    public void add(Order bean) {
        String sql = "INSERT INTO order_ (`uid`,`orderCode`,`address`,`post`,`receiver`," +
                "`mobile`,`userMessage`,`createDate`,`payDate`,`deliverDate`,`confirmDate`,`status`,`sum`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getUser().getId());
            ps.setString(2, bean.getOrderCode());
            ps.setString(3, bean.getAddress());
            ps.setString(4, bean.getPost());
            ps.setString(5, bean.getReceiver());
            ps.setString(6, bean.getMobile());
            ps.setString(7, bean.getUserMessage());
            ps.setTimestamp(8, DateUtil.d2t(bean.getCreateDate()));
            ps.setTimestamp(9, DateUtil.d2t(bean.getPayDate()));
            ps.setTimestamp(10, DateUtil.d2t(bean.getDeliverDate()));
            ps.setTimestamp(11, DateUtil.d2t(bean.getConfirmDate()));
            ps.setString(12, bean.getStatus());
            ps.setBigDecimal(13, bean.getSum());
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
    public void update(Order bean){
        String sql = "update order_ set uid=?,orderCode=?,address=?,post=?,receiver=?," +
                "mobile=?,userMessage=?,createDate=?,payDate=?,deliverDate=?,confirmDate=?,status=?,sum=?" +
                " where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getUser().getId());
            ps.setString(2, bean.getOrderCode());
            ps.setString(3, bean.getAddress());
            ps.setString(4, bean.getPost());
            ps.setString(5, bean.getReceiver());
            ps.setString(6, bean.getMobile());
            ps.setString(7, bean.getUserMessage());
            ps.setTimestamp(8, DateUtil.d2t(bean.getCreateDate()));
            ps.setTimestamp(9, DateUtil.d2t(bean.getPayDate()));
            ps.setTimestamp(10, DateUtil.d2t(bean.getDeliverDate()));
            ps.setTimestamp(11, DateUtil.d2t(bean.getConfirmDate()));
            ps.setString(12, bean.getStatus());
            ps.setBigDecimal(13, bean.getSum());
            ps.setInt(14, bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void delete(int id){
        String sql = "update order_ set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Order get(int id){
        Order bean = null;
        String sql = "select * from order_ where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new Order();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setOrderCode(rs.getString("orderCode"));
                bean.setSum(rs.getBigDecimal("sum"));
                bean.setAddress(rs.getString("address"));
                bean.setPost(rs.getString("post"));
                bean.setReceiver(rs.getString("receiver"));
                bean.setMobile(rs.getString("mobile"));
                bean.setUserMessage(rs.getString("userMessage"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                bean.setPayDate(DateUtil.t2d(rs.getTimestamp("payDate")));
                bean.setDeliverDate(DateUtil.t2d(rs.getTimestamp("deliverDate")));
                bean.setConfirmDate(DateUtil.t2d(rs.getTimestamp("confirmDate")));
                bean.setStatus(rs.getString("status"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }


    public List<Order> list(int start , int count){
        List<Order> beans = new ArrayList<>();
        String sql = "select * from order_ where deleteAt is null ORDER BY id DESC limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,start);
            ps.setInt(2,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Order bean = new Order();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setOrderCode(rs.getString("orderCode"));
                bean.setSum(rs.getBigDecimal("sum"));
                bean.setAddress(rs.getString("address"));
                bean.setPost(rs.getString("post"));
                bean.setReceiver(rs.getString("receiver"));
                bean.setMobile(rs.getString("mobile"));
                bean.setUserMessage(rs.getString("userMessage"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                bean.setPayDate(DateUtil.t2d(rs.getTimestamp("payDate")));
                bean.setDeliverDate(DateUtil.t2d(rs.getTimestamp("deliverDate")));
                bean.setConfirmDate(DateUtil.t2d(rs.getTimestamp("confirmDate")));
                bean.setStatus(rs.getString("status"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<Order> list() {
        return list(0,Short.MAX_VALUE);
    }

    public List<Order> list(int uid,int start , int count){
        List<Order> beans = new ArrayList<>();
        String sql = "select * from order_ where uid = ? and deleteAt is null ORDER BY id DESC limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,uid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Order bean = new Order();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setOrderCode(rs.getString("orderCode"));
                bean.setAddress(rs.getString("address"));
                bean.setPost(rs.getString("post"));
                bean.setReceiver(rs.getString("receiver"));
                bean.setSum(rs.getBigDecimal("sum"));
                bean.setMobile(rs.getString("mobile"));
                bean.setUserMessage(rs.getString("userMessage"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                bean.setPayDate(DateUtil.t2d(rs.getTimestamp("payDate")));
                bean.setDeliverDate(DateUtil.t2d(rs.getTimestamp("deliverDate")));
                bean.setConfirmDate(DateUtil.t2d(rs.getTimestamp("confirmDate")));
                bean.setStatus(rs.getString("status"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }
}
