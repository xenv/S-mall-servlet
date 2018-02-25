package service;

import bean.Comment;
import dao.CommentDAO;

import java.util.List;

public class CommentService {
    private CommentDAO dao = new CommentDAO();
    public int getTotalByProduct(int pid){
        return dao.getTotalByProduct(pid);
    }
    public void add(Comment bean) {
        dao.add(bean);
    }
    public void update(Comment bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public Comment get(int id){
        return dao.get(id);
    }
    public Comment get(int pid,int uid){
        return dao.get(pid,uid);
    }
    public List<Comment> list(int pid, int start , int count){
        List<Comment> result = dao.list(pid,0,count);
        for(Comment c : result){
            //使用户名变为匿名
            String name = c.getUser().getName();
            char[] chars = name.toCharArray();
            for(int i=1;i<chars.length-1;i++){
                chars[i] = '*';
            }
            c.getUser().setName(new String(chars));
        }
        return result;
    }
    public List<Comment> list(int pid){
        return this.list(pid,0,Short.MAX_VALUE);
    }
}
