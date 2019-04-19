//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService,
                                            itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()["id"];
        if (id != null) {
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    //设置描述
                    editor.html(response.goodsDesc.introduction);

                    //把商品图片信息转换
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    //把扩展属性信息转换
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                    //把规格信息转换
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                    //把sku列表的规格转换
                    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                        $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                    }

                }
            );
        }
    }

    //保存
    $scope.save = function () {
        //先获取商品描述
        $scope.entity.goodsDesc.introduction = editor.html();

		var serviceObject;//服务层对象 			
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
                alert(response.message);
                if (response.success) {
                    //清空信息
                    /*$scope.entity = {};
                 editor.html("");*/
                    window.location.href = "goods.html";
                }
            }
        );
    }

    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.image_entity = {url: ''};

    //上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message);
            }
        })
    }

    //定义页面商品实体结构
    //{goods:{基本信息},goodsDesc:{itemImages:[图片列表],specificationItems:[用户勾选的规格列表]}};
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};

	//追加图片
	$scope.add_image_entity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片
    $scope.delete_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //查询商品一级分类列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    }

    //二级分类加载
    //$watch(监听变量名称,function(newVlaue,oldValue))
    $scope.$watch("entity.goods.category1Id", function (newVlaue, oldValue) {
        itemCatService.findByParentId(newVlaue).success(function (response) {
            $scope.itemCat2List = response;
        });
    });

    //三级分类加载
    //$watch(监听变量名称,function(newVlaue,oldValue))
    $scope.$watch("entity.goods.category2Id", function (newVlaue, oldValue) {
        itemCatService.findByParentId(newVlaue).success(function (response) {
            $scope.itemCat3List = response;
        });
    });

    //模板ID加载
    //$watch(监听变量名称,function(newVlaue,oldValue))
    $scope.$watch("entity.goods.category3Id", function (newVlaue, oldValue) {
        itemCatService.findOne(newVlaue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });

    //加载品牌列表、扩展属性列表、规格列表
    //$watch(监听变量名称,function(newVlaue,oldValue))
    $scope.$watch("entity.goods.typeTemplateId", function (newVlaue, oldValue) {

        var id = $location.search()["id"];
        typeTemplateService.findOne(newVlaue).success(function (response) {
            //转换品牌列表
            response.brandIds = JSON.parse(response.brandIds);
            $scope.typeTemplate = response;

            if (id == null) {
                //转换扩展属性列表
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
            //查询规格与选项列表
            typeTemplateService.findSpecList(newVlaue).success(function (response) {
                $scope.specList = response;
            })
        })
    });

    /**
     * 规格页，checkbox点击事件
     * @param $event checkbox本身
     * @param specName 规格名称
     * @param optionName 选项名称
     */
    $scope.updateSpecAttribute = function ($event, specName, optionName) {
        //查询用户有没有勾选过当前规格
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        //如果是第一次勾中规格
        if (obj == null) {
            $scope.entity.goodsDesc.specificationItems.push({
                'attributeName': specName,
                'attributeValue': [optionName]
            });
        } else {
            //选中了
            if ($event.target.checked) {
                obj.attributeValue.push(optionName);
            } else {
                //取消勾选，删除元素
                var index = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(index, 1);

                //如果取消勾选后，当前规格已经没有内容
                if (obj.attributeValue.length < 1) {
                    //删除当前规格对象
                    var indexObj = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(indexObj, 1);
				}
			}
		}

        //刷新sku列表
        $scope.createItemList();
    }

    // 1.创建$scope.createItemList方法，同时创建一条有基本数据，不带规格的初始数据
    $scope.createItemList = function () {
        // 参考: $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }]
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
        // 2.查找遍历所有已选择的规格列表，后续会重复使用它，所以我们可以抽取出个变量items
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            // 9.回到createItemList方法中，在循环中调用addColumn方法，并让itemList重新指向返回结果;
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }

    // 3.抽取addColumn(当前的表格，列名称，列的值列表)方法，用于每次循环时追加列
    /**
     * 动态追加表行记录
     * @param list 当前表格
     * @param specName 列的名称-规格名称
     * @param optionName 列的值-选项名称列表
     * @return 最新表格
     */
    addColumn = function (list, specName, optionName) {
        // 4.编写addColumn逻辑，当前方法要返回添加所有列后的表格，定义新表格变量newList
        var newList = [];
        // 5.在addColumn添加两重嵌套循环，一重遍历之前表格的列表，二重遍历新列值列表
		for(var i = 0; i < list.length; i++){
			for(var j = 0; j < optionName.length; j++){
                // 6.在第二重循环中，使用深克隆技巧，把之前表格的一行记录copy所有属性，
				// 用到var newRow = JSON.parse(JSON.stringify(之前表格的一行记录));
                var newRow = JSON.parse(JSON.stringify(list[i]));
                // 7.接着第6步，向newRow里追加一列
				newRow.spec[specName] = optionName[j];
                // 8.把新生成的行记录，push到newList中
                newList.push(newRow);
            }
        }
        return newList;
    }

    //商品状态
    $scope.status=['未审核','已审核','审核未通过','关闭'];

    //商品分类列表，数据存储时，id作为下标，名称作为值
    $scope.itemCatList=[];
    $scope.findItemCatList=function () {
		itemCatService.findAll().success(function (response) {
			for(var i = 0; i < response.length; i++){
                $scope.itemCatList[response[i].id] = response[i].name;
			}
        })
    }

    /**
     * 规格页面中-checkbox是否是勾中
     * @param specName 规格名称
     * @param optionName 选项名称
     */
    $scope.checkAttributeValue=function (specName,optionName) {
    	//用户已经勾选的规格信息
		var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(items,'attributeName',specName);
        if(obj != null){
        	//检查选项是否存在
			if(obj.attributeValue.indexOf(optionName) > -1){
				return true;
			}
		}
        return false;
    };

    /***
     * 上架下架处理
     */
    $scope.shopUpAndDown = function (id, isMarketable,auditStatus) {

        $scope.entity = {"goods": {"id": id, "isMarketable": isMarketable ,"auditStatus":auditStatus}};

        goodsService.shopUpAndDown($scope.entity).success(function (result) {

            alert(result.message);

            $scope.reloadList();

        }).error(function () {
            alert("请求超时");
        });


    }


    //审核商品
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(function (response) {
            if(response.success){
                $scope.reloadList();
                //清空id列表
                $scope.selectIds = [];
            }else {
                alert(response.message);
            }
        })
    }
});

