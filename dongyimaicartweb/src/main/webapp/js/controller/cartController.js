app.controller('cartController',function ($scope,cartservice) {


    $scope.selectAddress = function(addr){

        $scope.address = addr;
    }

    $scope.order={paymentType:'1'};

    $scope.updatepaymentType = function(num){

        $scope.order.paymentType = num;

    }

    $scope.isSelectedAddress = function(addr){

        // alert($scope.address.contact + " ---- "+ addr.contact);
        //
        // alert($scope.address == addr);

        if($scope.address == addr){
            return true;
        }else{
            return false;
        }

    }


    $scope.findUserById = function(){
        cartservice.findUserById().success(
            function (response) {
                $scope.addressList = response;


                //设置默认地址
                for(var i=0;i< $scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
                // alert($scope.address.contact);
            }
        );
    }


    $scope.sum = function(list){
        $scope.totalValue = {totalNum:0,totalMoney:0};
        //用户的购物车
        for(var i=0;i<list.length;i++){
            var cart = list[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                $scope.totalValue.totalNum += cart.orderItemList[j].num;

                $scope.totalValue.totalMoney += cart.orderItemList[j].totalFee;
            }
        }

        return $scope.totalValue;
    }

    //添加商品到购物车
    $scope.addGoodsToCart = function (itemId,num) {
        cartservice.addGoodsToCart(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();
                }
            }
        );
    }


    $scope.findCartList = function () {
        cartservice.findCartList().success(
            function (response) {
                $scope.list = response;
                $scope.totalValue = $scope.sum($scope.list);
            }
        );
    }


})