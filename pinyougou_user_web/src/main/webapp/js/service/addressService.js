app.service("addressService",function ($http) {
//查询用户对应的地址
    this.findListByUserId = function () {
        return $http.get('../address/findListByUserId.do');
    }

    //修改地址的是否为默认值
    this.updateIsDefault = function (id) {
        return $http.get('../address/updateIsDefault.do?id='+id);
    }
    //增加
    this.add=function(entity){
        return  $http.post('../address/add.do?',entity );
    }
    //修改
    this.update=function(entity){
        return  $http.post('../address/update.do',entity );
    }

    //自动补全详细地址
    this.findAddressByQeury = function (query) {
        return $http.get('../address/findAddressByQeury.do?query='+query);
    }
    //删除
    this.deleteById = function (id) {
        return $http.get('../address/deleteById.do?id='+id);
    }
    //查询指定id对应的地址信息
    this.findOne = function (id) {
        return $http.get('../address/findOne.do?id='+id);
    }
});