app.service("loginService",function ($http) {
    //获取登录用户的信息
    this.showUserInfo=function () {
        return $http.get("login/userInfo.do");
    }
})