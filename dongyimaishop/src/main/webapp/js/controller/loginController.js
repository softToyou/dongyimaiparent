app.controller('loginController',function ($scop,$controller,loginService) {

    //读取当前登录人

    $scop.showLoginName=function () {

        loginService.loginName().success(
            function (response) {
                $scop.loginName = response.loginName;
            }
        );
    }

});