(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        // 参数
        $scope.initFilter = function () {
            $scope.filter = {
                type: '',
                isType: '1',
            };
        };

        $scope.isEdit = false;

        $scope.setPage = function (index) {
            $scope.filter.type = $scope.menuList[index].type;
            $scope.curPage = $scope.menuList[index];
            $scope.getParameter();
        }

        $scope.getParameter = function () {
            var url = window.basePath + "/order/Parameter/getParameter?1=1";
            $.each($scope.filter, function (k, v) {
                if (!window.isEmpty(v)) {
                    url += "&" + k + "=" + v;
                }
            });
            $http.get(url).success(function (re) {
                if (window.isEmpty($scope.filter.type)) {
                    $scope.menuList = re.content.items;
                    $scope.filter.isType = '';
                    $scope.setPage(0);
                } else {
                    $scope.curPage['$$isList'] = !window.isEmpty(re.content.items);
                    $scope.curData = $scope.curPage['$$isList'] ? re.content.items : re.content;
                    if (!$scope.curPage['$$isList']) {
                        $scope.getParameterLog();
                    }
                    $scope.updateData();
                }
            })
        }

        $scope.updateData = function () {
            if ($scope.curPage['$$isList']) {
                $.each($scope.curData, function (k, v) {
                    v['$$val'] = v['val'];
                });
                $scope.getCircle();
            } else {
                $scope.curData['$$val'] = $scope.curData['val'];
                $scope.getParameterLog();
            }
        };

        $scope.saveParameter = function () {
            var data = {};
            if ($scope.curPage['$$isList']) {
                data['items'] = [];
                var num = 0,flag = true;
                $.each($scope.curData, function (k, v) {
                    if($scope.curPage.unit==='%'){
                        if(!/^\d+(\.\d{1,3})?$/.test(v.val)){
                            flag = false;
                            malert("小数位数不能超过三位！");
                            return false;
                        }else{
                            num+=parseFloat(v.val);
                        }
                    }
                    if (v['$$val'] !== v['val']) {
                        data.items.push(v);
                    }
                });
                if(!flag){
                    return;
                }
                if (data.items === [] || data.items.length === 0) {
                    malert("参数没有改动");
                    return;
                }
                if($scope.curPage.unit==='%' && parseFloat($rootScope.getMoney(num))!==100){
                    malert("分配比例不等于100%! (差值"+$rootScope.getMoney(num-100)+"%)");
                    return;
                }
            } else {
                if ($scope.curData['$$val'] === $scope.curData['val']) {
                    malert("参数没有改动");
                    return;
                }
                data = $scope.curData;
            }

            var url = window.basePath + '/order/Parameter/saveParameter';
            if ($scope.curPage.$$isList) {
                url += 'More';
            }
            $http.post(url, data).success(function (re) {
                $scope.isEdit = false;

                $scope.updateData();
                malert("保存成功");
            })
        };

        $scope.cancelParameter = function () {
            if ($scope.curPage['$$isList']) {
                $.each($scope.curData, function (k, v) {
                    if (v['$$val'] !== v['val']) {
                        v['val'] = v['$$val'];
                    }
                });
            } else {
                if ($scope.curData['$$val'] !== $scope.curData['val']) {
                    $scope.curData['val'] = $scope.curData['$$val']
                }
            }
            $scope.isEdit = false;
        }

        $scope.setStatus = function (scope) {
            $scope[scope] = !$scope[scope];
        }

        $scope.getCircle = function (data) {
            $scope.pieChart(data);
        };

        // 最近10次修改记录
        $scope.getParameterLog = function () {
            var url = window.basePath + '/order/Parameter/getParameterLog?orderBy=1&pageNo=1&pageSize=10&type=' + $scope.curPage.type;
            $http.get(url).success(function (re) {
                $scope.lineData = re.content.items;
                $scope.lineChart(re.content.items);
            })
        }

        // 折线图
        $scope.lineChart = function (items) {
            $scope.budget = [];
            $.each(items, function (k, v) {
                $scope.budget[k] = parseFloat(v.val);
            });
            $('.container-line').highcharts({
                title: {
                    text: '最近十次修改记录'
                },
                subtitle: {
                    text: ''
                },
                yAxis: {
                    title: {
                        text: '金额'
                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'middle'
                },
                plotOptions: {
                    series: {
                        label: {
                            connectorAllowed: false
                        },
                        pointStart: 1,
                        pointInterval:1
                    }
                },
                series: [{
                    name: $scope.curPage.typeTitle,
                    data: $scope.budget
                }],
                responsive: {
                    rules: [{
                        condition: {
                            maxWidth: 500
                        },
                        chartOptions: {
                            legend: {
                                layout: 'horizontal',
                                align: 'center',
                                verticalAlign: 'bottom'
                            }
                        }
                    }]
                }
            });
        }

        // 饼图
        $scope.pieChart = function () {
            $scope.budget = [];
            $.each($scope.curData, function (k, v) {
                var map = {
                    name: v.title,
                    y: parseFloat(v.val),
                };
                $scope.budget.push(map);
            });

            $('.container').highcharts({
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false
                },
                title: {
                    text: ''
                },
                tooltip: {
                    headerFormat: '{series.name}<br>',
                    pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                            style: {
                                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                            }
                        }
                    }
                },
                series: [{
                    type: 'pie',
                    name: $scope.curPage.typeTitle,
                    data: $scope.budget
                }]
            });
        }

        $scope.initFilter();
        $scope.getParameter();
    });
})(angular);
