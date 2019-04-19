app.controller("indexController",function ($scope,$controller,loginService,orderService) {

    $controller('baseController',{$scope:$scope});//继承
    //加载登录用户信息
    $scope.showUserInfo=function () {
        loginService.showUserInfo().success(function (response) {
            $scope.loginUserName = response.username;
        })
    }
    $scope.searchEntity={'status':''};
    $scope.pageNo = 1;
    $scope.pageSize = 1;

    //搜索
    $scope.search=function(){
        $scope.totalPages = 0;
        orderService.search($scope.pageNo,$scope.pageSize,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                for (var i = 0; i < $scope.list.length; i++) {
                    for (var j = 0; j < $scope.list[i].orderGoods.length; j++) {
                        $scope.list[i].orderGoods[j].spec = JSON.parse($scope.list[i].orderGoods[j].spec);
                    }

                }
                var page = parseInt((response.total / $scope.pageSize));
                $scope.totalPages = (response.total % $scope.pageSize) == 0? page : page + 1;//更新总记录数
                buildPageLabel();
            }
        );
    }

    //构建分页标签
    buildPageLabel=function () {
        //总共多少页
        $scope.pageLable=[];

        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        var firstPage = 1;  //开始页码
        var lastPage = $scope.totalPages;  //结束页码
        //总页数大于5时，计算页面标签逻辑
        if($scope.totalPages > 5){
            //如果当前页在前三页
            if($scope.pageNo <= 3){
                lastPage = 5;
                $scope.firstDot=false;//前面有点
            }else if($scope.pageNo >= $scope.totalPages - 3){  //如果当前页在后三页
                firstPage = $scope.totalPages - 4;
                $scope.lastDot=false;//后边有点
            }else{
                firstPage = $scope.pageNo - 2;
                lastPage = $scope.pageNo + 2;

                $scope.firstDot=true;//前面有点
                $scope.lastDot=true;//后边有点
            }
        }else{
            $scope.firstDot=false;//前面有点
            $scope.lastDot=false;//后边有点
        }

        //构建标签
        for(var i = firstPage; i <= lastPage; i++){
            $scope.pageLable.push(i);
        }
    }

    /**
     * 传入页码跳转
     * @param pageNo
     */
    $scope.queryByPage=function (pageNo) {
        pageNo = parseInt(pageNo);
        if(pageNo < 1 || pageNo > $scope.totalPages){
            alert("请输入正确页码！");
            return;
        }
        //修改当前页
        $scope.pageNo = pageNo;
        //刷新数据
        $scope.search();
    }
})