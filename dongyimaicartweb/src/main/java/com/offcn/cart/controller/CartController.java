package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Result;
import com.offcn.pojo.Cart;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/addGoodsToCart")
    public Result addGoodsToCart(Long itemId,int num) {

        try {

            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //无论登录未登录 调用查询购物车方法 方法里面 自动区分 cookie 还是 redis
            List<Cart> cartList = findCartList();

            //2、加入购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if ("anonymousUser".equals(name)) {
                //未登录 访问cookie
                //1、先查询cookie购物车列表
                try {
                    //3、向cookie中存入该购物车
                    CookieUtil.setCookie(request, response, "cartList",
                            JSON.toJSONString(cartList), 3600 * 24, "UTF-8");

                    return new Result(true, "添加成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false, "添加失败");
                }

            } else {

                cartService.addGoodsToRedis(name, cartList);
            }
            return new Result(true,"添加购物车成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }
    }


    @RequestMapping("/findCookieCartList")
    public List<Cart> findCartList(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        String cartListString = CookieUtil.getCookieValue(request, "cartList","UTF-8");

        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }

        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if("anonymousUser".equals(username)){
            return cartList_cookie;
        }else{
            //从redis
            List<Cart> redisCartList = cartService.findGoodsFromRedis(username);

            if(cartList_cookie.size() > 0){
                redisCartList = cartService.mergeCartList(cartList_cookie,redisCartList);
            }

            //已经将cookie和redis 数据进行了合并
            CookieUtil.deleteCookie(request,response,"cartList");

            //将合并好的redis的购物车 存入到 redis中
            cartService.addGoodsToRedis(username,redisCartList);

            return redisCartList;
        }

    }

}
