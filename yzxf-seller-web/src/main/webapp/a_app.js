(function (angular, undefined) {
    window.basePath = '/s_agent/api'
    window.app = angular.module('phonecat', ['ngRoute']);
    window.menus = [];
    window.app.config(function ($controllerProvider, $compileProvider, $filterProvider, $provide) {
        app.register = {
            controller: $controllerProvider.register,
            directive: $compileProvider.directive,
            filter: $filterProvider.register,
            factory: $provide.factory,
            service: $provide.service
        };
    });
    window.app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/:modelName/:actionName', {
            templateUrl: function (rd) {
                return '/view/' + rd.modelName + '/' + rd.actionName + '.jsp';
            },
            // 关键点，用于动态加载js
            resolve: {
                load: function ($q, $route, $rootScope) {
                    var deferred = $q.defer();
                    var dependencies = ['/view_js/' + $route.current.params.modelName + '/' + $route.current.params.actionName + '.js?v=' + window.angular_temp_version];
                    $script(dependencies, function () {
                        $rootScope.$apply(function () {
                            deferred.resolve();
                        });
                    });
                    return deferred.promise;
                }
            }
        });
    }]);

    window.app.controller('allBodyCtrl', function ($rootScope, $scope, $http, $location, $interval) {
        $rootScope.pwdWindow = false;
        $rootScope.goPageDefault="#/account/login";
        $rootScope.showPwdWindow = function () {
            $scope.pwdWindow = true;
            $scope.oldPwd = "";
            $scope.newPwd = "";
            $scope.newSPwd = "";
        }
        $rootScope.closePwdWin = function () {
            $scope.pwdWindow = false;
            $scope.oldPwd = "";
            $scope.newPwd = "";
            $scope.newSPwd = "";
        }
        $rootScope.modifyPwd = function (oldPwd, newPwd, newSPwd) {
            if (!/^.{6,16}$/.test(oldPwd) || window.isEmpty(oldPwd)) {
                malert('请输入6~16位的原密码!');
                return;
            }
            if (!/^.{6,16}$/.test(newPwd) || window.isEmpty(newPwd)) {
                malert('请输入6~16位的新密码!');
                return;
            }
            if (newPwd != newSPwd || window.isEmpty(newSPwd)) {
                malert('请确认密码!');
                return;
            }
            var url = window.basePath + '/account/Agent/modifyLoginPwd';
            var data = {
                oldPwd: oldPwd,
                pwdOne: newPwd,
                pwdTwo: newSPwd
            }
            $http.post(url, data).success(function () {
                malert("密码修改成功");
                $rootScope.pwdWindow = false;
                $scope.oldPwd = "";
                $scope.newPwd = "";
                $scope.newSPwd = "";
            })
        }
        $rootScope.sites = {};
        $rootScope.sites.title = "";

        $rootScope.initMenus=function(agentLevel){
            $rootScope.allMenus=[
                [//平台管理员
                    {
                        _id: 'home',
                        __openMenu: true,
                        hide:true,
                        name: '首页',
                        icon: 'icon-huiyuan',
                        items: [
                            {_id: 'index', name: '首页'}
                        ]
                    }, {
                    _id: 'user',
                    __openMenu: true,
                    name: '运营管理',
                    icon: 'icon-huiyuan',
                    items: [
                        {_id: 'member', name: '会员管理'},
                        {_id: 'seller', name: '商户管理'},
                        {_id: 'agent', name: '代理商管理',adminAccesss: agentLevel==4},
                        {_id: 'factor', name: '服务站管理'},
                        {_id: 'relateUser', name: '账号关联', adminAccesss: true},
                        {_id: 'sellerRecommendManage', name: '商户权限管理',adminAccesss: agentLevel!=4},
                        {_id: 'commodityTypeManage', name: '商品管理', adminAccesss: true},
                        {_id: 'advertisingManage', name: '图标广告管理', adminAccesss: true},
                    ]
                }, {
                    _id: 'account',
                    __openMenu: true,
                    name: '清算管理',
                    icon: 'icon-gonggongxinxi',
                    items: [
                        {_id: 'trade', name: '交易流水'},
                        {_id: 'payLog', name: '第三方支付记录', adminAccesss: true},
                        {_id: 'cardMoneyLog', name: '会员激活记录', adminAccesss: true},
                        {_id: 'agentIncome', name: '代理商收益结算'},
                        {_id: 'factorIncome', name: '服务站收益月报'},
                        {_id: 'sellerIncome', name: '商家收益月报'}
                    ]
                }, {
                    _id: 'insure',
                    __openMenu: true,
                    name: '投保管理',
                    icon: 'icon-icon3',
                    items: [
                        {_id: 'accidentLog', name: '意外险投保明细'},
                        {_id: 'pensionLog', name: '养老金投保明细'}
                    ]
                }, {
                    _id: 'card',
                    __openMenu: true,
                    name: '实体卡管理',
                    icon: 'icon-llmenustorename',
                    items: [
                        {_id: 'cardManage', name: '实体卡分配'},
                        {_id: 'cardRecycle', name: '实体卡回收'},
                        {_id: 'cardInfo', name: '实体卡信息'},
                        {_id: 'cardCount', name: '实体卡统计'},
                        {_id: 'cardLog', name: '分配卡记录'}
                    ]
                }, {
                    _id: 'count',
                    __openMenu: true,
                    name: '统计分析',
                    icon: 'icon-dailiquan',
                    items: [
                        {_id: 'memberCount', name: '会员统计'},
                        {_id: 'memberTradeRank', name: '会员交易排行'},
                        {_id: 'sellerTradeRank', name: '商家交易排行'}
                    ]
                }, {
                    _id: 'pending',
                    __openMenu: true,
                    name: '审批',
                    icon: 'icon-shenfenzheng',
                    items: [
                        {_id: 'pendRecord', name: '用户审批历史'},
                        // {_id: 'pendFirst', name: '待初审用户'},
                        {_id: 'pending', name: '待复审用户'},
                        {_id: 'pendDrafts', name: '用户草稿箱'}
                    ]
                }
                ],[//财务管理员
                    {
                        _id: 'financial',
                        __openMenu: true,
                        name: '结算管理',
                        icon: 'icon-huiyuan',
                        items: [
                            {_id: 'financailReport', name: '代理商收益结算'}
                        ]
                    }, {
                        _id: 'pending',
                        __openMenu: true,
                        name: '审批',
                        icon: 'icon-shenfenzheng',
                        items: [
                            {_id: 'withdrawLog', name: '提现记录'},
                            {_id: 'withdrawPend', name: '提现申请'}
                        ]
                    }
                ],[//官网管理员
                    {
                        _id: 'website',
                        __openMenu: true,
                        name: '官网管理',
                        icon: 'icon-home',
                        items: [
                            {_id: 'advertisingManage', name: '官网图片管理'},
                            {_id: 'websiteNewsManage', name: '官网新闻管理'},
                            {_id: 'branches', name: '分支机构管理'},
                            {_id: 'mallHelp', name: '商城帮助中心'}
                        ]
                    }
                ],[//利润管理员
                    {
                        _id: 'profit',
                        __openMenu: true,
                        name: '利润管理',
                        icon: 'icon-home',
                        items: [
                            // {_id: 'profit', name: '利润分配'},
                            {_id: 'parameter', name: '参数配置'},
                            {_id: 'parameterLog', name: '修改记录'}
                        ]
                    }
                ]
            ];

            if(!window.isEmpty(getCookie("___agent_adminType"))){
                var index=parseInt(getCookie("___agent_adminType"));
                if(index<0 && index>=$rootScope.allMenus.length){
                    malert("获取管理员类型失败");
                    goPage('#/account/login');
                }else{
                    $rootScope.menus = $rootScope.allMenus[index];
                    $rootScope.goPageDefault="#/"+$rootScope.menus[0]._id+"/"+$rootScope.menus[0].items[0]._id;
                }
            }else if(!window.isEmpty(getCookie("___agent_Id"))){//代理商默认访问初始页面
                $rootScope.menus = $rootScope.allMenus[0];
                $rootScope.goPageDefault="#/home/index";
            }else{
                $rootScope.goPageDefault="#/account/login";
            }
        };

        $rootScope.clickAllBody = function () {
            $rootScope.$broadcast('/allMenuHide');
        }

        $rootScope.filterTimeButtons = [
            {_id: 'today', name: '今日'}
            , {_id: 'yesterday', name: '昨日'}
            , {_id: 'prev7day', name: '近7日'}
            , {_id: 'month', name: '本月'}
            , {_id: 'prevMonth', name: '上月'}
        ];

        $scope.loginFormType = 'login';
        window.deviceId = getCookie('deviceId');
        var actions = [];
        if (window.deviceId == null) {
            actions.push(function (next) {
                // 注册设备
                var data = genDeviceData();
                data._id = genUUID();
                $http.put(window.basePath + '/common/Device/save', data).success(function (re) {
                    window.deviceId = re.content._id;
                    setCookie('deviceId', window.deviceId);
                    next();
                }).error(next);
            });
        }
        // 加载系统信息
        actions.push(function (next) {
            $http.get(window.basePath + '/common/Setting/site').success(function (re) {
                $scope.site = {};
                $.each(re.content.items, function (k, v) {
                    $scope.site[v.name] = v.value;
                })
                next();
            }).error(next);
        });

        actions.push(function (next) {
            $http.get(window.basePath + '/account/Agent/getAgentByCurrent').success(function (re) {
                $rootScope.agent = re.content;
                next();
            }).error(next);
        });

        actions.push(function (next) {
            // 链接webscoket
            if (getCookie('___ADMIN_TOKEN') != null) {
                //window.initWebsocket();
            }
            if (window.pageCallback) {
                window.pageCallback();
            }
            (function () {
                $("#allLoadingCon").fadeOut(100);
            }).delay(0.4);
        });

        $(document).queue('init', actions);
        $(document).dequeue('init');
        $scope.sellerId = '';
        $scope.ccc = function () {
            console.log($scope.sellerId);
        }

        // init EVENT
        $scope.clickLeftMenu = function (item) {
            var menus = $rootScope.menus;
            if (menus.__selectMenuId == item._id) {
                menus.__openMenu = !menus.__openMenu;
            } else {
                menus.__openMenu = true;
            }
            menus.__selectMenuId = item._id;
        }
        $scope.clickLeftSelected = function (index) {
            if ($scope.leftSelected == index) {
                $scope.leftSelected = null;
            } else {
                $scope.leftSelected = index;
            }
        }
        $rootScope.iconImgUrl = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon + '&wh=650_0') : '/yzxfSeller_page/img/notImg02.jpg';
        }
        //显示图片
        $rootScope.iconImg = function (icon) {
            return (icon != null && icon != "") ? ('/s_img/icon.jpg?_id=' + icon ) : '/yzxfSeller_page/img/notImg02.jpg';
        }
        $rootScope.isNullNum = function (text) {
            return window.isEmpty(text) ? 0 : text;
        }
        $rootScope.showDate = function (time) {
            return new Date(time).showDate();
        }
        $rootScope.getFixed = function (model, eitity) {
            $.each($rootScope.menus, function (k, v) {
                var menus = $rootScope.menus;
                if (v._id == model) {
                    menus.__selectMenuId = v._id;
                    menus.__openMenu = true;
                    $.each(v.items, function (kk, vv) {
                        if (vv._id == eitity) {
                            $(".menuLeft").eq(k).children(".menuLeftChild").children().removeClass("selected");
                            $(".menuLeft").eq(k).children(".menuLeftChild").eq(kk).children().addClass("selected");
                        }
                    })
                }
            });
        }
        //$rootScope.goPagePend=function($event){
        //    $scope.goPage2('#/pending/pending');
        //    $event.stopPropagation();
        //}
        $rootScope.showYFullTime = function (time) {
            return new Date(time).showYFullTime();
        }
        //导出excel文件
        $rootScope.getExcel = function (url,filter){
            url += "?1=1";
            $.each(filter, function (k, v) {
                if (k == 'startTime' || k == 'endTime') {
                    return true;
                }
                if (window.isEmpty(v)) {
                    return true;
                }
                url += '&' + k + '=' + encodeURIComponent(v);
            });
            if (!isEmpty(filter.$$startTime) || !isEmpty(filter.$$endTime)) {
                $scope.timeType={_id:"setTime"};
                window.initFilterTime($rootScope, $scope);
                url += '&_createTime=___in_' + filter.startTime + '-' + filter.endTime;
            }else if(!isEmpty(filter.startTime) && !isEmpty(filter.endTime)){
                url += '&_createTime=___in_' + filter.startTime + '-' + filter.endTime;

            }
            window.location.href=url;
        };
        // 累计
        $rootScope.countField=function(items,field){
            if(window.isEmpty(field) || window.isEmpty(items)){
                return;
            }
            var count = 0;
            $.each(items, function (k, v) {
                count = parseFloat(count) + parseFloat(v[field]);
            });
            return $rootScope.getMoney(count);
        };

        $rootScope.formatCountField=function(items,field){
            var count = $rootScope.countField(items,field);
            if(!window.isEmpty(count) && count!==0){
                count="("+count+")";
            }else{
                count='';
            }
            return count;
        };

        $rootScope.getOrderType=function(type){
            if (type == "0") {
                return '会员扫码';
            } else if (type == "1") {
                return '现金交易';
            } else if (type == "2") {
                return '非会员扫码';
            } else if (type == "3") {
                return '商家充值';
            } else if (type == "4") {
                return '服务站充值';
            } else if (type == "5") {
                return '会员充值';
            } else if (type == "6") {
                return '会员替朋友充值';
            } else if (type == "7") {
                return '服务站激活会员卡';
            } else if (type == "8") {
                return '会员端激活会员卡';
            } else if (type == "9") {
                return '商家提现';
            } else if (type == "10") {
                return '发卡点提现';
            } else if (type == "11") {
                return '会员在线购买';
            } else if (type == "12") {
                return '快易帮现金收款';
            } else if (type == "13") {
                return '养老金激活会员卡';
            }
        }

        // 弹出提示框
        $rootScope.getTool = function(data){
            $rootScope.tool = {
                title:data.title,
                desc:data.desc,
                exec:data.exec,
                cancel:data.cancel?data.cancel:function(){
                    $rootScope.tool={}
                },
                isShow:true
            }
        };

        $scope.doMenuAction = function (item, url) {
            goPage('#' + url);
            if ($scope.selectedMenu != null) {
                $scope.selectedMenu.__selected = false;
            }
            item.__selected = true;
            $scope.selectedMenu = item;
        }
        $scope.doLogout = function () {
            if (confirm("你是否确定注销本次登录?")) {
                deleteCookie('___AGENT_TOKEN');
                deleteCookie('___agent_AreaValue');
                deleteCookie('___agent_Name');
                deleteCookie('___agent_Id');
                deleteCookie('___agent_adminType');
                $scope.showLogin = true;
                $rootScope.agent={};
                $http.put(window.basePath + '/account/User/logout', {}).success(function () {
                    goPage('#/account/login');
                });
            }
        }
        $rootScope.isEmpty2 = function(str){
            return str == null || str == '' || str=='null';
        }
        $scope.goPage2 = function (str) {
            goPage(str);
        }
        $scope.getAgentName = function () {
            $rootScope.agentName = getCookie('___agent_Name');
        }
        //当值为Null时改为0
        $rootScope.isNullZero = function (number) {
            if (window.isEmpty(number) || number == 'null') {
                return 0;
            }
            return number;
        }
        $rootScope.isNullText = function (number) {
            if (window.isEmpty(number) || number == 'null') {
                return '';
            }
            return number;
        }
        $rootScope.getMoney = function (money) {
            money = $rootScope.isNullZero(money);
            var moneyStr=money.toString().split(".")[1];
            if(!window.isEmpty(moneyStr) && moneyStr.length>2){
                if(moneyStr.substring(2,3)=="9"){
                    money=money.toFixed(2);
                }
            }
            return money.toString().replace(/([0-9]+.[0-9]{2})[0-9]*/, "$1");
        }
        $scope.doLogin = function () {
            if ($scope.login.loginName != null && $scope.login.loginName.length > 0) {
                setCookie('loginName', $scope.login.loginName);
            }
            var con = {
                loginName: $scope.login.loginName,
                deviceId: deviceId,
                userType: 'agent',
                password: $scope.login.password
            };
            if ($scope.loginFormType == 'reg') {
                con.name = $scope.login.name;
            }
            var url = window.basePath + '/account/User/login';
            $http.post(url, con).success(function (re) {
                setCookie('clientType', 'admin', 1);
                setCookie('___AGENT_TOKEN', re.content.token, 60 * 12);
                setCookie('___agent_Name', re.content.agentName, 60 * 12);
                setCookie('___agent_Id', re.content.agentId, 60 * 12);
                setCookie('___agent_AreaValue', re.content.agentAreaValue, 60 * 12);
                setCookie('___agent_adminType', re.content.adminType, 60 * 12);
                $(document).queue('init', actions);
                $(document).dequeue('init');
                $scope.showLogin = false;
                //$scope.menus.__openMenu = false;
                $(".menuLeft>.menuLeftChild").children().removeClass("selected");

                $scope.login.password = "";
                //初始化页面
                $rootScope.initMenus(re.content.agentLevel);

                $scope.loginTimeCount = $interval(function () {
                    if (getCookie("___AGENT_TOKEN") == null) {
                        $interval.cancel($scope.loginTimeCount);
                        goPage('#/account/login');
                        location.reload(true);
                    }
                }, 1000 * 60 * 30 + 1000);
                $scope.getNewPending();
                goPage($rootScope.goPageDefault);
            });
        };

        $scope.isShow = function(level,loginLevel){
            var flag = true;
            if(level != null && loginLevel !=null){
                if(level <loginLevel){
                    flag =false;
                }
            }
            return flag;
        };
        $scope.getAgentLevel=function(level){
            if(level=='1'){
                return '平台管理员';
            }else if(level=='2'){
                return '省级代理商';
            }else if(level=='3'){
                return '市级服务中心';
            }else if(level=='4'){
                return '区县服务站';
            }
        };

        //根据管理员类型,判断跳转页面路径是否超出范围
        $scope.goPageByAdminType=function(){
            var curPage = $location.path().substring(1,$location.path().length).split("/");//当前页面
            var checkCount=0;
            if(window.isEmpty($rootScope.menus)){
                //goPage($rootScope.goPageDefault);
                $rootScope.initMenus();
                return;
            }
            //检查访问的父级路径是否在权限范围内
            for(var i= 0,len=$rootScope.menus.length;i<len;i++){
                if($rootScope.menus[i]._id==curPage[0]){
                    //检查子路径
                    for(var j= 0,jlen=$rootScope.menus[i].items.length;j<jlen;j++){
                        if($rootScope.menus[i].items[j]._id==curPage[1]){
                            checkCount++;
                        }
                    }
                }
            }
            if(checkCount==0){
                goPage($rootScope.goPageDefault);
            }
        };

        $scope.getNewPending = function () {
            if (!window.isEmpty(getCookie("___agent_adminType")) && parseInt(getCookie("___agent_adminType"))!=0) {
                return;
            }
            if (!window.isEmpty(getCookie("___AGENT_TOKEN"))) {
                var url = window.basePath + '/account/UserPending/getNewPending';
                $http.get(url).success(function (re) {
                    if (window.isEmpty(re.content.userPend)) {
                        return;
                    }
                    $rootScope.userPend = re.content.userPend;
                    $rootScope.withdrawPend = re.content.withdrawPend;
                    $rootScope.pendTotal = $rootScope.userPend.sellerCountSecond
                            + $rootScope.userPend.factorCountSecond
                            + $rootScope.userPend.agentCountSecond;
                    if (isNaN($rootScope.pendTotal)) {
                        $rootScope.pendTotal = 0;
                    }
                });
            }
        }
        $scope.modifyMyInfo = function () {
            if (!window.isEmpty(getCookie("___agent_adminType"))) {
                return;
            }
            $scope.winMyInfo = '/view/user/t_agentAdd_grid.jsp';
            $scope.showMyInfo = true;
            $scope.winMyInfoCheck = true;
            $rootScope.popWinTitle = '修改';

            $scope.userInfo = {};
            var url = window.basePath + "/account/Agent/getAgentByCurrent";
            $http.get(url).success(function (re) {
                $scope.userInfo = re.content;
                $scope.pendingId = "";
                $scope.isModify = true;
                modifyAgentGrid($rootScope, $scope, $http);
            });
        }
        $scope.closePopWin = function () {
            $scope.showMyInfo = false;
            $scope.winMyInfoTitle = null;
            $rootScope.agent.name = $scope.userInfo.name;
            $scope.winMyInfoCheck = false;
            $rootScope.popWinTitle = null;
        }

        $rootScope.$on('$locationChangeStart', function (e, path) {
            var agentId = getCookie("___agent_Id");
            if(window.isEmpty(agentId)){
                $rootScope.goPageDefault="#/account/login";
                goPage($rootScope.goPageDefault);
                return;
            }else{
                $scope.goPageByAdminType();
            }
            var url = window.basePath + '/account/Agent/getAgentIsTrue?agentId='+agentId;
            $http.get(url).success(function(re){
                if(!re.content.canUse){
                    deleteCookie('___AGENT_TOKEN');
                    deleteCookie('___agent_AreaValue');
                    deleteCookie('___agent_Name');
                    deleteCookie('___agent_Id');
                    deleteCookie('___agent_adminType');
                    $rootScope.agent={};
                    $scope.showLogin = true;
                    malert("抱歉!账户已禁用!请联系管理员!");
                }
            })
        });

        //动态为单元格创建悬停效果
        // $(document).on({
        //     mousemove : function(e){
        //         $("#checkTdWidth").text($(e.target).text());
        //         var width = parseInt($(e.target).css("width"));
        //         var textWidth = parseInt($("#checkTdWidth").css("width"));
        //
        //         //console.log("width:"+width+",text:"+textWidth);
        //         //没有内容或全显示的文字不显示
        //         if(window.isEmpty($(e.target).text()) || width>=textWidth){
        //             $("div").remove(".td-hover");
        //             return;
        //         }
        //         if($(".td-hover").length > 0 ){
        //             $(".td-hover").css({top:parseInt(e.originalEvent.y)+20,left:parseInt(e.originalEvent.x)+10});
        //         }else{
        //             $("body").append(
        //                 "<div class='td-hover' style='top:"+(parseInt(e.originalEvent.y)+20)+"px;left:"+(parseInt(e.originalEvent.x)+10)+"px'>" +
        //                 $(e.target).text()+
        //                 "<div class='triangle-bottomleft'></div>"+
        //                 "</div>")
        //         }
        //     } ,
        //     mouseout : function(){
        //         $(".td-hover").addClass("td-animate-out");
        //         setTimeout(function(){
        //             $("div").remove(".td-hover");
        //         },500);
        //     }
        // },".sectionTable >div>div");

        $scope.getNewPending();
        $scope.getAgentName();
    });
})(angular);