package servlet;

import bean.*;
import service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.*;
import util.PasswordUtil;
import util.ProductSort;

@SuppressWarnings("deprecation")
public class FrontServlet extends BaseServlet {
    public String home(HttpServletRequest request, HttpServletResponse response) {
        List<Category> categories = new CategoryService().listInHome();
        request.setAttribute("categories", categories);
        return "jsp/home.jsp";
    }

    public String register(HttpServletRequest request, HttpServletResponse response) {
        return "jsp/register.jsp";
    }

    public String registerAdd(HttpServletRequest request, HttpServletResponse response) {
        String refer = request.getParameter("refer");
        request.setAttribute("refer", refer);
        String failUrl = "jsp/register.jsp";
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        name = StringEscapeUtils.escapeHtml4(name);
        password = PasswordUtil.encryptPassword(password);
        boolean exist = new UserService().isExist(name);
        if (exist) {
            request.setAttribute("msg", "用户名已经被使用了，请换一个用户名吧");
            return failUrl;
        }
        if (name.isEmpty() || password.isEmpty()) {
            request.setAttribute("msg", "用户名或密码不能为空");
            return failUrl;
        }
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        new UserService().add(user);
        return "jsp/registerSuccess.jsp";
    }

    public String login(HttpServletRequest request, HttpServletResponse response) {
        return "jsp/login.jsp";
    }

    public String loginIn(HttpServletRequest request, HttpServletResponse response) {
        String refer = request.getParameter("refer");
        String name = request.getParameter("name");
        name = StringEscapeUtils.escapeHtml4(name);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml4(password);
        password = PasswordUtil.encryptPassword(password);
        User user = new UserService().get(name, password);
        if (user == null) {
            request.setAttribute("msg", "用户名或密码错误");
            request.setAttribute("refer", refer);
            return "jsp/login.jsp";
        }
        request.getSession().setAttribute("user", user);
        if(refer==null || refer.equals("")){
            refer = "/";
        }
        return "@" + refer;
    }

