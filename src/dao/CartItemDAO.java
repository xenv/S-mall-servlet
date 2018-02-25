package dao;

import bean.CartItem;
import service.ProductService;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO {
    public int getTotal(int uid) {
        int total = 0;
        String sql = "SELECT count(*) FROM cartitem WHERE uid=? and deleteAt IS NULL";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    public void add(CartItem bean) {
        String sql = "INSERT INTO cartitem (`uid`,`pid`,`number`,`sum`) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getUser().getId());
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
    public void update(CartItem bean){
        String sql = "update cartitem set uid=?,pid=?,number=?,sum=? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getUser().getId());
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
        String sql = "update cartitem set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public CartItem get(int id){
        CartItem bean = null;
        String sql = "select * from cartitem where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new CartItem();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setProduct(new ProductService().get(rs.getInt("pid")));
                bean.setNumber(rs.getInt("number"));
                bean.setSum(rs.getBigDecimal("sum"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }


    public List<CartItem> listByUser(int uid, int start , int count){
        List<CartItem> beans = new ArrayList<>();
        String sql = "select * from cartitem where uid = ? and deleteAt is null limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,uid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                CartItem bean = new CartItem();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setProduct(new ProductService().get(rs.getInt("pid")));
                bean.setNumber(rs.getInt("number"));
                bean.setSum(rs.getBigDecimal("sum"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<CartItem> listByUser(int uid) {
        return listByUser(uid,0,Short.MAX_VALUE);
    }
}
