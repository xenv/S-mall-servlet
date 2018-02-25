package dao;

import bean.Category;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "select count(*) from category where deleteAt is null";
            ResultSet rs = s.executeQuery(sql);
            if(rs.next()){
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(Category bean){
        String sql = "insert into category (name,recommend) values (?,?)";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,bean.getName());
            ps.setInt(2,bean.getRecommend());
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

    public void update(Category bean){
        String sql = "update category set name = ? ,recommend = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1,bean.getName());
            ps.setInt(2,bean.getRecommend());
            ps.setInt(3,bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int id){
        String sql = "update category set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Category get(int id){
        Category bean = null;
        String sql = "select * from category where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new Category();
                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setRecommend(rs.getInt("recommend"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public List<Category> list(int start ,int count){
        List<Category> beans = new ArrayList<>();
        String sql = "select * from category where deleteAt is null ORDER BY recommend DESC , id DESC limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,start);
            ps.setInt(2,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Category bean = new Category();
                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setRecommend(rs.getInt("recommend"));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }
}
