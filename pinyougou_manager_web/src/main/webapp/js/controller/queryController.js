app.controller("queryController",function ($scope,$controller,loginService,orderService) {

    $controller('baseController',{$scope:$scope});//继承
    //加载登录用户信息
    $scope.showUserInfo=function () {
        loginService.showUserInfo().success(function (response) {
            $scope.loginUserName = response.username;
        })
    }
    // $scope.searchEntity={'status':''};
    //搜索
    // $scope.search=function(page,rows){
    //     orderService.search(page,rows,$scope.order).success(
    //         function(response){
    //             $scope.list=response.rows;
    //             for (var i = 0; i < $scope.list.length; i++) {
    //                 $scope.list[i].spec = JSON.parse($scope.list[i].spec);
    //             }
    //             $scope.paginationConf.totalItems=response.total;//更新总记录数
    //         }
    //     );
    // }
    $scope.order = {'selllerId':'','nickName':'','name':'1'};
    //搜索

    $scope.search=function(page,rows){
        if ($scope.order.name == '1'){
            return;
        }
        if ($scope.order.selllerId == '' && $scope.order.nickName == '') {
            alert("店铺名称和商家ID不能为空");
            return;
        }
        orderService.findAllByUserId(page,rows,$scope.order).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
})