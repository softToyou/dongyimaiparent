package com.offcn.cart.service;

import com.offcn.pojo.Cart;

import java.util.List;

public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList, long itemId, int num);

    public void addGoodsToRedis(String name,List<Cart> cartList);

    public List<Cart> findGoodsFromRedis(String name);

    public List<Cart> mergeCartList(List<Cart> cookieList,List<Cart> redisList);

}
