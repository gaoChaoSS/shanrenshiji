/**
 * Created by zq2014 on 16/12/19.
 */
/**
 * Created by zq2014 on 16/12/19.
 */
(function (angular, undefined) {

    var model = 'store';
    var entity = 'storeBusiness';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        $scope.openTime=9;
        $scope.closeTime=18;

        //num:减去的数量;check:减去的时间
        $scope.timeBtn=function(num,check){
            if (check) {
                $scope.openTime += num;
                if ($scope.openTime < 0 || $scope.closeTime <= $scope.openTime) {
                    $scope.openTime -= num;
                }
            } else {
                $scope.closeTime += num;
                if ($scope.closeTime <= $scope.openTime || $scope.closeTime > 24) {
                    $scope.closeTime -= num;
                }
            }
        }

        $scope.updateStoreTime = function () {
            var w=$scope.weekList;
            var weekStr="";
            for(id in w){
                if(w[id].check){
                    weekStr+=w[id].name+',';
                }
            }
            weekStr=weekStr.substring(0,weekStr.length - 1);
            var url = window.basePath + '/account/Seller/updateSellerTime';
            var data={
                openTime:$scope.openTime,
                closeTime:$scope.closeTime,
                openWeek:weekStr
            }
            $http.post(url,data).success(function () {
                malert("保存成功!");
                $rootScope.goPage("/store/sellerInfo");
            });
        }

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '营业时间');
                $scope.weekList=[
                    {name:'一',check:false},
                    {name:'二',check:false},
                    {name:'三',check:false},
                    {name:'四',check:false},
                    {name:'五',check:false},
                    {name:'六',check:false},
                    {name:'日',check:false}
                ]

                if(!window.isEmpty($rootScope.pathParams.openTime)){
                    var timeArr= $rootScope.pathParams.openTime.split("_");
                    $scope.openTime=parseInt(timeArr[0]);
                    $scope.closeTime=parseInt(timeArr[1]);
                }
                if(!window.isEmpty($rootScope.pathParams.openWeek)){
                    var weekArr = $rootScope.pathParams.openWeek.split(",");
                    for(var i= 0,leni=weekArr.length;i<leni;i++){
                        for(var j= 0,lenj=$scope.weekList.length;j<lenj;j++){
                            if($scope.weekList[j].name==weekArr[i]){
                                $scope.weekList[j].check=true;
                                break;
                            }
                        }
                    }
                }
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);