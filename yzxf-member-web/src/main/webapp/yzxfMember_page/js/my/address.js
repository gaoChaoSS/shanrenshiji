(function (angular, undefined) {

    var model = 'my';
    var entity = 'address';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //$scope.list=[
        //    {name:"abc"},
        //    {name:"ccc"}
        //]
        //
        //$scope.additem=function(){
        //    $scope.list.push({name:"a"})
        //}
        //$scope.del=function($index){
        //    $scope.list.splice($index,1)
        //}

        $scope.delCheck=false;

        $scope.manageBtn=function(){
            $scope.check=!$scope.check;
            $scope.delCheck=false;
        }

        $scope.delCheckFun=function(){
            $scope.delCheck=true;
        }

        $scope.clearArea=function(){
            $rootScope['$$addAddressArea']=null;
        }

        $scope.getAddress = function () {
            var url = window.basePath + '/crm/MemberAddress/getMemberAddress';
            $http.get(url).success(function (re) {
                $scope.queryAddress = re.content.items;
            })
        }

        $scope.delAddress = function (id) {
            var url = window.basePath + '/crm/MemberAddress/delAddress';
            var data={_id:id}
            $http.post(url,data).success(function () {
                $scope.getAddress();
            })
        }
        $scope.setDefaultAddress = function(addressId){
            var url = window.basePath + '/crm/MemberAddress/setDefaultAddress?addressId='+addressId;
            $http.get(url).success(function () {
                $scope.getAddress();
            })
        }
       //$scope.isDefaultAddress = function(){
       //
       //}
        //页面事件处理
        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                //$rootScope.windowTitle = '个人中心';
                window.setWindowTitle($rootScope, '我的收货地址');
                $scope.getAddress();
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();

    });
})(angular);