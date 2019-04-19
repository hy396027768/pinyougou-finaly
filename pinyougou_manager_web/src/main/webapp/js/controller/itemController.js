 //控制层 
app.controller('itemController' ,function($scope,$controller,itemService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

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