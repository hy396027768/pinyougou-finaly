//控制层
app.controller('userController', function ($scope, $controller, userService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.searchEntity = {};

    //用户状态
    $scope.status = ['已冻结', '正常'];
    //用户来源
    $scope.source = ['PC', 'IOS', 'WeChat'];

    //会员等级
    $scope.grade = ['土豪', '地主', '农民', '乞丐'];

    //分页查询所有用户
    $scope.search = function (page, rows) {

        userService.search(page, rows, $scope.searchEntity).success(function (result) {

            //当前页结果
            $scope.list = result.rows;
            //总记录数
            $scope.paginationConf.totalItems = result.total;

        }).error(function () {
            alert("请求超时");
        });
    };

    //点击处理用户状态
    $scope.setStatus = function (id, status) {

        userService.setStatus(id, status).success(function (result) {

            alert(result.message);
            $scope.reloadList();

        }).error(function () {

            alert("请求超时");
        });
    };

    //用户详情回显
    $scope.findOne = function (id) {

        userService.findOne(id).success(function (result) {

            $scope.entity = result;
        }).error(function () {
            alert("请求超时");
        });
    };


    //性别统计
    $scope.sexStatistics = function () {
        userService.sexStatistics().success(function (result) {
            $scope.sexMap = result;
        }).error(function () {
            alert("请求超时");
        });
    };
    $scope.values = [];

    //基本数据统计
    $scope.basic = function () {

        userService.basic().success(
            function (result) {

                $scope.basicMap = result;

                //获取总数
                $scope.count = result.count[0];

                //获取状态
                for (var i = 0; i < result.status.length; i++) {
                    //正常状态
                    if (result.status[i].status == "1") {
                        $scope.status1 = result.status[i].calculate
                    } else {
                        $scope.status0 = result.status[i].calculate
                    }
                }

                //获取性别
                for (var i = 0; i < result.sex.length; i++) {
                    //正常状态
                    if (result.sex[i].sex == "1") {
                        $scope.sex1 = result.sex[i].calculate
                    } else {
                        $scope.sex2 = result.sex[i].calculate
                    }
                }

                //获取会员等级
                $scope.userLevelList = result.userLevel;






                var level = echarts.init(document.getElementById("level"));
                option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}: {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        x: 'left',


                        data: ['土豪', '地主', '农民', '乞丐']

                    },
                    series: [
                        {
                            name: '访问来源',
                            type: 'pie',
                            radius: ['30%', '60%'],
                            avoidLabelOverlap: false,
                            label: {
                                normal: {
                                    show: false,
                                    position: 'center'
                                },
                                emphasis: {
                                    show: true,
                                    textStyle: {
                                        fontSize: '30',
                                        fontWeight: 'bold'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: false
                                }
                            },
                            data: [
                                {value:7, name:'土豪'},
                                {value:2, name:'地主'},
                                {value:4, name:'农民'},
                                {value:5, name:'乞丐'},
                            ]

                        }
                    ]
                };
                if (option && typeof option === "object") {
                    level.setOption(option, true)
                };



                var sexbing = echarts.init(document.getElementById("sex"));
                option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}: {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        x: 'left',
                        data: ['男', '女']
                    },
                    series: [
                        {
                            name: '访问来源',
                            type: 'pie',
                            radius: ['30%', '60%'],
                            avoidLabelOverlap: false,
                            label: {
                                normal: {
                                    show: false,
                                    position: 'center'
                                },
                                emphasis: {
                                    show: true,
                                    textStyle: {
                                        fontSize: '30',
                                        fontWeight: 'bold'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: false
                                }
                            },
                            data: [
                                {value: $scope.sex1, name: '男'},
                                {value: $scope.sex2, name: '女'},
                            ]
                        }
                    ]
                };

                if (option && typeof option === "object") {
                    sexbing.setOption(option, true)
                };

                var status = echarts.init(document.getElementById("status"));
                option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}: {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        x: 'left',
                        data: ['正常', '已冻结']
                    },
                    series: [
                        {
                            name: '访问来源',
                            type: 'pie',
                            radius: ['30%', '60%'],
                            avoidLabelOverlap: false,
                            label: {
                                normal: {
                                    show: false,
                                    position: 'center'
                                },
                                emphasis: {
                                    show: true,
                                    textStyle: {
                                        fontSize: '30',
                                        fontWeight: 'bold'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: false
                                }
                            },
                            data: [
                                {value: $scope.status1, name: '正常'},
                                {value: $scope.status0, name: '已冻结'},
                            ]
                        }
                    ]
                };

                if (option && typeof option === "object") {
                    status.setOption(option, true)
                };

            }).error(function () {
            alert("请求超时");
        });
    };


    //数据下载
    $scope.download = function () {


        userService.download().success(function (resutl) {

            alert(resutl.message);


        }).error(function () {
            alert("请求超时");
        });
    };


});
