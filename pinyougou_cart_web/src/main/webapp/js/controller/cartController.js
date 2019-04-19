app.controller("cartController",function ($scope,cartService,$http) {

    //统计变量：{totalMoney:总金额,totalNum:总数量}
    $scope.totalValue={totalMoney:0,totalNum:0};

    //查询购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //先清空金额与总数
            $scope.totalValue={totalMoney:0,totalNum:0};
            //计算金额与数量
            for(var i = 0; i < response.length; i++){
                var item = response[i];
                for(var j = 0; j < item.orderItemList.length; j++){
                    $scope.totalValue.totalMoney += item.orderItemList[j].totalFee;
                    $scope.totalValue.totalNum += item.orderItemList[j].num;
                }
            }
        })
    }

    //操作购物车
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    }

    //查询当前用户的收件人列表
    $scope.findAddressList=function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;

            //识别默认收件人
            for(var i = 0; i < response.length; i++){
                if(response[i].isDefault == '1'){
                    $scope.address = response[i];
                    break;
                }
            }
        })
    }


    //用户选择收件人
    $scope.selectAddress=function (address) {
        //记录用户点击的地址
        $scope.address = address;
    }

    //订单对象
    $scope.order={paymentType:'1'};

    //修改支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType = type;
    }

    //提交订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//手机
        $scope.order.receiver = $scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(function (response) {
            alert(response.message);
            if(response.success){
                if($scope.order.paymentType == "1"){
                    window.location.href = "pay.html";
                }else {
                    window.location.href = "paysuccess.html";
                }
            }
        })
    }
    //我的关注
    $scope.cartCollect=function(id){
        cartService.addGoodsToCollect(id).success(function (response) {
            alert(response.message);
    })
      /*  $http.get("http://localhost:8086/user/collect.do?id="
            +id,{'withCredentials':true}).success(function (response) {
            alert(response.message);
        })*/
    }

})