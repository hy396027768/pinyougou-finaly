app.controller("addressController",function ($scope,addressService) {
    //查询用户对应的地址
    $scope.findListByUserId = function () {
        addressService.findListByUserId().success(
            function (response) {
                $scope.addressList = response;
            }
        )
    }

    //修改地址的是否为默认值
    $scope.updateIsDefault = function (id) {
        addressService.updateIsDefault(id).success(
            function (response) {
                if (!response.success){
                    alert(response.message)
                }else{
                   //重新加载
                    $scope.findListByUserId();
                }
            }
        )
    };

    $scope.entity =  {};
    //设置地址备注
    $scope.setNotes  = function (notes) {
        $scope.entity.notes = notes;
    }
    //保存
    $scope.save=function(){

        //将省市区的数据封装到entity中
        $scope.entity.provinceId = window.addr['province'];
        $scope.entity.cityId = window.addr['city'];
        $scope.entity.townId = window.addr['areas'];


        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=addressService.update( $scope.entity ); //修改
        }else{
            serviceObject=addressService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findListByUserId();
                    //清空entity
                    $scope.entity = {};
                }else{
                    alert(response.message);
                }
            }
        );
    };
    //监听输入框内容值的变化
    // $scope.findAddressByQuery = function (query) {
    //     addressService.findAddressByQuery(newVlaue).success(
    //         function (response) {
    //             if (response.status == 0){
    //                 //查询成功
    //                 // $scope.AddressResult = response.result;
    //                 return response.result;
    //             }
    //         }
    //     )
    // }
    // $scope.$watch("entity.address",function (newVlaue,oldValue) {
    //     if (newVlaue!=null){
    //
    //         addressService.findAddressByQeury(newVlaue).success(
    //             function (response) {
    //                 response = JSON.parse(response);
    //                 response = JSON.parse(response);
    //                 if (response.status == 0){
    //                     //查询成功
    //                     $scope.AddressResult = response.result;
    //
    //                 }
    //             }
    //         );
    //     }
    //
    // });
    //
    // $scope.findAddressByQeury = function(query){
    //     addressService.findAddressByQeury(query).then(
    //                 function (response) {
    //                     $scope.result = response.data;
    //                     $scope.result = JSON.parse($scope.result);
    //                     $scope.result = JSON.parse($scope.result);
    //                     if ($scope.result.status == 0){
    //                         //查询成功
    //                         // $scope.AddressResult = response.result;
    //                         return $scope.result.result;
    //                     }
    //                 }
    //             );
    // }
    
    //删除指定地址
    $scope.deleteById =  function (id) {

        addressService.deleteById(id).success(
            function (response) {
                if (response.success){
                    //重新加载
                    $scope.findListByUserId();
                }else{
                    alert(response.message);
                }
            }
        )
    }
    /**
     * 查询指定id对应的地址信息
     * @param id
     */
    $scope.cityVal = "";
    $scope.findOne = function (id) {
        addressService.findOne(id).success(
            function (response) {
                $scope.entity = response.address;
                $scope.cityVal =response.provinces.province+","+response.cities.city+","+response.areas.area;

                window.updateAddress($scope.cityVal);
                // document.querySelector('#city-picker-selector').setCityVal($scope.cityVal);
            }
        )
    }

    $scope.cleanData = function () {
        alert("cleanData")
        window.updateAddress(' ');


    }
});