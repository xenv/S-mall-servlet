package dao;

import bean.ProductImage;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
        public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "select count(*) from product_image where deleteAt is null";
            ResultSet rs = s.executeQuery(sql);
            if(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    public void add(ProductImage bean){
        String sql = "insert into product_image (`pid`,`type`) values (?,?)";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,bean.getProduct().getId());
            ps.setString(2, bean.getType());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                int id = rs.getInt(1);
                bean.setId(id);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(ProductImage bean){
        //无需直接更新，逻辑为先删除再更新
    }

    public void delete(int id){
        String sql = "update product_image set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<ProductImage> list(int pid ,String type, int start , int count){
        List<ProductImage> beans = new ArrayList<>();
        String sql = "select * from product_image where pid = ? and type = ? and deleteAt is null limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ps.setString(2,type);
            ps.setInt(3,start);
            ps.setInt(4,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ProductImage bean = new ProductImage();
                bean.setId(rs.getInt("id"));
                bean.setType(rs.getString("type"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }


    public ProductImage get(int id){
        ProductImage bean = null;
        String sql = "select * from product_image where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new ProductImage();
                bean.setId(rs.getInt("id"));
                bean.setType(rs.getString("type"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }
}
