(function(angular, undefined) {
    // 注意：
    // name必须全小写，不能有下划线，因为是标签的属性
    // 标签必须要有name属性，且ng-model一致
    var checkTypes = [ {
        name : 'integer',
        desc : '整数',
        reg : /^\-?\d*$/
    }, {
        name : 'float',
        desc : '小数',
        reg : /^\-?\d+((\.|\,)\d+)?$/
    }, {
        name : 'mobile',
        desc : '手机号码',
        reg : /^(1[3-9][0-9])[0-9]{8}$/
    }, {
        name : 'password',
        desc : '密码格式',
        reg : /^[A-Za-z0-9_]{6,20}$/
    }, {
        name : 'idcard',
        desc : '身份证',
        reg : /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i
    } ];

    $.each(checkTypes, function(k, v) {
        window.app.directive(v.name, function() {
            return {
                require : 'ngModel',
                link : function(scope, elm, attrs, ctrl) {
                    ctrl.$parsers.unshift(function(viewValue) {
                        if (v.reg.test(viewValue)) {
                            ctrl.$setValidity(v.name, true);
                            return viewValue;
                        } else {
                            ctrl.$setValidity(v.name, false);
                            return undefined;
                        }
                    });
                }
            };
        });
    })

    // 远程校验，检查电话号码是否存在
    window.app.directive('exist', function($http) {
        return {
            require : 'ngModel',
            link : function(scope, elm, attrs, ctrl) {
                elm.bind('blur', function() {
                    
                    var url = '';
                    if (attrs['exist'] == 'phone') {
                        url = window.frontBaseUrl + "/Member/checkNumber/" + elm.val();
                    }

                    $http({
                        method : 'GET',
                        url : url
                    }).success(function(data, status, headers, config) {
                        if (parseInt(data) == 0) {
                            ctrl.$setValidity('exist', true);
                        } else {
                            ctrl.$setValidity('exist', false);
                        }
                    }).error(function(data, status, headers, config) {
                        ctrl.$setValidity('servererror', false);
                    });
                });
            }
        };
    });

})(angular);
