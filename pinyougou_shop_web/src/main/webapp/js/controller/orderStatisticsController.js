 //控制层 
app.controller('orderStatisticsController' ,function($scope,$filter,$controller,orderService){
	
	//$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		orderService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		orderService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=orderService.update( $scope.entity ); //修改  
		}else{
			serviceObject=orderService.add( $scope.entity  );//增加 
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
		orderService.dele( $scope.selectIds ).success(
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
		orderService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.startTime = $filter("date")(new Date(new Date().getTime() - 6*24 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm:ss");
    $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd HH:mm:ss");
    //查询指定时间内的状态,默认查询七天内的
    $scope.findOrderService=function () {
        orderService.findOrderService($scope.startTime,$scope.endTime).success(
            function (response) {
                $scope.list = response;
                $scope.saleRooms =0;
                for (var i = 0;i<$scope.list.length;i++){
                    $scope.saleRooms = $scope.saleRooms + $scope.list[i].saleRoom;
                }
                //刷新界面
                // location.reload();
            }
        )
    }

    //查询七天内
	$scope.findHebdomad = function () {
        $scope.startTime = $filter("date")(new Date(new Date().getTime() - 6*24 * 60 * 60 * 1000), "yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findOrderService();
    }

    //查询一个月内
    $scope.findMonth = function () {
        $scope.startTime = $filter("date")(new Date(new Date().getTime() - 30*24 * 60 * 60 * 1000), "yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findOrderService();
    }
    //查询全部
    $scope.findAll = function () {
        $scope.startTime = $filter("date")(new Date(0),"yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findOrderService();
    }
    //自定义时间
    $scope.searchDefined = function () {
    	if ($scope.startTime == null || $scope.endTime == null ){
    		alert("请选择对应的时间")
		}
        // $scope.startTime = $filter("date")($scope.searchEntity.startTime,"yyyy-MM-dd HH:mm:ss");
        // $scope.endTime = $filter("date")($scope.searchEntity.endTime,"yyyy-MM-dd HH:mm:ss");
        $scope.findOrderService();
    }
    
});	
