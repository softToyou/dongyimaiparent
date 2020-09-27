 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,brandService,specificationService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

    //JOSN格式装换
    $scope.stringToJosn = function(josnString,key){

        var values="";
        josnString = JSON.parse(josnString);

        for (var i=0;i < josnString.length;i++) {
            if (i>0){
                values+=",";
            }
            values +=josnString[i][key];
        }
        return values;
    }


    $scope.brandList={data:[]};//品牌列表

    $scope.specList={data:[]};//规格列表

    $scope.entity={customAttributeItems:[]};

    //新增扩展属性行
    $scope.addTableRow=function(){
        $scope.entity.customAttributeItems.push({});
    }

    $scope.removeTableRows = function(index){
        $scope.entity.customAttributeItems.splice(index,1);
    }

    $scope.findSpecList = function(){
        specificationService.selectOptionList().success(
            function (response) {
                $scope.specList= {data:response};
            }
        );
    }


    $scope.findBrandIds = function(){
        brandService.findAll().success(
            function (response) {
                $scope.brandList = {data:response};
            }
        )
    }

    //定义同时初始化品牌、规格列表数据
    $scope.initSelect=function(){
        $scope.findSpecList();
        $scope.findBrandIds();
    }

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.specIds = JSON.parse($scope.entity.specIds);
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);

            }
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
                    $scope.entity={};
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	