(function(angular, undefined) {
    // test data
    var tree_test = {
        modelName : 'common',
        entityName : 'Menu',
        name : '数的名称'
    };
    window._genInputStr = function(v, isForm) {
        var str = '';
        $rootScope.formMData[v.name] = v;

        if (isForm) {
            str += '<div class="row">';
            str += '<div class="headerCon">' + v.title + '</div>';
        }
        var fname = 'formMData.' + v.name;
        var isErrClass = fname + ".__inputError==null||" + fname + ".__inputError==''?'':'err'";
        str += '<div class="inputCon">';

        v.type || (v.type = 'string');
        v.inputType || (v.inputType = v.type);
        if (v.inputType == 'textarea') {
            str += '    <textarea style="width:100%;height:100%;" ng-class="' + isErrClass + '" ng-blur="formDataSumbit();checkInputData(\'' + v.name + '\');" ng-model="formData.' + v.name
                    + '" class="fl"></textarea>';
        } else if (v.inputType == 'img') {
            str += '    <input readonly="true" ng-click="showDateTime(formData,\'' + v.name + '\');" ng-model="formData.' + v.name + '" class="fl"/>';
        } else if (v.inputType == 'imgMuti') {
            str += '    <input readonly="true" ng-click="showDateTime(formData,\'' + v.name + '\');" ng-model="formData.' + v.name + '" class="fl"/>';
        } else if (v.inputType == 'date') {
            str += '    <input readonly="true" ng-click="showDateTime(formData,\'' + v.name + '\');" ng-model="formData.' + v.name + '" class="fl"/>';
        } else if (v.inputType == 'boolean') {
            var booleanClass = "formData." + v.name + "?'icon-jianchacheck35 iconfont':'icon-cross'";
            str += '    <div ng-click="formData.' + v.name + '=!formData.' + v.name + ';formDataSumbit();" class="fl stateBoolen" ng-class="' + booleanClass + '"></div>';
        } else {
            str += '    <input type="text" ng-blur="formDataSumbit();checkInputData(\'' + v.name + '\');" ng-class="' + isErrClass + '" ng-model="formData.' + v.name + '" class="fl"/>';
        }
        if (v.inputType != 'boolean') {
            str += '    <div style="padding:5px;" ng-show="' + fname + '.__inputError==\'\'" class="fl green icon-jianchacheck35 iconfont"></div>';
            str += '    <div style="padding:4px" ng-show="' + fname + '.__inputError!=null&&' + fname + '.__inputError.length>0" class="fl highlight">{{' + fname + '.__inputError}}</div>';
        }
        str += '    <div class="clearDiv"></div>';
        str += '</div>';
        if (isForm) {
            str += '</div>';
        }
        return str;
    }
    window.formCtrl = function(actionPath, form, mData, $http, $element, $compile) {
        // 动态构建新加数据行
        form.actions = {
            showForm : function(p) {
                var mark = $element('<div class="mark"></div>').show();
                var popWin = $element('<div class="popWin"></div>');
                $(popWin).show().showCenter(800, 500);
                form.formData = p != null ? p : {};
                form.formMData = {};// 构造元数据map
                // 构造表单内容
                var str = '';
                str += '<form class="con hasBtn" ng-submit="formDataSumbit(true);">';
                str += '<div class="title">{{popWinTitle}}</div>';
                str += '<div class="closeBtn icon-cross" ng-click="closePopWin();"></div>';
                str += '<div class="winContent"><div class="formCon" style="margin:4px">';
                $.each(mData, function(k, v) {
                    if (v.setByServer) {
                        return true;
                    }
                    str += _genInputStr(v, true);
                });
                str += '</div>';
                str += '</div>';
                // actionCon
                str += '<div class="btnCon">';
                str += '<button class="button highT" type="submit">保存</button>';
                str += '<button class="button" ng-click="closePopWin();" type="button">关闭</button>';
                str += '</div>';
                // set form attr;
                str += '</form>';

                $rootScope.popWinTitle = '保存数据';
                $rootScope.formDataSumbit = function(isSaveAll) {
                    if ($scope.grid.isTree) {
                        $rootScope.formData.pid = $scope.tree.__selecteTreeItem;
                    }
                    $rootScope.saveData($scope.grid.actionPath, $rootScope.formData, function() {
                        if (isSaveAll) {
                            malert('保存成功！');
                        }
                    });
                };
                $rootScope.closePopWin = function() {
                    $rootScope.formData = {};
                    $rootScope.formMData = {};
                    $('#mark,#popWin').hide();
                }
                if (p != null) {
                    $rootScope.checkFormData();
                }

                angular.element('#popWin').empty().append(str);

                var link = $compile(angular.element('#popWin').contents());
                link($rootScope);
            }
        }

        return form;
    }

})(angular);