app.controller('loginController',function ($scope,$controller,longinService) {

    $scope.showName = function () {
        longinService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }

})