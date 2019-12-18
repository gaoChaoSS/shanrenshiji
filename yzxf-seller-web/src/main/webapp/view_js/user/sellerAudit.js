/**
 * Created by tianchangsen on 17/1/24.
 */

(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = model;
        $scope.entity = entity;
        $scope.pageSize = 10;
        $scope.pageNo = 1;
        $rootScope.getFixed($scope.model, $scope.entity);

        //修改列表
        $scope.modifyList=[];
        //选择所有
        $scope.checkedAll=false;
        //修改模式
        $scope.modifyCheck=false;

        $scope.status=0;

        $scope.canUseCheck=function(canUse){
            if(canUse){
                return "通过";
            }
            return "未通过";
        }

        //选择修改的数据
        $scope.modifyCheckedFun=function(selectIndex){
            if($scope.modifyCheck){
                if(typeof(selectIndex)=="number"){
                    if($scope.modifyList.length<1){
                        malert("获取列表失败!");
                        return;
                    }
                    $scope.modifyList[selectIndex].check=!$scope.modifyList[selectIndex].check;
                }else{
                    for(var index=0;index<$scope.orderList.length;index++){
                        $scope.modifyList[index].check=selectIndex;
                    }
                }
            }else{
                goPage("#/seller/sellerInfo?sellerId="+$scope.modifyList[selectIndex]._id);
            }
        }
        //生成修改列表
        $scope.getModifyList=function(){
            //是否删除状态
            $scope.modifyDel=false;
            $scope.modifyList=[];
            for(var index=0;index<$scope.orderList.length;index++){
                $scope.modifyList.push({
                    _id:$scope.orderList[index]._id,
                    check:false,
                    modifyCanUse:$scope.orderList[index].canUse
                });
            }
        }
        //点击'审核通过'或'审核不通过'按钮,临时修改页面数据
        $scope.modifyCanUseFun=function(check){
            for(var index=0;index<$scope.orderList.length;index++) {
                if($scope.modifyList[index].check){
                    $scope.modifyList[index].modifyCanUse = check;
                }
            }
        }
        //提交审核数据
        $scope.submitModifyList=function(){
            var idStr="";
            var canUseStr="";
            for(var index=0;index<$scope.modifyList.length;index++){
                if($scope.modifyList[index].modifyCanUse!=$scope.orderList[index].canUse){
                    idStr+=$scope.modifyList[index]._id+",";
                    canUseStr+=$scope.modifyList[index].modifyCanUse+",";
                }
            }
            if(idStr.length>1){
                idStr=idStr.toString().substring(0,idStr.length-1);
                canUseStr=canUseStr.toString().substring(0,canUseStr.length-1);
            }else{
                malert("未做任何改动");
                return;
            }

            var url = window.basePath + '/account/Seller/updateSellerCanUse';
            var date = {
                idStr:idStr,
                canUseStr:canUseStr
            };
            $http.post(url, date).success(function () {
                malert("保存成功!");
                $scope.modifyCheck=false;
                $scope.checkedAll=false;
                $scope.querySeller();
            });
        }
        //取消
        $scope.cancelBtn=function(){
            $scope.modifyCheck=false;
            $scope.checkedAll=false;
            $scope.getModifyList();
        }

        $scope.querySeller = function (p) {
            if (p != null) {
                $scope.pageNo = p;
            }
            var url = window.basePath + "/account/Seller/queryAllSeller?pageSize=" + $scope.pageSize + "&pageNo=" + $scope.pageNo;
            if ($scope.name != null) {
                url += "&name=" + $scope.name;
            }
            if ($scope.status != null) {
                url += "&status=" + $scope.status;
            }
            $http.get(url).success(function (re) {
                $scope.orderList = re.content.sellerList;
                $scope.totalPage = re.content.totalPage;
                $scope.sellerPage = [];

                $scope.totalNumber = re.content.totalCount;
                //页码集合
                $scope.pageList = [];
                //当前显示的页码从第几页开始
                var listCur = 1;
                var listCurCount = 5;
                if ($scope.pageNo <= 5 && $scope.totalPage <= 5) {
                    listCur = 1;
                    listCurCount = $scope.totalPage;
                } else {
                    listCur = $scope.pageNo - 2;
                    if (listCur <= 1) {
                        listCur = 1;
                    } else if (($scope.pageNo > $scope.totalPage - 3 && listCur >= 4) || $scope.totalPage == 6) {
                        listCur = $scope.totalPage - 4;
                    }
                }
                for (var index = 0; index < listCurCount; index++, listCur++) {
                    $scope.pageList.push({num: listCur});
                }
                //选中的是第几个页码
                $scope.pageIndex = $scope.pageNo;
                //是否显示最后一页的页码
                $scope.isLastPage = ($scope.pageIndex < $scope.totalPage - 2) && $scope.totalPage > 5;
                //是否显示第一页的页码
                $scope.isFirstPage = $scope.pageIndex >= 4 && $scope.totalPage > 5;

                $scope.isNullPage = ($scope.totalPage < 1) || ($scope.totalPage == null);

                $scope.getModifyList();
            });
        }
        $scope.pageNext = function (num) {
            if ($scope.pageNo + num < 1 || $scope.totalPage < $scope.pageNo + num) {
                return;
            }
            $scope.pageNo += num;
            $scope.querySeller();
        }

        //跳转页码
        $scope.pageNumber = function (num) {
            if (num < 1 || $scope.totalPage < num) {
                return;
            }
            $scope.pageNo = num;
            $scope.querySeller();
        }

        $scope.pageCur = function (index) {
            $scope.pageIndex = index;
        }

        $scope.querySeller();

    });
})(angular);
