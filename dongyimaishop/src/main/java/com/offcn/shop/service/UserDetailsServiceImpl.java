package com.offcn.shop.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //数据库查询
        TbSeller seller = sellerService.findOne(username);

        if (seller!=null && "1".equals(seller.getStatus())){

            List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

            list.add(new SimpleGrantedAuthority("ROLE_SELLER"));

            return new User(username,seller.getPassword(),list);

        }else {

            return null;
        }
    }
}
