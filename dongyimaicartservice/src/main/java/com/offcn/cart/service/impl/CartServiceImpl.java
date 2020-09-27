package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.Cart;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    public Cart searchCartBySellerId(List<Cart> cartList,String sellerId){

        //3、查询该购物车中是否存在该商家
        for (Cart cart : cartList) {

            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    public TbOrderItem createOrderItem(TbItem item,Integer num){
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().longValue()*num));

        return orderItem;
    }

    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,long itemId){

        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId() == itemId){
                return orderItem;
            }
        }
        return null;

    }


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, long itemId, int num) {

        //1、查询该商品是否存在
        try {
            TbItem item = itemMapper.selectByPrimaryKey(itemId);

            if(item == null){
                throw new RuntimeException("商品不存在");
            }

            if(!item.getStatus().equals("1")){
                throw new RuntimeException("商品状态无效");
            }

            //2、查询该商家是否存在
            Cart newCart = searchCartBySellerId(cartList,item.getSellerId());

            if(newCart == null){
                //3.1、购物车不存在该商家
                //3.1.1、 新建该商家 存入该商品
                newCart = new Cart();

                newCart.setSellerId(item.getSellerId());
                newCart.setSellerName(item.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
                TbOrderItem orderItem = createOrderItem(item,num);
                orderItemList.add(orderItem);

                //将商品添加到商家购物车列表中
                newCart.setOrderItemList(orderItemList);

                //将商家购物车 添加到 用户购物车列表中
                cartList.add(newCart);

            }else{
                //3.2、购物车存在该商家
                //3.2.1 取出该商家的购物车 判断 是否存在该商品
                TbOrderItem newOrderItem = searchOrderItemByItemId(newCart.getOrderItemList(),itemId);


                //3.2.1.1 如果不存在该商品 将商品加入该商家购物车
                if(newOrderItem == null){
                    newOrderItem = createOrderItem(item,num);
                    newCart.getOrderItemList().add(newOrderItem);
                }else{
                    //3.2.1.2 如果存在该商品 修改数量 及 价格
                    newOrderItem.setNum(newOrderItem.getNum() + num);

                    newOrderItem.setTotalFee(new BigDecimal(newOrderItem.getPrice().longValue()*newOrderItem.getNum()));

                    if(newOrderItem.getNum()<=0){
                        //如果 商家购物车中的某商品数量小于等于0 将该商品从商家购物车中移除
                        newCart.getOrderItemList().remove(newOrderItem);
                    }

                    if(newCart.getOrderItemList().size() <= 0){
                        //如果 该商家购物车中一件商品都没有了 将该商家移除
                        cartList.remove(newCart);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cartList;
    }

    //合并cookie购物车
    public List<Cart> mergeCartList(List<Cart> cookieList,List<Cart> redisList){

        for (Cart cart : cookieList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisList = addGoodsToCartList(redisList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisList;
    }

    @Override
    public void addGoodsToRedis(String name,List<Cart> cartList) {

        redisTemplate.boundHashOps("cartList").put(name,cartList);

    }

    @Override
    public List<Cart> findGoodsFromRedis(String name) {
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(name);
        if(cartList == null){
            cartList = new ArrayList<Cart>();

        }
        return cartList;

    }
}
