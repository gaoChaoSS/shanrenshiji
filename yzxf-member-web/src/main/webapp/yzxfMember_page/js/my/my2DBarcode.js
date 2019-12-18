(function (angular, undefined) {

    var model = 'my';
    var entity = 'my2DBarcode';
    var entityUrl = '/' + model + '/' + entity;
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {

        //显示认证或者未认证
        $scope.renZheng = false;
        $scope.isCardNo = null;
        $scope.address=null;
        $scope.email=null;

        $scope.getMemberIsRealName = function () {
            var url = window.basePath + '/crm/Member/getMemberInfoByCur';
            $http.get(url).success(function (re) {
                $scope.memberInfo = re.content.items[0];
                $scope.address=$scope.memberInfo.address;
                $scope.email=$scope.memberInfo.email;
                re.content.items[0].isRealName ? $scope.renZheng = true : $scope.renZheng = false;
                $scope.isCardNo = $scope.memberInfo.memberCardId;
                //if (!window.isEmpty($scope.memberInfo.realName)) {
                //    $scope.memberInfo.realName = $scope.memberInfo.realName.replace(/.(?=.)/g, '*');
                //}

                if (!window.isEmpty($scope.memberInfo.idCard)) {
                    if (($scope.memberInfo.idCard).length == 15) {
                        $scope.memberInfo.idCard = $scope.memberInfo.idCard.substr(0, 3) + "*********" + $scope.memberInfo.idCard.substr(11);
                    } else if (($scope.memberInfo.idCard).length == 18) {
                        $scope.memberInfo.idCard = $scope.memberInfo.idCard.substr(0, 3) + "***********" + $scope.memberInfo.idCard.substr(14);
                    }
                }
                if (!window.isEmpty($scope.memberInfo.mobile)) {
                    $scope.qrMobile=$scope.memberInfo.mobile;
                    $scope.memberInfo.mobile = $scope.memberInfo.mobile.substr(0, 3) + "****" + $scope.memberInfo.mobile.substr(7);
                }
                $scope.getShowBk();
                $scope.iconCheck();
            });
        };

        //处理背景图片
        $scope.getShowBk=function(){
            if(window.isEmpty($scope.memberInfo.icon)){
                $scope.showBk="background:#ccc";
            }else{
                $scope.showBk="background-image:url('"+$rootScope.iconImg($scope.memberInfo.icon)+"')";
            }
        };

        $scope.uploadFile = function (inputObj) {
            window.uploadWinObj = {
                one: true,
                entityName: 'Member',
                entityField: 'icon',
                entityId: $scope.memberInfo._id,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.saveData(options.fileId);
                    })
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        }

        $scope.saveData = function (iconStr) {
            var url = window.basePath + '/crm/Member/saveMyInfo';
            $http.put(url, {icon: iconStr}).success(function () {
                malert('修改头像成功!');
                $scope.memberInfo.icon = iconStr;
                $rootScope.myInfo.icon = iconStr;
                $scope.getShowBk();
            });
        }

        $scope.cardCheck = function () {
            if (window.isEmpty($scope.isCardNo)) {
                return '未绑定会员卡';
            }
            return $scope.isCardNo;
        }
        $scope.iconCheck = function () {
            if ($scope.renZheng) {
                $scope.iconImg = '/yzxfMember_page/img/logo2.png';
                $scope.iconText = '已认证';
                $scope.icon2DCard = '/yzxfMember_page/img/2DCODE.png';
            } else {
                $scope.iconImg = '/yzxfMember_page/img/logo1.png';
                $scope.iconText = '未认证';
                $scope.icon2DCard = '/yzxfMember_page/img/2DCODE.png';
            }
        }
        $scope.updateArea = function () {
            if ($rootScope['$$myInfoAddress'] != null) {
                var url = window.basePath + '/crm/Member/updateMemberInfoArea';
                var data={
                    area:$rootScope['$$myInfoAddress'].locationArea,
                    areaValue:$rootScope['$$myInfoAddress'].locationAllValue
                }
                $http.post(url,data).success(function () {
                    $scope.getMemberIsRealName();
                    malert("保存成功!");
                    $rootScope['$$myInfoAddress'] = null;
                });
            }else{
                $scope.getMemberIsRealName();
            }
        }
        $scope.updateAddress = function () {
            if (!window.isEmpty($scope.address)) {
                var url = window.basePath + '/crm/Member/updateMemberInfo';
                var data={
                    field:'address',
                    content:$scope.address
                }
                $http.post(url,data).success(function () {
                    malert("保存成功!");
                })
            }
        }
        $scope.updateEmail = function () {
            if (!/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($scope.email)) {
                $scope.email = '';
                malert("请输入正确的邮箱!");
                return;
            }
            var url = window.basePath + '/crm/Member/updateMemberInfo';
            var data={
                field:'email',
                content:$scope.email
            }
            $http.post(url,data).success(function () {
                malert("保存成功!");
            })
        };

        $scope.updateSex = function () {
            if (window.isEmpty($scope.memberInfo.sex)) {
                malert("请选择性别!");
                return;
            }
            var url = window.basePath + '/crm/Member/updateMemberInfo';
            var data={
                field:'sex',
                content:$scope.memberInfo.sex
            };
            $http.post(url,data).success(function () {
                malert("保存成功!");
            })
        };

        $scope.getSex=function(sex){
            if(window.isEmpty(sex)){
                return '未知';
            }else if(sex == 1){
                return '男';
            }else if(sex == 2){
                return '女';
            }
        }

        $scope.clearStoreLocation=function(){
            $rootScope['$$storeArea']=null;
        };

        $scope.getQrcode=function(){
            $scope.myCard=!$scope.myCard;
            var qr = qrcode(10, 'H');
            qr.addData($scope.qrMobile);
            qr.make();
            $(".qrcode").html(qr.createImgTag());
            $(".qrcode img").addClass("iconImg2");
        }


        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, '个人信息');
                $rootScope.isLoginPage = true;
                $scope.renZheng = false;
                $scope.sexCheck = false;
                $scope.updateArea();
            }
        }
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
    //当图片加载失败时,显示的404图片
    window.app.register.directive('errSrc', function () {
        return {
            link: function (scope, element, attrs) {
                element.bind('error', function () {
                    if (attrs.src != attrs.errSrc) {
                        attrs.$set('src', attrs.errSrc);
                    }
                });
            }
        }

    });
})(angular);