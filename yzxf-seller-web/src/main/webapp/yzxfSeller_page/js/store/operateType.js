(function (angular, undefined) {

    var model = 'store';
    var entity = 'operateType';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        var init = function(){
            //地区ID
            $scope.operateId=null;
            //经营名字
            $scope.operateName=null;
            $scope.firstList=null;
            $scope.secondList=null;
            $scope.thirdList=null;

            $scope.operateValue=null;
            $scope.operatePValue=null;
        }
        var initId=function(){
            $scope.firstId=null;
            $scope.secondId=null;
            $scope.thirdId=null;
        }
        //获取地理位置
        $scope.getOperate = function(_id,type,name,value,pvalue){
            var url = window.basePath + '/account/Seller/getOperate?pid='+_id;
            $http.get(url).success(function (re) {
                $scope.operateId=_id;
                $scope.operateName=name;
                if(type==1){
                    $scope.firstList = re.content.items;
                    $scope.secondList=null;
                    $scope.thirdList=null;

                    $scope.secondId=null;
                    $scope.thirdId=null;
                }
                if(type==2){
                    $scope.secondList = re.content.items;
                    $scope.firstId=_id;
                    $scope.thirdList=null;

                    $scope.secondId=null;
                    $scope.thirdId=null;

                    $scope.operatePValue=null;
                    $scope.operateValue=value;
                }
                if(type==3){
                    $scope.thirdList = re.content.items;
                    $scope.secondId=_id;
                    $scope.thirdId=null;

                    $scope.operatePValue=pvalue;
                    $scope.operateValue=value;
                }if(type==4){
                    $scope.thirdId=_id;

                    $scope.operatePValue=pvalue;
                    $scope.operateValue=value;
                }
            })
        };

        $scope.isGoPage=function(){
            var selectedValue="";
            if(window.isEmpty($scope.operatePValue)){
                selectedValue="_"+$scope.operateValue+"_";
            }else{
                selectedValue=$scope.operatePValue+"_"+$scope.operateValue+"_";
            }

            if($rootScope.pathParams.userApply != null){
                $rootScope["$$"+$rootScope.pathParams.userApply]={
                    operateType:$scope.operateName,
                    operateValue:selectedValue
                };
                $rootScope.goBack();
            }
            //else if($scope.operateId!=null && $scope.operateId!=-1){
            //    var url = window.basePath + '/account/Seller/saveSellerInfo';
            //    var data = {
            //        operateType:$scope.operateName,
            //        operateValue: selectedValue
            //    };
            //    $http.post(url,data).success(function () {
            //        malert("修改成功!");
            //        $rootScope.goPage('/store/sellerInfo');
            //    });
            //}
            else{
                malert("请选择一个类别");
            }
        };

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '选择经营范围');
                $rootScope.isIndex = false;

                init();
                initId();

                $scope.getOperate(-1,1);
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);