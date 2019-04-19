//服务层
app.service('userService',function($http){
	//显示收藏
    this.findListByUserId=function () {

        return $http.get("../user/findListByContect.do");

    }
    //取消收藏
    this.cancleCollect=function (id) {
		return $http.get("user/cancleCollect.do?id="+id);
    }
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../user/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../user/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity,code){
		return  $http.post('../user/add.do?code='+code,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../user/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../user/search.do?page='+page+"&rows="+rows, searchEntity);
	}

    //发送验证码
    this.sendCode=function(phone){
        return $http.get("../user/sendCode.do?phone="+phone);
    }

    //修改个人信息
    this.updateUserMessage=function (entity) {
		return $http.post("../user/updateUserMessage.do?",entity);
	}

	//查找所有职业
	this.findJob=function () {
		return $http.get("../user/findJob.do");
	}

	// this.findAddressId=function (province,city,areas) {
	// 	return $http.get("../user/findAddressId.do?province="+province+"&city="+city+"&areas="+areas);
	// }
	this.findOneAddress=function () {
		return $http.get("../user/findOneAddress.do")
	}


    //查询足迹列表

	this.findListByUserId=function () {

		return $http.get("../user/findListByUserId.do");

	}

});
