app.controller("indexController",function ($scope,contentService) {

    $scope.contentList=[];//广告集
    $scope.contentList2=[];//广告集
    $scope.contentList3=[];//广告集
    $scope.contentList4=[];//广告集
    $scope.contentList5=[];//服装楼层广告
    $scope.contentList6=[];//广告
    $scope.contentList7=[];//服装
    $scope.CatList =[];//顶级分类列表
    //查询广告列表
    $scope.findByCategoryId=function () {
        //查询轮播图
        contentService.findByCategoryId(1).success(function (response) {
            $scope.contentList[1] = response;

        });
        contentService.findByCategoryId(2).success(function (response) {
            $scope.contentList[2] = response;

        });
        contentService.findByCategoryId(3).success(function (response) {
            $scope.contentList[3] = response;

        });
        contentService.findByCategoryId(4).success(function (response) {
            $scope.contentList[4] = response;

        });
        contentService.findByCategoryId(5).success(function (response) {
            $scope.contentList[5] = response;

        });
        contentService.findByCategoryId(6).success(function (response) {
            $scope.contentList[6] = response;

        });
        contentService.findByCategoryId(7).success(function (response) {
            $scope.contentList[7] = response;

        });
    }
    $scope.findCatList=function(){
        contentService.findCatList().success(function (response) {
            var array = new Array();
            for (var i = 0; i <15 ; i++) {
                array.push(response[i])
            }
                $scope.catList=array;
        })
    }

    $scope.keywords = "";

    $scope.search=function () {
        if($scope.keywords == ""){
            alert("请输入搜索关键字");
            return;
        }
        window.location.href = "http://localhost:8084/search.html#?keywords=" + $scope.keywords;
    }
})