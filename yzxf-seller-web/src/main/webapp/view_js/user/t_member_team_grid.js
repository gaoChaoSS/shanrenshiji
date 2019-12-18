(function () {
    window.teamGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            if(window.isEmpty($scope.dataPage) || window.isEmpty($scope.dataPage.$$selectedItem)
                || window.isEmpty($scope.dataPage.$$selectedItem._id)){
                malert("请选择一个用户");
                return;
            }

            $scope.popWindowTemp = '/view/user/t_member_team_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '团队关系';

            $scope.checkSubPage(0);
            $scope.getTeamCount();
        };

        $scope.initFilter = function(){
            $scope.pageNo2 = 1;
            $scope.pageSize2 = 10;
            $scope.isNullPage2 = false;
            $scope.pageList2 = [];
            if($scope.entity==='Seller'){
                $scope.$$filter={sellerId:$scope.dataPage.$$selectedItem._id}
            }
            if($scope.entity==='Member'){
                $scope.$$filter={memberId:$scope.dataPage.$$selectedItem._id}
            }
        };

        // 收益统计
        $scope.getTeamCount = function(){
            $scope.teamCount={
                day: 0,
                dayNum: 0,
                month: 0,
                monthNum: 0,
                total: 0,
                totalNum: 0
            };
            var url = window.basePath+"/order/Team/getTeamCountByAdmin?"+($scope.entity).toLowerCase()+"Id="+$scope.dataPage.$$selectedItem._id;
            $http.get(url).success(function(re){
                if(re.content){
                    $scope.teamCount = re.content;
                    $.each($scope.teamCount,function(k,v){
                        if(!v){
                            $scope.teamCount[k]=0;
                        }else{
                            $scope.teamCount[k]=$rootScope.getMoney(v);
                        }
                    })
                }
            })
        };

        // 切换子页面
        $scope.checkSubPage =function(index){
            $scope.subIndex=index;
            $scope.initFilter();
            $scope.getData();
        };

        $scope.getData = function(){
            if($scope.subIndex===0){
                $scope.teamList = {};
                $scope.getTeam();
            }else if($scope.subIndex===1){
                $scope.getTeamOrder();
            }
        };

        // 获取团队订单
        $scope.getTeamOrder = function(){
            var url = window.basePath + "/order/Team/getTeamLog?"
                + "pageNo="+$scope.pageNo2
                + "&pageSize="+$scope.pageSize2;

            $.each($scope.$$filter,function(k,v){
                if(!window.isEmpty(v)){
                    url+= "&"+k+"="+v;
                }
            });

            $http.get(url).success(function(re){
                $scope.page = re.content;
                $scope.totalNumber = re.content.totalNum;
                $scope.totalPage2 = re.content.totalPage;
                $scope.checkPage();
            })
        };

        // 获取团队关系
        $scope.getTeam = function(){
            var url = window.basePath + "/order/Team/getTeamUpUnder?"
                + "pageNo="+$scope.pageNo2
                + "&pageSize="+$scope.pageSize2;

            $.each($scope.$$filter,function(k,v){
                if(!window.isEmpty(v)){
                    url+= "&"+k+"="+v;
                }
            });

            $http.get(url).success(function(re){
                $scope.teamList = re.content;
                $scope.page = re.content.under;
                $scope.totalNumber = re.content.under.totalNum;
                $scope.totalPage2 = re.content.under.totalPage;
                $scope.checkPage();
            })
        };

        $scope.checkPage = function(){
            //页码集合
            $scope.pageList2 = [];
            //当前显示的页码从第几页开始
            var listCur = 1;
            var listCurCount = 5;
            if ($scope.pageNo2 <= 5 && $scope.totalPage2 <= 5) {
                listCur = 1;
                listCurCount = $scope.totalPage2;
            } else {
                listCur = $scope.pageNo2 - 2;
                if (listCur <= 1) {
                    listCur = 1;
                } else if (($scope.pageNo2 > $scope.totalPage2 - 3 && listCur >= 4) || $scope.totalPage2 == 6) {
                    listCur = $scope.totalPage2 - 4;
                }
            }
            for (var index = 0; index < listCurCount; index++, listCur++) {
                $scope.pageList2.push({num: listCur});
            }
            //选中的是第几个页码
            $scope.pageIndex2 = $scope.pageNo2;
            //是否显示最后一页的页码
            $scope.isLastPage2 = ($scope.pageIndex2 < $scope.totalPage2 - 2) && $scope.totalPage2 > 5;
            //是否显示第一页的页码
            $scope.isFirstPage2 = $scope.pageIndex2 >= 4 && $scope.totalPage2 > 5;

            $scope.isNullPage2 = $scope.totalNumber < 1 || $scope.totalNumber === null;
        }

        //上一页/下一页
        $scope.pageNext2 = function (num) {
            if ($scope.pageNo2 + num < 1 || $scope.totalPage2 < $scope.pageNo2 + num) {
                return;
            }
            $scope.pageNo2 += num;
            $scope.getData();
        }

        //跳转页码
        $scope.pageNumber2 = function (num) {
            if (num < 1 || $scope.totalPage2 < num) {
                return;
            }
            $scope.pageNo2 = num;
            $scope.getData();
        }

        $scope.pageCur2=function(index){
            $scope.pageIndex2=index;
        }

        $scope.init();
    }
})();