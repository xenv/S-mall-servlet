package dao;

import bean.Category;
import bean.Product;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static util.DateUtil.d2t;

public class ProductDAO {
    public int getTotal(int cid) {
        int total = 0;
        String sql = "SELECT count(*) FROM product WHERE cid=? and deleteAt IS NULL";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    public void add(Product bean) {
        String sql = "INSERT INTO product (`cid`,`name`,`subTitle`,`originalPrice`," +
                "`nowPrice`,`stock`,`createDate`) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
            ps.setString(3, bean.getSubTitle());
            ps.setBigDecimal(4, bean.getOriginalPrice());
            ps.setBigDecimal(5, bean.getNowPrice());
            ps.setInt(6, bean.getStock());
            ps.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));
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
    public void update(Product bean){
        String sql = "update product set cid=?,name=?,subTitle=? ,originalPrice=?,nowPrice=?," +
                "stock=?,createDate=? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
            ps.setString(3, bean.getSubTitle());
            ps.setBigDecimal(4, bean.getOriginalPrice());
            ps.setBigDecimal(5, bean.getNowPrice());
            ps.setInt(6, bean.getStock());
            ps.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));
            ps.setInt(8, bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void delete(int id){
        String sql = "update product set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Product get(int id){
        Product bean = null;
        String sql = "select * from product where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new Product();
                bean.setId(rs.getInt("id"));
                bean.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getBigDecimal("originalPrice"));
                bean.setNowPrice(rs.getBigDecimal("nowPrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }


    public List<Product> listByCategory(int cid, int start , int count){
        List<Product> beans = new ArrayList<>();
        String sql = "select * from product where cid = ? and deleteAt is null ORDER BY id desc limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,cid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product bean = new Product();
                bean.setId(rs.getInt("id"));
                bean.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getBigDecimal("originalPrice"));
                bean.setNowPrice(rs.getBigDecimal("nowPrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<Product> listBySearch(String keyword, int start , int count){
        List<Product> beans = new ArrayList<>();
        if(null==keyword||0==keyword.trim().length())
            return beans;
        String sql = "select * from product where name like ? and deleteAt  is null ORDER BY id desc limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1,"%"+keyword.trim()+"%");
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Product bean = new Product();
                bean.setId(rs.getInt("id"));
                bean.setCategory(new CategoryDAO().get(rs.getInt("cid")));
                bean.setName(rs.getString("name"));
                bean.setSubTitle(rs.getString("subTitle"));
                bean.setOriginalPrice(rs.getBigDecimal("originalPrice"));
                bean.setNowPrice(rs.getBigDecimal("nowPrice"));
                bean.setStock(rs.getInt("stock"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

}