    public String checkLogin(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        return null == user ? "%fail" : "%success";
    }

    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        return "@/";
    }

    public String product(HttpServletRequest request, HttpServletResponse response) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        Product product = new ProductService().get(pid);
        List<Comment> comments = new CommentService().list(pid);
        List<ProductImage> topImages = new ProductImageService().listTopImage(pid, 0, 5);
        List<ProductImage> detailImages = new ProductImageService().listDetailImage(pid, 0, Short.MAX_VALUE);
        Map<Property, PropertyValue> propertyValues = new PropertyService().list(product);
        List<Category> categories = new CategoryService().list(0, 4);
        request.setAttribute("product", product);
        request.setAttribute("comments", comments);
        request.setAttribute("pvs", propertyValues);
        request.setAttribute("topImages", topImages);
        request.setAttribute("detailImages", detailImages);
        request.setAttribute("categories", categories);
        return "jsp/product.jsp";
    }

    public String category(HttpServletRequest request, HttpServletResponse response) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        String sort = request.getParameter("sort");
        List<Product> products = new ProductService().listByCategory(cid);
        ProductSort.sort(products, sort);
        request.setAttribute("products", products);
        request.setAttribute("category", new CategoryService().get(cid));
        request.setAttribute("categories", new CategoryService().list(0, 7));
        return "jsp/category.jsp";
    }

    public String search(HttpServletRequest request, HttpServletResponse response) {
        String keyword = request.getParameter("keyword");
        String sort = request.getParameter("sort");
        List<Product> products = new ProductService().listBySearch(keyword, 0, 20);
        keyword = StringEscapeUtils.escapeHtml4(keyword);
        request.setAttribute("keyword", keyword);
        request.setAttribute("products", products);
        ProductSort.sort(products, sort);
        request.setAttribute("categories", new CategoryService().list(0, 7));
        return "jsp/search.jsp";
    }

    public String buyOne(HttpServletRequest request, HttpServletResponse response) {
        //客户下单，在session里面而不是在数据库里面注册一个cartItem项目，打上标示跳转到下单页面
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product product = new ProductService().get(pid);
        User user = (User) request.getSession().getAttribute("user");
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setNumber(num);
        cartItem.setSum(cartItem.getProduct().getNowPrice().multiply(new BigDecimal(cartItem.getNumber())));
        request.getSession().setAttribute("tempCartItem", cartItem);
        return "@buy?ciid=-1"; //-1的话提醒buy页面从session取cartItem而不是从数据里面拿
    }

    public String buy(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
            String[] cartItemIdStrings = request.getParameterValues("ciid");
        List<CartItem> cartItems = new ArrayList<>();
        BigDecimal total = new BigDecimal(0);
        for (String cartItemIdString : cartItemIdStrings) {
            int cartItemId = Integer.parseInt(cartItemIdString);
            if (cartItemId == -1) {//点的是立即购买，从session里面拿cartItem
                CartItem cartItem = (CartItem) request.getSession().getAttribute("tempCartItem");
                total = total.add(cartItem.getSum());
                cartItem.setId(-1);
                cartItems.add(cartItem);
                break;
            } else {//从购物车中来的
                List<CartItem> userList = new CartItemService().listByUser(user.getId());
                for(CartItem userItem:userList) { //判断是否是该用户的订单
                    if(userItem.getId()==cartItemId) {
                        CartItem cartItem = new CartItemService().get(cartItemId);
                        total = total.add(cartItem.getSum());
                        cartItems.add(cartItem);
                        break;
                    }
                }
            }
        }

        request.getSession().setAttribute("cartItems", cartItems);
        request.setAttribute("total", total);
        return "jsp/buy.jsp";
    }

    public String addCart(HttpServletRequest request, HttpServletResponse response) {
        //ajax请求加购物车
        int pid = Integer.parseInt(request.getParameter("pid"));
        int num = Integer.parseInt(request.getParameter("num"));
        Product product = new ProductService().get(pid);
        User user = (User) request.getSession().getAttribute("user");
        List<CartItem> cartItems = new CartItemService().listByUser(user.getId());
        boolean found = false;
        //如果购物车中已经有相关项目就拿出来加数量，更新
        for (CartItem item : cartItems) {
            if (product.getId() == item.getProduct().getId()) {
                int newNum = item.getNumber() + num;
                //判断库存是否足够
                if(product.getStock()<newNum){
                    return "%OutOfStock";
                }
                item.setNumber(newNum);
                item.setSum(item.getProduct().getNowPrice().multiply(new BigDecimal(item.getNumber())));
                new CartItemService().update(item);
                found = true;
                break;
            }
        }
        //如果购物车里面没有相关项目新建一个
        if(!found) {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            if(product.getStock()<num){
                return "%OutOfStock";
            }
            cartItem.setNumber(num);
            cartItem.setSum(cartItem.getProduct().getNowPrice().multiply(new BigDecimal(cartItem.getNumber())));
            new CartItemService().add(cartItem);
        }
        return "%success";
    }
    public String cart(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        List<CartItem> cartItems = new CartItemService().listByUser(user.getId());
        request.setAttribute("cartItems",cartItems);
        request.setAttribute("categories", new CategoryService().list(0, 4));
        return "jsp/cart.jsp";
    }
    public String changeCartNum(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        if(null==user)
            return "%fail";
        int ciid = Integer.parseInt(request.getParameter("ciid"));
        int num = Integer.parseInt(request.getParameter("num"));
        List<CartItem> cartItems = new CartItemService().listByUser(user.getId());
        for(CartItem item:cartItems){
            if(item.getId()==ciid){
                Product product = item.getProduct();
                if(product.getStock()>=num) {
                    item.setNumber(num);
                    new CartItemService().update(item);
                    return "%success";
                }
                break;
            }
        }
        return "%fail";
    }
    public String deleteCartItem(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        if(null==user)
            return "%fail";
        int ciid = Integer.parseInt(request.getParameter("ciid"));
        List<CartItem> cartItems = new CartItemService().listByUser(user.getId());
        for(CartItem item:cartItems){
            if(item.getId()==ciid){
                new CartItemService().delete(ciid);
                return "%success";
            }
        }
        return "%fail";
    }
    @SuppressWarnings("unchecked")
    public String createOrder(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<CartItem> cartItems = (List<CartItem>) request.getSession().getAttribute("cartItems");
        String address = request.getParameter("address");
        String post = request.getParameter("post");
        String receiver = request.getParameter("receiver");
        String mobile = request.getParameter("mobile");
        String userMessage = request.getParameter("userMessage");
        Order order = new Order();
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date())+RandomUtils.nextInt();
        order.setOrderCode(orderCode);
        order.setAddress(address);
        order.setPost(post);
        order.setReceiver(receiver);
        order.setMobile(mobile);
        order.setUserMessage(userMessage);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.OrderType.WAIT_PAY);
        BigDecimal sum = new BigDecimal(0);
        for(CartItem item:cartItems){
            sum = sum.add(item.getSum());
        }
        order.setSum(sum);
        new OrderService().add(order);
        for(CartItem item:cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setNumber(item.getNumber());
            orderItem.setProduct(item.getProduct());
            orderItem.setSum(item.getSum());
            new CartItemService().delete(item.getId());
            new OrderItemService().add(orderItem);
        }


        return "@pay?oid="+order.getId();
    }

    public String pay(HttpServletRequest request, HttpServletResponse response) {
        int orderId = Integer.parseInt(request.getParameter("oid"));
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        for(Order item : orders){
            if(orderId == item.getId()){
                request.setAttribute("order",item);
                return "jsp/pay.jsp";
            }
        }
        return "@/";
    }
    public String payed(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int orderId = Integer.parseInt(request.getParameter("oid"));
        for(Order item : orders){
            if(orderId == item.getId()){
                item.setPayDate(new Date());
                item.setStatus(OrderService.OrderType.WAIT_DELIVERY);
                new OrderService().update(item);
                request.setAttribute("order",item);
                return "jsp/payed.jsp";
            }
        }
        return "@/";
    }
    public String myOrder(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        request.setAttribute("orders",orders);
        return "jsp/myOrder.jsp";
    }
    public String confirmPay(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oid = Integer.parseInt(request.getParameter("oid"));
        for(Order order:orders){
            if(order.getId()==oid){
                request.setAttribute("order",order);
                return "jsp/confirmPay.jsp";
            }
        }
        return "@/";
    }
    public String confirmed(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oid = Integer.parseInt(request.getParameter("oid"));
        for(Order order:orders){
            if(order.getId()==oid){
                order.setConfirmDate(new Date());
                order.setStatus(OrderService.OrderType.WAIT_REVIEW);
                new OrderService().update(order);
                request.setAttribute("order",order);
                return "jsp/confirmed.jsp";
            }
        }
        return "@/";
    }
    public String deleteOrder(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oid = Integer.parseInt(request.getParameter("oid"));
        for(Order order:orders){
            if(order.getId()==oid){
                new OrderService().delete(oid);
                return "%success";
            }
        }
        return "%fail";
    }
    public String comment(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        for(Order order:orders){
            for(OrderItem item : order.getOrderItems()){
                if(oiid == item.getId()){
                    request.setAttribute("orderItem",item);
                    request.setAttribute("order",order);
                    return "jsp/comment.jsp";
                }
            }
        }
        return "@/";
    }
    public String addComment(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oiid = Integer.parseInt(request.getParameter("oiid"));
        for(Order order:orders){
            for(OrderItem item : order.getOrderItems()){
                if(oiid == item.getId()) {
                    int allCount = 0;
                    int hasComment = 0;
                    for(OrderItem item2:order.getOrderItems()){
                        Comment comment = new CommentService().get(item2.getProduct().getId(), user.getId());
                        allCount++;
                        if(comment != null){
                            hasComment++;
                        }
                    }
                    Comment comment = new Comment();
                    comment.setContent(request.getParameter("content"));
                    comment.setCreateDate(new Date());
                    comment.setProduct(item.getProduct());
                    comment.setUser(user);
                    new CommentService().add(comment);
                    if(allCount-hasComment == 1){
                        order.setStatus(OrderService.OrderType.FINISH);
                        new OrderService().update(order);
                    }
                    return "@myOrder";
                }

            }
        }
        return "@/";
    }
    public String delivery(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        List<Order> orders = new OrderService().list(user.getId());
        int oid = Integer.parseInt(request.getParameter("oid"));
        for(Order order:orders){
            if(order.getId()==oid){
                order.setDeliverDate(new Date());
                order.setStatus(OrderService.OrderType.WAIT_CONFIRM);
                new OrderService().update(order);
                return "@myOrder";
            }
        }
        return "@/";
    }
    public String cartNumber(HttpServletRequest request, HttpServletResponse response) {
        User user =(User) request.getSession().getAttribute("user");
        int number = new CartItemService().getTotal(user.getId());
        return "%"+number;
    }
}
