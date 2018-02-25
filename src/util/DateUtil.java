package util;

public class DateUtil {
    public static java.sql.Timestamp d2t(java.util.Date d){
        if(d == null){
            return null;
        }
        return new java.sql.Timestamp(d.getTime());
    }
    public static java.util.Date t2d(java.sql.Timestamp t){
        if(t == null){
            return null;
        }
        return new java.util.Date(t.getTime());
    }
    public static java.sql.Timestamp nowTimestamp(){
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
}
