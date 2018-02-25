package service;

import bean.User;
import dao.UserDAO;

import java.util.List;

public class UserService {
    private UserDAO dao = new UserDAO();
    public int getTotal() {return dao.getTotal();}
    public void add(User bean) {
        dao.add(bean);
    }
    public void update(User bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public User get(int id){
        return dao.get(id);
    }
    public User get(String name){
        return dao.get(name);
    }
    public User get(String name,String password){
        return dao.get(name,password);
    }
    public boolean passwordIsRight(String id,String password){
        return dao.get(id, password) != null;
    }
    public boolean isExist(String name){
        return dao.get(name) != null;
    }
    public List<User> list(int start , int count){
        return dao.list(start,count);
    }
    public List<User> list(){
        return dao.list(0,Short.MAX_VALUE);
    }
}
