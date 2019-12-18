(function () {
    window.insureLogGrid = function ($rootScope, $scope, $http) {
        $scope.init = function () {
            $scope.popWindowTemp = '/view/insure/t_insure_list_grid.jsp';
            $rootScope.showPopWin = true;
            $rootScope.popWinTitle = '投保记录';

            $scope.insureList = [];
            $scope.pageNo2 = 1;
            $scope.pageSize2 = 20;
            $scope.isNullPage2 = false;
            $scope.pageList2 = [];


            $scope.initFilter();
            $scope.getInsureLog();
        };

        $scope.initFilter = function(){
            $scope.$$filter = {
                logId : '',
                status: '',
                returnFlag:'',
                orderType:'',
                realName:'',
                memberId:'',
                $$queryType:'all',//all,memberAll,logId
                $$queryTypeLast:'',//all,memberAll,logId
            };

            if(!window.isEmpty($scope.dataPage) &&
                !window.isEmpty($scope.dataPage.$$selectedItem) && !window.isEmpty($scope.dataPage.$$selectedItem._id)){
                $scope.$$filter.$$queryType = 'memberAll';
                $scope.$$filter.memberId = $scope.dataPage.$$selectedItem._id;
            }else{
                $scope.$$filter.$$queryType = 'all';
            }
        };

        $scope.getStatus = function(status){
            if(status==='apply'){
                return '承保缴费申请'
            } else if(status==='confirm'){
                return '承保缴费确认'
            } else if(status==='end'){
                return '承保缴费完结'
            }
        }
        $scope.getReturnFlag = function(returnFlag){
            if(returnFlag==='start'){
                return '未完成'
            } else if(returnFlag==='success'){
                return '成功'
            } else if(returnFlag==='fail'){
                return '失败'
            }
        }

        $scope.getInsureLog = function(){
            var url = window.basePath + "/order/Insure/queryInsureLog?"
                + "pageNo="+$scope.pageNo2
                + "&pageSize="+$scope.pageSize2;


            $.each($scope.$$filter,function(k,v){
                if(!window.isEmpty(v)){
                    url+= "&"+k+"="+v;
                }
            });

            $http.get(url).success(function(re){
                if($scope.$$filter.$$queryType!=='logId'){
                    $scope.insureList = re.content.items;
                    $scope.page = re.content;
                    $scope.totalNumber = re.content.totalNum;
                    $scope.totalPage2 = re.content.totalPage;
                    $scope.checkPage();
                }else{
                    $scope.insureInfo = re.content;
                }
            })
        };

        $scope.showInsureInfo = function(logId){
            $scope.$$filter.logId = logId;
            $scope.$$filter.$$queryTypeLast=$scope.$$filter.$$queryType;
            $scope.$$filter.$$queryType='logId';
            $scope.getInsureLog();
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
            $scope.getInsureLog();
        }

        //跳转页码
        $scope.pageNumber2 = function (num) {
            if (num < 1 || $scope.totalPage2 < num) {
                return;
            }
            $scope.pageNo2 = num;
            $scope.getInsureLog();
        }

        $scope.pageCur2=function(index){
            $scope.pageIndex2=index;
        }

        $scope.init();
    }
})();