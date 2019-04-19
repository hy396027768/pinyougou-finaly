app.controller("searchController",function ($scope,$location,searchService) {

    /**
     * 搜索对象
     * @type {{keywords: 关键字, category: 商品分类, brand: 品牌, spec: {'网络'：'移动4G','机身内存':'64G'},
     * price:价格区间,'pageNo':当前页,'pageSize':每页查询的记录数,'sortField':排序的域,'sort':排序的方式 }}
     */
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':"",
        'pageNo':1,'pageSize':40,'sortField':'','sort':'' };


    //查询商品
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            //刷新分页标签
            buildPageLabel();
        })
    }

    //构建分页标签
    buildPageLabel=function () {
        //总共多少页
        $scope.pageLable=[];

        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        var firstPage = 1;  //开始页码
        var lastPage = $scope.resultMap.totalPages;  //结束页码
        //总页数大于5时，计算页面标签逻辑
        if($scope.resultMap.totalPages > 5){
            //如果当前页在前三页
            if($scope.searchMap.pageNo <= 3){
                lastPage = 5;
                $scope.firstDot=false;//前面有点
            }else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 3){  //如果当前页在后三页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot=false;//后边有点
            }else{
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

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
        if(pageNo < 1 || pageNo > $scope.resultMap.totalPages){
            alert("请输入正确页码！");
            return;
        }
        //修改当前页
        $scope.searchMap.pageNo = pageNo;
        //刷新数据
        $scope.search();
    }

    /**
     * 排序查询
     * @param sortField 排序的域名
     * @param sort 排序的方式asc|desc
     */
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        //刷新数据
        $scope.search();
    }

    /**
     * 查询条件组装
     * @param key 操作的属性
     * @param value 传入的值
     */
    $scope.addSearchItem=function (key,value) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        //刷新数据
        $scope.search();
    }

    /**
     * 查询条件组装
     * @param key 操作的属性
     * @param value 传入的值
     */
    $scope.deleteSearchItem=function (key) {
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }
        //刷新数据
        $scope.search();
    }

    /**
     * 识别关键字是否包含品牌
     */
    $scope.keywordsIsBrand=function () {
        for(var i = 0; i < $scope.resultMap.brandIds.length; i++){
            //如果找到
            if($scope.resultMap.brandIds[i].text == $scope.searchMap.keywords){
                return true;
            }
        }
        return false;
    }

    /**
     * 对接其它页面传入的搜索条件
     */
    $scope.loadKeywords = function () {
        var keywords = $location.search()["keywords"];
        $scope.searchMap.keywords = keywords;

        $scope.search();
    }
})