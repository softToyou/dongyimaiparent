app.controller('baseController',function ($scope) {
    //顶一个数组
    $scope.selectIds=[];
        //选中的ID集合
        // 更新复选
    $scope.updateSelection = function($event, id) {
        if($event.target.checked){//如果是被选中,则增加到数组
            $scope.selectIds.push(id);
        }else{
            //没有被选中
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);//删除
        }
    }

//重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        // $scope.findPages( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }
//分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };
});


