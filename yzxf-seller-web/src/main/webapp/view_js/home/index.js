/**
 * Created by tianchangsen on 17/1/23.
 */
(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        //$scope.model = model;
        //$scope.entity = entity;

        //$scope.selectedDate1 = 1;
        //$scope.selectedDate2 = 1;
        //$scope.selectedDate3 = 1;
        //
        //$scope.memberList = [];
        //$scope.pageNo = 1;
        //$scope.pageSize = 10;
        //
        //$scope.isNullPageM = false;
        ////显示页数
        //$scope.pageList = [];
        //
        //$scope.isNullNumber = function (num) {
        //    if (window.isEmpty(num)) {
        //        return 0;
        //    }
        //    return num;
        //}
        ////生成圆饼图
        //$scope.create=function(item,myStart,part,total,title,color){
        //    if(window.isEmpty(part)){
        //        part=0;
        //    }
        //    if(window.isEmpty(total)){
        //        total=0;
        //    }
        //
        //    $("#"+item).children().remove();
        //    $("#"+item).append(
        //        "<div id=\""+myStart+"\" data-dimension=\"150\" data-text=\"+"+part+"\" data-total=\""+total+"\" data-part=\""+part+"\""+
        //            "data-info=\"New Clients\""+
        //            "data-width=\"4\" data-fontsize=\"15\" data-percent=\"17.5\" data-fgcolor=\""+color+"\""+
        //            "data-bgcolor=\"#DBDBDB\" data-fill=\"#fff\"></div>"+
        //        "<div class=\"frTextCon\">"+
        //            "<div class=\"font16px\">"+
        //                title+
        //                //"<span class=\"iconfont icon-jiaoyijine\" style=\"font-size: 18px;color:"+color+"\"></span>"+
        //            "</div>"+
        //            "<div class=\"font30px\">"+total+"</div>"+
        //        "</div>"
        //    );
        //    $("#"+myStart).circliful();
        //}
        //
        ////获取 月/日 统计数据
        //$scope.getCountData = function () {
        //    var url = window.basePath + '/order/OrderInfo/getCountData?selectedDate=' + $scope.selectedDate1;
        //    $http.get(url).success(function (re) {
        //        $scope.countData = re.content;
        //        $scope.create("roundItem1","myStat1",Math.floor($scope.countData.addMember),Math.floor($scope.countData.totalMember),"注册会员","#ED5050");
        //        $scope.create("roundItem2","myStat2",Math.floor($scope.countData.addMemberActive),Math.floor($scope.countData.totalMemberActive),"激活会员","#940000");
        //        $scope.create("roundItem3","myStat3",Math.floor($scope.countData.addPension),Math.floor($scope.countData.totalPension),"养老金","#F16DB9");
        //        $scope.create("roundItem4","myStat4",Math.floor($scope.countData.addAgentIncome),Math.floor($scope.countData.totalAgentIncome),"代理商收益","#E58619");
        //        $scope.create("roundItem5","myStat5",Math.floor($scope.countData.addFactorIncome),Math.floor($scope.countData.totalFactorIncome),"发卡点收益","#3a9e01");
        //        $scope.create("roundItem6","myStat6",Math.floor($scope.countData.addSellerIncome),Math.floor($scope.countData.totalSellerIncome),"商家交易额","#731986");
        //    });
        //}
        //
        //$scope.getMemberList = function () {
        //    //var startDateLong = $scope.startDate;
        //    //var endDateLong = $scope.endDate;
        //    //if (startDateLong != null) {
        //    //    startDateLong = startDateLong.getTime();
        //    //}
        //    //if (endDateLong != null) {
        //    //    endDateLong = endDateLong.getTime();
        //    //}
        //    //if (startDateLong != null && endDateLong != null) {
        //    //    if (startDateLong >= endDateLong) {
        //    //        malert("开始日期不能大于等于结束日期");
        //    //        return;
        //    //    }
        //    //}
        //
        //    var url = window.basePath + '/order/OrderInfo/getMemberRank';
        //    var date = {
        //        //startDate: startDateLong,
        //        //endDate: endDateLong,
        //        //search: $scope.search,
        //        pageNo: $scope.pageNo,
        //        pageSize: $scope.pageSize
        //    }
        //    $http.post(url, date).success(function (re) {
        //        $scope.memberList = [];
        //        $.each(re.content.memberList, function (k, v) {
        //            $scope.memberList.push(v);
        //        });
        //        $scope.totalNumber = re.content.totalNum;
        //        $scope.totalPage = re.content.totalPage;
        //
        //        //页码集合
        //        $scope.pageList = [];
        //        //当前显示的页码从第几页开始
        //        var listCur = 1;
        //        var listCurCount = 5;
        //        if ($scope.pageNo <= 5 && $scope.totalPage <= 5) {
        //            listCur = 1;
        //            listCurCount = $scope.totalPage;
        //        } else {
        //            listCur = $scope.pageNo - 2;
        //            if (listCur <= 1) {
        //                listCur = 1;
        //            } else if (($scope.pageNo > $scope.totalPage - 3 && listCur >= 4) || $scope.totalPage == 6) {
        //                listCur = $scope.totalPage - 4;
        //            }
        //        }
        //        for (var index = 0; index < listCurCount; index++, listCur++) {
        //            $scope.pageList.push({num: listCur});
        //        }
        //        //选中的是第几个页码
        //        $scope.pageIndex = $scope.pageNo;
        //        //是否显示最后一页的页码
        //        $scope.isLastPage = ($scope.pageIndex < $scope.totalPage - 2) && $scope.totalPage > 5;
        //        //是否显示第一页的页码
        //        $scope.isFirstPage = $scope.pageIndex >= 4 && $scope.totalPage > 5;
        //
        //        $scope.isNullPageM = ($scope.totalPage <=1) || ($scope.totalPage == null);
        //    })
        //}


        //$scope.getCountData();
        //$scope.getMemberList();
    });
})(angular);
