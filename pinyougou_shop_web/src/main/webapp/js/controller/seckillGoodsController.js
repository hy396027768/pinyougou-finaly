//控制层
app.controller('seckillGoodsController', function ($scope, $controller, $location, seckillGoodsService, uploadService) {

    $controller('baseController', {$scope: $scope});//继承
    $scope.entity = {goodsId: "", smallPic: "", introduction: "",startTime:"",endTime:""};
    $scope.entity.goodsId = $location.search()['id'];//获取参数值
    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        seckillGoodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        seckillGoodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //保存
    $scope.save = function () {
        //先获取商品描述
        $scope.entity.introduction = editor.html();
        $scope.entity.startTime;
        $scope.entity.endTime;
        seckillGoodsService.add($scope.entity).success(
            function (response) {
                alert(response.message);
                if (response.success) {
                    //清空信息
                    /*$scope.entity = {};
                 editor.html("");*/
                    window.location.href = "goods.html";
                }
            }
        );
    }

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        seckillGoodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        seckillGoodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.image_entity = {url: ''};
    //上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {

                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }
        })
    }

    //追加图片
    $scope.add_image_entity = function () {
        $scope.entity.smallPic = $scope.image_entity.url;
    }
    //删除图片
    $scope.delete_image_entity = function (index) {
        $scope.entity.smallPic = "";
    }

});
