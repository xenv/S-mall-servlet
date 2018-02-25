package util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParseUploadUtil {
    /**
     *
     * @param request Servlet请求
     * @param params 一个预备的键值表储存非文件流的参数Map
     * @return 上传的文件流
     */
    public static InputStream parseUpload(HttpServletRequest request, Map<String,String> params){
        InputStream is = null;
        try{
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            factory.setSizeThreshold(10*1024*1024);
            List<FileItem> items= upload.parseRequest(request);
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    is = item.getInputStream();
                } else {
                    String paramName = item.getFieldName();
                    String paramValue = item.getString();
                    paramValue = new String(paramValue.getBytes("ISO-8859-1"), "UTF-8");
                    params.put(paramName, paramValue);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return is;
    }
}
