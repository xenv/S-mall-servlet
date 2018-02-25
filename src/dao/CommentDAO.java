package dao;

import bean.Comment;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "SELECT count(*) FROM comment WHERE deleteAt IS NULL";
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
        String sql = "SELECT count(*) FROM comment WHERE pid=? and deleteAt IS NULL";
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

    public void add(Comment bean) {
        String sql = "INSERT INTO comment (`pid`,`uid`,`content`,`createDate`) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getUser().getId());
            ps.setString(3, bean.getContent());
            ps.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));
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
    public void update(Comment bean){
        String sql = "update comment set pid = ? , uid = ? ,content = ?,createDate = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getUser().getId());
            ps.setString(3, bean.getContent());
            ps.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void delete(int id){
        String sql = "update comment set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public Comment get(int id){
        Comment bean = null;
        String sql = "select * from comment where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new Comment();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getString("value")));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setContent(rs.getString("content"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }
    public Comment get(int pid,int uid){
        Comment bean = null;
        String sql = "select * from comment where deleteAt is null and pid=? and uid = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ps.setInt(2,uid);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new Comment();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getString("value")));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setContent(rs.getString("content"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public List<Comment> list(int pid,int start , int count){
        List<Comment> beans = new ArrayList<>();
        String sql = "select * from comment where `pid`=? and deleteAt is null ORDER BY id DESC limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ps.setInt(2,start);
            ps.setInt(3,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Comment bean = new Comment();
                bean.setId(rs.getInt("id"));
                bean.setUser(new UserDAO().get(rs.getInt("uid")));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setContent(rs.getString("content"));
                bean.setCreateDate(DateUtil.t2d(rs.getTimestamp("createDate")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }
}
