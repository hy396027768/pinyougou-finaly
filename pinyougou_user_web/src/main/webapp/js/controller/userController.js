 //控制层 
app.controller('userController' ,function($scope,userService){
	//展示收藏
    $scope.findListByUserId=function () {
        userService.findListByUserId().success(function (response) {
			$scope.contectList=response;
        })
    }
    //取消收藏
    $scope.cancleCollect=function(id){
    	userService.cancleCollect(id).success(function (response) {
            alert(response.message);
    		if(response.success){
    			window.location.href="http://localhost:8086/home-person-collect.html"
			}
			})
	}
    //读取列表数据绑定到表单中
	$scope.findAll=function(){
		userService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}

	//分页
	$scope.findPage=function(page,rows){
		userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userService.update( $scope.entity ); //修改  
		}else{
			serviceObject=userService.add( $scope.entity  );//增加 
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

	$scope.entity={password:'',phone:''};
	$scope.password = '';
    $scope.code = '';

	//用户注册
    $scope.reg=function(){
		//验证密码
		if($scope.entity.password == ''){
            alert("请先输入密码!");
            return;
		}
		if($scope.entity.password != $scope.password){
            alert("两次密码输入不正确!");
            return;
		}
        if($scope.code == ''){
            alert("请先输入验证码!");
            return;
        }
        userService.add( $scope.entity,$scope.code).success(
            function(response){
                alert(response.message);
            }
        );
    }
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		userService.dele( $scope.selectIds ).success(
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
		userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//发送验证码
	$scope.sendCode=function () {
		if($scope.entity.phone == ''){
            alert("请先输入手机号");
            return;
		}
		userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }


    $scope.updateUserMessage=function () {
		// window.func('c','fas',"casd");
		if ($scope.entity.month<10){
			$scope.entity.month = '0'+$scope.entity.month
		}

		if ($scope.entity.day<10){
			$scope.entity.day = '0'+$scope.entity.day
		}
		// $scope.findAddressId();
		// alert(JSON.stringify($scope.entity));

		$scope.entity.provinceid=window.addr.province;
		$scope.entity.citiesid=window.addr.city;
		$scope.entity.areasid=window.addr.areas;



		$scope.entity.birthday = $scope.entity.year+"-"+$scope.entity.month+"-"+$scope.entity.day;
		userService.updateUserMessage($scope.entity).success(function (response) {
			if (response.success) {
				alert(response.message);
				$scope.entity={}
			}else {
				alert(response.message);
			}
		})
	}


	$scope.findJob=function () {
		userService.findJob().success(function (response) {
			$scope.jobList=response;
		})
	}


	// $scope.findAddressId=function () {
	// 	// alert(window.addr.province+window.addr.city+window.addr.areas)
	// 	userService.findAddressId(window.addr.province,window.addr.city,window.addr.areas).success(function (response) {
	// 		alert(JSON.stringify(response))
	// 		$scope.entity=response;
	// 	})
	// }

	$scope.jobArr=['程序员','农民工','杨联铭','何庆鸿','张天飞','安宁','刘晓宁','姜琰','黄春辉','邱东秋','杨火昌','斗地主','学生','司机','演员','人才','996',
	]


	$scope.findOneAddress=function () {
		userService.findOneAddress().success(function (response) {
			//数据回显的方法
			$scope.entity=response;
			//将生日通过-分割   数据库日期的格式为yyyy-MM-dd
			var strings = $scope.entity.birthday.split("-");
			//获取截取过后的月份
			var months = strings[1];
			//获取截取过后的天数
			var daysAndTime = strings[2];
			var days = daysAndTime[0]+''+daysAndTime[1];


					//设置entity.year 年份
					$scope.entity.year = strings[0];

					//如果月份小于10 应该将月份前面的0删掉，否则birthday.js不支持
					if (months < 10) {
						var month = months.split('');
						$scope.entity.month = month[1];
					}
					if (months > 10) {
						$scope.entity.month = months;
					}

					//如果天数小于10 应该将天数前面的0删掉，否则birthday.js不支持
					if (days < 10) {
						var day = days.split('');
						$scope.entity.day = day[1]
					}
					if (days > 10) {
						$scope.entity.day = days;
					}

					window.findOneAddress($scope.entity.provinceid,$scope.entity.citiesid,$scope.entity.areasid,$scope);
		})
	}







    //查询足迹

    $scope.findListByUserId=function () {

		userService.findListByUserId().success(function (response) {

			$scope.collects=response;

		})

	}


});
