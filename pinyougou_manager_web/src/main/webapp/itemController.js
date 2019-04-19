app.controller("itemController",function ($scope) {
    $scope.num = 1;
    
    $scope.addNum=function (x) {
        $scope.num = x;
        if($scope.num < 1){
            $scope.num = 1;
        }
    }

    //记录用户选择的规格
    $scope.specificationItems={};
    /**
     * 用户勾选规格
     * @param specName 规格名称
     * @param optionName 选项名称
     */
    $scope.selectSpecification=function (specName,optionName) {
        $scope.specificationItems[specName] = optionName;
        //更新sku信息
        searchSku();
    }

    /**
     * 识别是否要勾选规格
     * @param specName
     * @param optionName
     */
    $scope.isSelected=function (specName,optionName) {
        return $scope.specificationItems[specName] == optionName;
    }

    //加载默认的sku信息
    $scope.loadSku=function () {
        //后续不操作sku的属性
        $scope.sku = skuList[0];
        //深克隆绑定默认sku勾中的规格
        $scope.specificationItems=JSON.parse(JSON.stringify(skuList[0])).spec;
    }

    //匹配两个对象
    matchObject=function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }

    //查询SKU
    searchSku=function(){
        for(var i=0;i<skuList.length;i++ ){
            if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
                $scope.sku=skuList[i];
                return ;
            }
        }
        $scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
    }

    //添加商品到购物车
    $scope.addToCart=function(){
        alert('skuid:'+$scope.sku.id);
    }



});