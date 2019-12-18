(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.model = 'account';
        $scope.entity = 'UserPending';
        $scope.entityTitle = "待初审";
        $scope.tempGridList = '/view/pending/t_pending_grid.jsp';
        $scope.windowInfo = '/view/pending/t_pendFirstInfo_grid.jsp';
        $scope.tempGridFilter = '/view/pending/t_pending_grid_filter.jsp';
        $scope.apiName = "getPendingList";

        $scope.pendingBtnCheck = false;
        $scope.modifyAreaCheck = false;
        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if (/^[1]$/.test($rootScope.agent.level)) {
                $scope.pendingBtnCheck = true;
            }
        };

        $scope.setUserType=function(){
            $scope.filter._status='0.1';
        };

        initGrid($rootScope, $scope, $http);
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
        $scope.showOtherData = function () {
            $scope.checkBtn = [false, false, false, false];
            $rootScope.showPopWin = true;
            $scope.showImg='';
            var url = window.basePath + "/account/UserPending/show?_id=" + $scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content.text;
                $scope.verifyInfo = re.content;

            });
        }

        $scope.getOwnerType = function (type) {
            if (type == 'Seller') {
                return '商户';
            } else if (type == 'Agent') {
                return '代理商';
            } else if (type == 'Factor') {
                return '服务站';
            }
        }

        $scope.checkBtnFun = function (index) {
            $scope.checkBtn[index] = !$scope.checkBtn[index];
        }

        $scope.submitForm = function (status) {
            if ($scope.checkBtn[2] && (window.isEmpty($scope.userInfo.explain) || $scope.userInfo.explain.length>200)) {
                malert("请填写审核不通过的原因,且在200字以内");
                return;
            }

            $scope.userInfo.status = status;
            $scope.userInfo.pendingId = $scope.verifyInfo._id;
            var url = window.basePath + "/account/UserPending/verifyFirstUser";
            $http.post(url, $scope.userInfo).success(function () {
                $scope.checkBtnFun(3);
                $scope.queryCurrentList();
            });
        }

        $scope.closeWin = function () {
            $scope.checkBtn = [false, false, false, false];
            $rootScope.showPopWin = false;
        }

        $scope.getTimeChange = function (time) {
            return new Date(time).getTime();
        }

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        }

        $scope.closeImgFun=function(){
            $scope.showImg='';
        }
    });
})(angular);
