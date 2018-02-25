package service;

import bean.Product;
import bean.Property;
import bean.PropertyValue;
import dao.PropertyDAO;
import dao.PropertyValueDAO;

import java.util.HashMap;
import java.util.List;

public class PropertyService {
    private PropertyDAO dao = new PropertyDAO();
    public int getTotal(int cid) {return dao.getTotal(cid);}
    public void add(Property bean) {
        dao.add(bean);
    }
    public void update(Property bean) {
        dao.update(bean);
    }
    public void delete(int id) {
        dao.delete(id);
    }
    public Property get(int id){
        return dao.get(id);
    }
    public List<Property> list(int cid,int start ,int count){
        return dao.list(cid,start,count);
    }
    public List<Property> list(int cid){return dao.list(cid,0,Short.MAX_VALUE);}
    public HashMap<Property,PropertyValue> list(Product p){
        HashMap<Property,PropertyValue> result = new HashMap<>();
        List<Property> PropertiesInCategory = this.list(p.getCategory().getId());
        for (Property pt: PropertiesInCategory) {
            PropertyValue value = new PropertyValueDAO().get(pt.getId(),p.getId());
            if(null==value){
                value = new PropertyValue();
                value.setProduct(p);
                value.setProperty(pt);
            }
            result.put(pt,value);
        }
        return result;
    }
}
