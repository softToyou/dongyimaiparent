app.service('cartservice',function ($http) {

    this.findUserById = function () {
        return $http.get('../address/findUserById.do')
    }


    this.addGoodsToCart = function (itemId,num) {
        return $http.post('../cart/addGoodsToCart.do?itemId='+itemId+'&num='+num);
    }

    this.findCartList = function () {
        return $http.get('../cart/findCookieCartList.do');
    }

})