app.service("userService", function ($http) {


    //分页查询 + 搜索查询
    this.search = function (page, rows, searchEntity) {

        return $http.post("../user/search.do?page=" + page + "&rows=" + rows, searchEntity);
    };

    //修改用户状态
    this.setStatus = function (id, status) {
        return $http.get("../user/setStatus.do?id=" + id + "&status=" + status);
    };

    //根据ID查询用户
    this.findOne = function (id) {
        return $http.get("../user/findOne.do?id=" + id);
    };

    //性别统计
    this.sexStatistics = function () {
        return $http.get("../user/sexStatistics.do");
    };

    //基本数据统计
    this.basic = function () {
        return $http.get("../user/basic.do");

    };

    //下载数据
    this.download = function () {
        return $http.post("../user/download.do");
    };

});