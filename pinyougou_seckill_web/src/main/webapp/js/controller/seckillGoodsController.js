//控制层
app.controller('seckillGoodsController' ,function($scope,$location,$interval,seckillGoodsService){
    //读取列表数据绑定到表单中
    $scope.findList=function(){
        seckillGoodsService.findList().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //查询实体
    $scope.findOne=function(){
        seckillGoodsService.findOne($location.search()['id']).success(
            function(response){
                $scope.entity= response;

                //获取当前时间的时间(毫秒)
                var now = new Date().getTime();
                //获取结束时间的时间(毫秒)
                var end = new Date(response.endTime).getTime();
                var allSecound = Math.floor((end - now) / 1000);

                //$interval(执行的函数,间隔的毫秒数,运行次数);
                timer = $interval(function () {
                    allSecound--;
                    if(allSecound < 1){
                        //停止计时器
                        $interval.cancel(timer);
                    }
                    //格式化输出秒数
                    $scope.allSecoundStr = convertTimeString(allSecound);
                },1000);
            }
        );
    }



    //把秒转换为 天小时分钟秒格式  XXX天 10:22:33
    convertTimeString=function(allsecond){
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小时数
        if(hours < 10){
            hours = "0" + hours;
        }
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        if(minutes < 10){
            minutes = "0" + minutes;
        }
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        if(seconds < 10){
            seconds = "0" + seconds;
        }
        var timeString="";
        if(days>0){
            timeString=days+"天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    }

    $scope.submitOrder=function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(function (response) {
            alert(response.message);
            if(response.success){
                window.location.href = "pay.html";
            }
        })
    }

});
