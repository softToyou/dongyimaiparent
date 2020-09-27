package com.offcn.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import com.offcn.content.service.ContentService;

import com.offcn.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

    @Override
    public List<TbContent> findContentList(long categroyId) {

        //先去Redis中查询广告数据，查不到，再去mysql查询
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categroyId);
        if (contentList == null){
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categroyId);

            contentList = contentMapper.selectByExample(example);
            //就像Redis中存一份
            redisTemplate.boundHashOps("content").put(categroyId,contentList);
        }else {
            System.out.println("从Redis中读取...");
        }



        return contentList;
    }

    /**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		contentMapper.insert(content);

		//清楚缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

	    //查询categoryID是原来存储的id
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        //清楚缓存 删除当前节点
        redisTemplate.boundHashOps("content").delete(categoryId);

		contentMapper.updateByPrimaryKey(content);

		//判断content要修改的ID是否为其他广告ID，如果是说明其他广告id要新增
        if (content.getId().longValue() != categoryId.longValue()){
            redisTemplate.boundHashOps("content").delete(content.getId());
        }
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){

		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){

		    TbContent content = contentMapper.selectByPrimaryKey(id);
		    redisTemplate.boundHashOps("content").delete(content.getCategoryId());


			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
