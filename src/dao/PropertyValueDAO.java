package dao;

import bean.PropertyValue;
import util.DBUtil;
import util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyValueDAO {
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {
            String sql = "SELECT count(*) FROM property_value WHERE deleteAt IS NULL";
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void add(PropertyValue bean) {
        String sql = "INSERT INTO property_value (`pid`,`ptid`,`value`) VALUES (?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getProperty().getId());
            ps.setString(3, bean.getValue());
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
    public void update(PropertyValue bean){
        String sql = "update property_value set pid = ? , ptid = ? ,value = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getProperty().getId());
            ps.setString(3, bean.getValue());
            ps.setInt(4,bean.getId());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void delete(int id){
        String sql = "update property_value set deleteAt = ? where deleteAt is null and id = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setTimestamp(1, DateUtil.nowTimestamp());
            ps.setInt(2,id);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public PropertyValue get(int id){
        PropertyValue bean = null;
        String sql = "select * from property_value where deleteAt is null and id=?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new PropertyValue();
                bean.setId(rs.getInt("id"));
                bean.setValue(rs.getString("value"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setProperty(new PropertyDAO().get(rs.getInt("ptid")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }
    public PropertyValue get(int ptid,int pid){
        PropertyValue bean = null;
        String sql = "select * from property_value where deleteAt is null and pid=? and ptid = ?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ps.setInt(2,ptid);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                bean = new PropertyValue();
                bean.setId(rs.getInt("id"));
                bean.setValue(rs.getString("value"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setProperty(new PropertyDAO().get(rs.getInt("ptid")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return bean;
    }

    public List<PropertyValue> list(int pid){
        List<PropertyValue> beans = new ArrayList<>();
        String sql = "select * from property_value where `pid`=? and deleteAt is null";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,pid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                PropertyValue bean = new PropertyValue();
                bean.setId(rs.getInt("id"));
                bean.setValue(rs.getString("value"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setProperty(new PropertyDAO().get(rs.getInt("ptid")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }

    public List<PropertyValue> list(int start , int count){
        List<PropertyValue> beans = new ArrayList<>();
        String sql = "select * from property_value where deleteAt is null limit ?,?";
        try(Connection c = DBUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1,start);
            ps.setInt(2,count);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                PropertyValue bean = new PropertyValue();
                bean.setId(rs.getInt("id"));
                bean.setValue(rs.getString("value"));
                bean.setProduct(new ProductDAO().get(rs.getInt("pid")));
                bean.setProperty(new PropertyDAO().get(rs.getInt("ptid")));
                beans.add(bean);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return beans;
    }





}
