app.controller("payController",function ($scope,$location,payService) {

    //生成二维码
    $scope.createNative=function () {
        payService.createNative().success(function (response) {
            //订单号
            $scope.out_trade_no = response.out_trade_no;
            //金额
            $scope.money = (response.total_fee / 100).toFixed(2);

            var qr = window.qr = new QRious({
                element: document.getElementById('qrious'),
                size: 260,
                value: response.code_url,
                level: 'Q'
            });

            //检测支付状态
            payService.queryPayStatus(response.out_trade_no).success(function (response) {
                if(response.success){
                    window.location.href = "paysuccess.html#?money=" + $scope.money;
                }else{
                    if("支付已超时" == response.message){
                        window.location.href = "paytimeout.html";
                    }else{
                        window.location.href = "payfail.html";
                    }
                }
            });
        })
    }

    //加载金额参数
    $scope.loadMoney=function () {
        $scope.money = $location.search()["money"];
    }
})