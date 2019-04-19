app.controller("orderLineChartController", function ($scope,$filter, orderService) {

    $scope.startTime = $filter("date")(new Date(new Date().getTime() - 6 * 24 * 60 * 60 * 1000), "yyyy-MM-dd");
    $scope.endTime = $filter("date")(new Date(), "yyyy-MM-dd");

    $scope.findData = function () {
        orderService.findOrderLineChart($scope.startTime, $scope.endTime).success(
            function (response) {


                window.xAxisData = response.lineList;
                window.legendData = response.goodsNameList;

                var seriesData = [];

                for (var i = 0; i< response.goodsNameList.length ;i++) {

                    if (i == (response.goodsNameList.length-1)){

                        var series = {
                            name:'',
                            type:'line',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'top'
                                }
                            },
                            areaStyle: {normal: {}},
                            data:[]
                        };
                    }else{
                        var series = {
                            name:'',
                            type:'line',
                            stack: '总量',
                            areaStyle: {},
                            data:[]
                        };
                    }

                    var goodsname = response.goodsNameList[i];
                    series.name=goodsname;
                    series.data=response.seriesMap[goodsname];
                    seriesData.push(series);
                }
                window.seriesData = seriesData;

                var zhexian = echarts.init(document.getElementById('zhexiantu'));
                option = {
                    title: {
                        text: ''
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            label: {
                                backgroundColor: '#6a7985'
                            }
                        }
                    },
                    legend: {
                        data: window.legendData
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: [
                        {
                            type: 'category',
                            boundaryGap: false,
                            data: window.xAxisData
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value'
                        }
                    ],
                    series: window.seriesData
                };
                if (option && typeof option === "object") {
                    zhexian.setOption(option, true);
                }
            }
        )

    };
    //查询七天内
    $scope.findHebdomad = function () {
        $scope.startTime = $filter("date")(new Date(new Date().getTime() - 6*24 * 60 * 60 * 1000), "yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findData();
    }

    //查询一个月内
    $scope.findMonth = function () {
        $scope.startTime = $filter("date")(new Date(new Date().getTime() - 30*24 * 60 * 60 * 1000), "yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findData();
    }
    //查询三个月内
    $scope.findAll = function () {
        $scope.startTime = $filter("date")(new Date(new Date().getTime() - 3*30*24 * 60 * 60 * 1000),"yyyy-MM-dd");
        $scope.endTime = $filter("date")(new Date(),"yyyy-MM-dd");
        $scope.findData();
    }
    //自定义时间
    $scope.searchDefined = function () {
        if ($scope.startTime == null || $scope.endTime == null ){
            alert("请选择对应的时间")
        }
        $scope.findData();
    }
});