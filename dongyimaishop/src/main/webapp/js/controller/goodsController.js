 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,uploadService,typeTemplateService,goodsService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承

    //模拟状态值
    $scope.status = ['未审核','审核通过','审核驳回','已关闭'];

    $scope.itemCatList=[];

    $scope.findItemCatList = function(){

        itemCatService.findAll().success(
            function (response) {
                for (var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    }

    $scope.entity = {
        goods:{},
        goodsDesc:{
            itemImages:[],
            specificationItems:[]
        },
        itemList:[]
    };//定义页面实体结构


    $scope.createItemList = function(){
        //每一个产品对应的不同规格的列表
        $scope.entity.itemList = [{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
        //取所勾选的 规格
        var item = $scope.entity.goodsDesc.specificationItems;

        //遍历itemList 丰富规格
        for(var i=0;i<item.length;i++){
            $scope.entity.itemList = addColumn($scope.entity.itemList,item[i].attributeName,item[i].attributeValue);
        }

    }

    //对规格列表的拼接
    addColumn = function(list,columnName,columnValues){
        //方法的返回值 拼接好的每一条数据
        var newList = [];

        for(var i=0;i<list.length;i++){
            //克隆旧模板 模板中可能有多个对象 分别取出 分别规格拼接
            var oldRow = list[i];
            //采用JSON 的方法实现格式克隆 16G 64G
            //网络 [移动3G","移动4G","联通3G"]
            for(var j=0;j<columnValues.length;j++){
                //深克隆 json格式的字符串
                var newRow = JSON.parse(JSON.stringify(oldRow));

                newRow.spec[columnName] = columnValues[j];

                newList.push(newRow);
            }
        }
        return newList;
    }

    //修改规格的方法,$event复选框对象,name规格属性名称
    $scope.updateSepcification = function($event,name,value){
        //算法比较
        var  obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
        if (obj != null){
            if ($event.target.checked){
                obj.attributeValue.push(value);
            }else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
                if (obj.attributeValue.length == 0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
                }
            }
        }else {
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name, 'attributeValue':[value]});
        }
    }

    //集合容器比较内容 比较头
    $scope.searchObjectByKey =function(list,key,value){
        //遍历容器
        for (var i=0;i<list.length;i++){
            if (list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }

    //修改规格的方法,$event复选框对象,name规格属性名称
    $scope.updateSepcification = function($event,name,value){
        //算法比较
        var  obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
        if (obj != null){
            if ($event.target.checked){
                obj.attributeValue.push(value);
            }else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
                if (obj.attributeValue.length == 0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
                }
            }
        }else {
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name, 'attributeValue':[value]});
        }
    }


    //删除图片
    $scope.delete_images_entity =function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1)
    }


    //添加图片列表
    $scope.add_image_entity=function(){

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);

    }


    // 监控模板ID
    $scope.$watch('entity.goods.typeTemplateId',function (newValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

                if ($location.search()['id'] ==null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
                }
            }
        );
        // **********
        typeTemplateService.findSpecById(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });

    //模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    //三级标签
    $scope.$watch('entity.goods.category2Id',function (newValue) {
        itemCatService.findItemCatByParentId(newValue).success(
            function (response) {
                $scope.itemCatList3 = response;
            }
        );
    });

    //二级标签
    $scope.$watch('entity.goods.category1Id',function (newValue) {
        itemCatService.findItemCatByParentId(newValue).success(
            function (response) {
                $scope.itemCatList2 = response;
            }
        );
    });

    //查询一级标题
    $scope.findItemCatList1 = function(id){
        itemCatService.findItemCatByParentId(id).success(
            function (response) {
                $scope.itemCatList1 = response;
            }
        );
    }

   /* $scope.entity = {goods:{},goodsDesc:{itemImage:[]}}//定义页面实体结构*/

    //添加图片列表
   /* $scope.add_image_entity = function(){
        $scope.entity.goodsDesc.itemImage.push($scope.add_image_entity());
    }*/

    //删除图片
    $scope.dele_image=function(index){
        $scope.entity.uploadDesc.itemImages.splice(index,1);
    }

    //上传图片
    $scope.upload = function(){
        uploadService.upload().success(
            function (response) {
                if (response.success){
                    $scope.image_entity.url=response.message;
                }else {
                    alert(response.message);
                }
            }
        ).error(
            function () {
                alert("服务器发生错误！");
            }
        )
    }
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    //查询实体
    $scope.findOne=function(){

        var id= $location.search()['id'];//获取参数值

        if(id==null){
            return null;
        }
        goodsService.findOne(id).success(
            function(response){
                $scope.entity= response;

                editor.html($scope.entity.goodsDesc.introduction);
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }

            }
        );
    }
    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        var specificationItems= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(specificationItems,'attributeName',specName);
        if(object==null){
            if (object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
	
	//保存 
	$scope.save=function(){

        $scope.entity.goodsDesc.introduction=editor.html();

		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
                    //editor.html('');//清空富文本编辑器
                    //清空,避免图片因重复而不能添加
                    //$scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };
                    location.href = "goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	