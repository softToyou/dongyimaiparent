package com.offcn.page.service.impl;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {

    private String pagedir = "e:/item/";

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {

        try {
            //获取freemark的
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template= configuration.getTemplate("item.ftl");
            //去取后台的数据
            Map map = new HashMap();

            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

            map.put("goods",goods);
            map.put("goodsDesc",goodsDesc);

            //查询商品分类
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

            map.put("itemCat1",itemCat1.getName());
            map.put("itemCat2",itemCat2.getName());
            map.put("itemCat3",itemCat3.getName());


            //根据商品id查询规格
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria= example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            map.put("itemList",itemList);


            //创建weiter对象
            Writer out = new FileWriter(pagedir+goods.getId()+".html");
            //输出
            template.process(map,out);

            System.out.println("输出成功！");
            //关闭输出流
            out.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {

        try {

            for (Long goodsId : goodsIds) {

                new File(pagedir+goodsId+".html").delete();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
