app.service("orderService",function ($http) {
    this.search = function (pageNum,pageSize,order) {
        return $http.post("../order/findAllByUserId.do?pageNum=" + pageNum + "&pageSize=" + pageSize, order);
    }
})