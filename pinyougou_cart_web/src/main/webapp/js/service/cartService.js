//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    //添加商品到购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    //获取地址列表
    this.findAddressList=function(){
        return $http.get('address/findListByUserId.do');
    }

    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }

    this.addGoodsToCollect=function (id) {
        return $http.get('http://localhost:8086/user/collect.do?id='+id)
    }
});
