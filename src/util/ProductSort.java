package util;

import bean.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductSort {
    public static void sort(List<Product> products,String sortWay){
        sortWay = sortWay == null?"default":sortWay;
        switch (sortWay){
            case "comment":
                products.sort((p1, p2) -> p2.getCommentCount() - p1.getCommentCount());
                break;
            case "date":
                products.sort(Comparator.comparing(Product::getCreateDate));
                break;
            case "saleCount":
                products.sort((p1,p2)->p2.getCommentCount()-p1.getCommentCount());
                break;
            case "price":
                products.sort((p1,p2)-> p1.getNowPrice().compareTo(p2.getNowPrice()));
                break;
            case "priceInverse":
                products.sort((p1,p2)-> p2.getNowPrice().compareTo(p1.getNowPrice()));
                break;
            default:
                products.sort((p1,p2)->p2.getCommentCount()*p2.getSaleCount()-p1.getCommentCount()-p1.getSaleCount());
                break;
        }
    }
}
