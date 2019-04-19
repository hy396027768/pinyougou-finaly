 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId=$scope.parentId;//赋予上级ID
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	// $scope.reloadList();//重新加载

					$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象

	$scope.status=['未审核','已审核','驳回'];
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	/**
	 * //跟据父ID查询商品分类列表
	 * @param parentId
	 */

	$scope.parentId=0;

	$scope.findByParentId=function (parentId) {

		$scope.parentId=parentId;

		itemCatService.findByParentId(parentId).success(function (data) {

			$scope.list=data;
		})
	}

	//面包屑当前级别

	$scope.grade=1;

	//修改当前级别

	$scope.setGrade=function (value) {

		$scope.grade=value;
	}

	//面包屑记录逻辑
	//二级分类
	$scope.entity_1=null;
	//三级分类
	$scope.entity_2=null;

	/**
	 * 记录用户点击过的分类内容
	 * @param p_entity
	 */
	$scope.selectList=function (p_entity) {


		if($scope.grade==2){

			$scope.entity_1=p_entity;

			$scope.entity_2=null;

		}
		else if ($scope.grade == 3) {

			$scope.entity_2=p_entity;
		}


		else {

			$scope.entity_1=null;
			//三级分类
			$scope.entity_2=null;
		}

		//刷新数据列表

		this.findByParentId(p_entity.id)


	};


	$scope.updateStatusByItemCatId = function(ItemCatId,status){
		$scope.selectIds.push(ItemCatId);
		$scope.updateStatus(status);
	}

	//审核分类
	$scope.updateStatus=function (status) {

		itemCatService.updateStatus($scope.selectIds,status).success(function (response) {
			if(response.success){

				$scope.findByParentId($scope.parentId)
				//清空id列表
				$scope.selectIds = [];
			}else {
				alert(response.message);
			}
		})
	}






    
});	
