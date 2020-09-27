package com.offcn.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.offcn.mapper.TbSpecificationMapper;
import com.offcn.group.Specification;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.pojo.TbSpecificationExample;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationExample.Criteria;


import com.offcn.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
	    //插入规格
		specificationMapper.insert(specification.getSpecification());
		//插入选项表
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {

            //插入主键
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());

            specificationOptionMapper.insert(tbSpecificationOption);
        }
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
	    //修改规格表
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		//修改规格选项表，先删除，再添加
        TbSpecificationOptionExample example= new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria =example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpecification().getId());
        specificationOptionMapper.deleteByExample(example);
        //新增
        for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());
            specificationOptionMapper.insert(tbSpecificationOption);
        }



	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
	  //创建组合对象
        Specification spec= new Specification();
        //查询规格表
        TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
        spec.setSpecification(specification);
        //查询规格选项表
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria= example.createCriteria();
        criteria.andSpecIdEqualTo(id);

        List<TbSpecificationOption> optionList=specificationOptionMapper.selectByExample(example);
        spec.setSpecificationOptionList(optionList);

        return spec;

	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
	    //先删除子表

		for(Long id:ids){
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria=example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);

            //后删除主表
            specificationMapper.deleteByPrimaryKey(id);
		}

	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
