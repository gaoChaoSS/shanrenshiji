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

            $scope.industryId=[null,null,null,null];
        }
        var initId=function(){
            $scope.firstId=null;
            $scope.secondId=null;
            $scope.thirdId=null;
        }
        //获取经营集合
        $scope.getOperate = function(parentCode,type,code,name){
            var url = window.basePath + '/account/Seller/getOperate?parentCode='+parentCode;
            $http.get(url).success(function (re) {
                $scope.operateId=code;
                $scope.operateName=name;
                if(type==1){
                    $scope.firstList = re.content.items;
                    $scope.industryId[0]=parentCode;
                    $scope.secondList=null;
                    $scope.thirdList=null;

                    $scope.secondId=null;
                    $scope.thirdId=null;
                }
                if(type==2){
                    $scope.secondList = re.content.items;
                    $scope.firstId=parentCode;
                    $scope.industryId[1]=parentCode;
                    $scope.thirdList=null;

                    $scope.secondId=null;
                    $scope.thirdId=null;

                    $scope.operatePValue=null;
                    $scope.operateValue=value;
                }
                if(type==3){
                    $scope.thirdList = re.content.items;
                    $scope.secondId=parentCode;
                    $scope.industryId[2]=parentCode;
                    $scope.thirdId=null;

                    $scope.operatePValue=pvalue;
                    $scope.operateValue=value;
                }
                if(type==4){
                    $scope.thirdId=parentCode;
                    $scope.industryId[3]=parentCode;
                    $scope.operatePValue=pvalue;
                    $scope.operateValue=value;
                }

            })
        }
        //$scope.getIndex=function(_id,name){
        //
        //    $scope.operateId=_id;
        //    $scope.operateName=name;
        //}

        $scope.isGoPage= function(){
            if($scope.operateName==null){
                $rootScope["$$"+$rootScope.pathParams.operateUrl]=null;
            }else{
                /*var selectedValue="";
                if(window.isEmpty($scope.operatePValue)){
                    selectedValue="_"+$scope.operateValue+"_";
                }else{
                    selectedValue=$scope.operatePValue+"_"+$scope.operateValue+"_";
                }*/
                $rootScope["$$"+$rootScope.pathParams.operateUrl]={
                    operateName:$scope.operateName,
                    operateId:$scope.operateId,
                    industryId:$scope.industryId,
                    //operateValue:selectedValue
                };
            }
            $rootScope.goBack();

        }

        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '选择经营范围');
                $rootScope.isIndex = false;

                init();
                initId();

                $scope.getOperate(0,1);
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);