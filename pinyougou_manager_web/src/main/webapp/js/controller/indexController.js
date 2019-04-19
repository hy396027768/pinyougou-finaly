app.controller("indexController",function ($scope,$filter,orderService,loginService) {

    $scope.loginName=function () {
        loginService.loginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }

    $scope.startTime = $filter("date")(new Date(new Date().getTime() - 6*30*24 * 60 * 60 * 1000), "yyyy-MM-dd");
    $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
    $scope.sellerName = null;
    //查询指定时间内的状态,默认查询七天内的
    $scope.findOrderService=function () {
        orderService.findOrderService($scope.sellerName,$scope.startTime,$scope.endTime).success(
            function (response) {
                $scope.list = response;
                //销售额
                $scope.saleRooms =0;
                for (var i = 0;i<$scope.list.length;i++){
                    $scope.saleRooms = $scope.saleRooms + $scope.list[i].saleRoom;
                }
                //封装饼状图数据
                var bingzhuangData = [];
                for (var i = 0;i<$scope.list.length;i++){
                    var data = {name:'',value:''};
                    data.name = $scope.list[i].goodsName;
                    data.value = $scope.list[i].saleRoom;
                    bingzhuangData.push(data);
                }
                var bingzhuang = echarts.init(document.getElementById('bingzhuangtu'));
                option = null;
                option = {
                    backgroundColor: 'transparent',

                    title: {
                        //标题名字
                        text: '订单统计饼状图',
                        left: '160',
                        top: 20,
                        textStyle: {
                            color: 'black'
                        }
                    },

                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },

                    visualMap: {
                        show: false,
                        min: 80,
                        max: 600,
                        inRange: {
                            colorLightness: [0, 1]
                        }
                    },
                    series : [
                        {
                            name:'商品名称',
                            type:'pie',
                            //设置大小与xy轴位置
                            radius : '40%',
                            center: ['40%', '50%'],
                            data:bingzhuangData.sort(function (a, b) { return a.value - b.value; }),
                            roseType: 'radius',
                            label: {
                                normal: {
                                    textStyle: {
                                        color: 'red'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    lineStyle: {
                                        color: 'green'
                                    },
                                    smooth: 0.8,
                                    length: 70,
                                    length2: 20
                                }
                            },
                            itemStyle: {
                                normal: {
                                    color: 'yellow',
                                    shadowBlur: 200,
                                    shadowColor: 'red'
                                    //    rgba(23, 123, 12, 0.5)
                                }
                            },

                            animationType: 'scale',
                            animationEasing: 'elasticOut',
                            animationDelay: function (idx) {
                                return Math.random() * 200;
                            }
                        }
                    ]
                };;
                if (option && typeof option === "object") {
                    bingzhuang.setOption(option, true);
                }
            }
        )
    };
})