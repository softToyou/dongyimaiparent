app.controller('brandController' ,function($scope,$http,brandService,$controller){

    $controller('baseController',{$scope:$scope});//继承

    //删除
    $scope.delete=function(){
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            }
        )
    }

    //根据主键查询
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }

    //保存
    $scope.save = function(){

        var methodName="save";

        if ($scope.entity.id!=null){
            methodName="update";
        }

        brandService.save(methodName,$scope.entity).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                }else {
                    alert(respones.message);
                }
            }
        )
    }

    // 查询所有
    $scope.findAll=function(){
        $http.get('../brand/findAll.do').success(
            function(response){
                $scope.list=response;
            }
        );
    }
    //分页查询
    $scope.findPages = function (pageNum,pageSize) {
        $http.get("../brand/findPages.do?pageNum="+pageNum+"&pageSize="+pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }
    //模糊查询分页
    $scope.searchEntity = {};

    $scope.search = function(pageNum,pageSize){
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }
});