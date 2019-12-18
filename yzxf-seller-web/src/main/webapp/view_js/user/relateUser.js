(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $scope.tempGridList = '/view/user/t_relateUser_grid.jsp';
        $scope.tempGridFilter = '/view/user/t_relateUser_grid_filter.jsp';
        $scope.model = 'account';
        $scope.entity = 'User';
        $scope.entityTitle = "账号关联";
        $scope.windowInfo = '/view/user/t_relateInfo_grid.jsp';
        $scope.fullQueryApi = window.basePath + "/account/User/getRelateUser";

        $scope.adminCheck=false;
        $scope.getAgentAction = function () {
            $scope.actionList || ($scope.actionList = []);
            if ($rootScope.agent.level==1) {
                $scope.adminCheck=true;
            }
        }

        $scope.addBtn=function(name,action){
            for(var i =0;i<$scope.actionList.length;i++){
                if($scope.actionList[i].name==name){
                    $scope.actionList.remove(i);
                    break;
                }
            }
            $scope.actionList.push({
                name: name, action: action
            });
        };

        initGrid($rootScope, $scope, $http);

        $scope.showOtherData = function () {
            $scope.isBindUser = false;
            $scope.$$userInfo = angular.copy($scope.dataPage.$$selectedItem);
            $scope.getUserInfo();
        };

        // 组装数据
        $scope.checkUserData = function(oldData){
            if(window.isEmpty(oldData.memberName)){
                oldData.memberName = oldData.memberMobile;
            }
            $scope.$$userInfo = oldData;
            $scope.userList= [
                {title:'会员',type:'Member'},
                {title:'商家',type:'Seller'},
                {title:'服务站',type:'Factor'},
                {title:'代理商',type:'Agent'},
            ];
            var type = "";
            $.each($scope.userList,function(k,v){
                $.each($scope.$$userInfo,function(k2,v2){
                    type = v.type.toLowerCase();
                    if(k2.indexOf(type)!==-1 && !window.isEmpty(v2)){
                        if(k2.indexOf(type+'Id')!==-1){
                            v['_id'] = v2;
                        }else if(k2.indexOf(type+'Name')!==-1){
                            v['name'] = v2;
                        }
                    }
                });
            })
        };

        // 绑定账号
        $scope.goBindUser = function(user){
            $scope.searchPage = {
                pageNo:1,
                pageSize:10,
                items:[]
            };
            $scope.isBindUser = true;
            $scope.selectUser = angular.copy(user);
        }

        $scope.getSearchUser = function(){
            var url = window.basePath + '/account/User/getUserBaseInfo?' +
                'search='+$scope.selectUser.$$name+
                '&userType='+$scope.selectUser.type+
                '&pageNo='+$scope.searchPage.pageNo+
                '&pageSize='+$scope.searchPage.pageSize;
            $http.get(url).success(function(re){
                var $$page = angular.copy($scope.searchPage);
                $.each(re.content.items,function(k,v){
                    $$page.items.push(v);
                });
                $scope.searchPage = re.content;
                $scope.searchPage.items = $$page.items;
            });
        };

        // 重新搜索
        $scope.reSearchUser = function(){
            $scope.searchPage = {
                pageNo:1,
                pageSize:10,
                items:[]
            };
            $scope.getSearchUser();
        }

        // 下一页
        $scope.nextSearchUser = function(){
            $scope.searchPage.pageNo++;
            $scope.getSearchUser();
        }

        $scope.getUserInfo = function(){
            var url = window.basePath + '/account/User/getRelateUser?userId='+$scope.$$userInfo.userId;
            $http.get(url).success(function(re){
                $scope.$$userInfo = re.content.items[0];
                $scope.checkUserData($scope.$$userInfo);
            });
        }

        // 绑定账号
        $scope.setBindUser = function(user){
            var name = window.isEmpty(user.name)?user.mobile:user.name;
            mconfirm("是否绑定'"+name+"'?","请谨慎操作",function(){
                var url = window.basePath + '/account/User/bindUser';
                var data = {
                    userId:$scope.$$userInfo.userId,
                    bindId:user._id,
                    bindType:$scope.selectUser.type
                };
                $http.post(url,data).success(function(re){
                    malert("操作成功");
                    $scope.isBindUser = false;
                    $scope.getUserInfo();
                });
            })
        }

        // 解除绑定账号
        $scope.relieveUser = function(user){
            var name = window.isEmpty(user.name)?user.mobile:user.name;
            mconfirm("是否解除绑定'"+name+"'?","请谨慎操作",function(){
                var typeIdStr = user.type.toLowerCase();
                var url = window.basePath + '/account/User/relieveUser';
                var data = {
                    userId:$scope.$$userInfo.userId,
                    bindId:$scope.$$userInfo[typeIdStr+'Id'],
                    bindType:user.type
                };
                $http.post(url,data).success(function(re){
                    malert("操作成功");
                    $scope.isBindUser = false;
                    $scope.getUserInfo();
                });
            })
        }

        // 重置账号密码
        $scope.resetPwd = function(user){
            var content = "";
            var name = window.isEmpty(user.name)?user.mobile:user.name;
            if(user.type!=='Member'){
                content = "重置该角色密码将会同时更改商家、服务站、代理商的登录密码"
            }else{
                content = "会员为独立密码，不影响其他三个角色的登录密码";
            }
            mconfirm("是否重置'"+name+"'角色账号的密码?",content,function(){
                var typeIdStr = user.type.toLowerCase();
                var url = window.basePath + '/account/User/resetPwd';
                var data = {
                    userId:$scope.$$userInfo.userId,
                    bindId:$scope.$$userInfo[typeIdStr+'Id'],
                    bindType:user.type
                };
                $http.post(url,data).success(function(re){
                    malert("新密码："+re.content.password);
                    $scope.isBindUser = false;
                });
            })
        }
    });
})(angular);
