(function (angular, undefined) {
    var model = 'common';
    var entity = 'MData';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile, $templateCache) {
        $rootScope.model = model;
        $rootScope.entity = entity;

        var initGrid = false;
        var pid = null, filter = {};


        var compare = function (l, r, fields) {
            var isSame = true;
            $.each(fields.split(','), function (k, v) {
                if (l[v] != r[v]) {
                    isSame = false;
                    return false;
                }
            });
            return isSame;
        }


        $scope.mDataDef = [
            {name: 'name', title: '名称', width: 120, inputType: 'input', level: '0,1,2'}
            , {name: 'title', title: '标题', width: 150, inputType: 'input', level: '0,1,2'}
            , {name: 'desc', title: '描述', width: 150, inputType: 'input', level: '0,1,2'}
            , {name: 'minLength', title: '最小长度', inputType: 'number', width: 100, level: '2'}
            , {name: 'maxLength', title: '最大长度', inputType: 'number', width: 100, level: '2'}
            , {
                name: 'type',
                title: '类型',
                inputType: 'select',
                inputTypeList: 'string,int,long,double,boolean'.split(','),
                width: 80,
                level: '2',
            }
            , {
                name: 'inputType',
                inputType: 'select',
                title: '编辑方式',
                inputTypeList: 'input,password,number,textarea,htmlEdit,select,selectMore,date,time,dateTime'.split(','),
                width: 100,
                level: '2'
            }
            , {name: 'sellerOwner', inputType: 'boolean', title: '商家表', width: 70, level: '1'}
            , {name: 'memberOwner', inputType: 'boolean', title: '会员表', width: 70, level: '1'}
        ];


        $scope.isShowField = function (item) {
            if ($scope.selectParent == null) {
                return false;
            }
            return (',' + item.level + ',').indexOf(',' + ($scope.selectParent.level + 1) + ',') > -1;
        }
        $scope.newData = {};
        $scope.isEditObj = {};
        $scope.saveData = function (item, field) {
            if (window.isEmpty(item.name)) {
                malert('名称必须填写!');
                return;
            }
            var add = false;
            if (item._id == null) {
                item._id = genUUID();
                item.pid = $scope.selectParent._id;
                add = true;
            }

            if (window.isEmpty(item.pid)) {
                malert('必须设置pid!');
                return;
            }

            if (field != null) {
                item['$$old_' + field] = item['$$old_' + field] == null ? '' : item['$$old_' + field];
                item[field] = item[field] == null ? '' : item[field];
                if (item['$$old_' + field] == item[field]) {
                    delete $scope.isEditObj[item._id + '_' + field];
                    return;
                }
            }

            var url = window.basePath + '/common/MData/save'
            $http.put(url, item).success(function (re) {
                malert('保存成功!');
                getSyncList();

                if (add) {
                    item._id = null;
                } else {
                    delete $scope.isEditObj[item._id + '_' + field];
                }
            }).error(function () {
                if (field != null && item['$$old_' + field]) {
                    item[field] = item['$$old_' + field];
                }
                delete $scope.isEditObj[item._id + '_' + field];
            });
        }
        $scope.selectText = function ($event) {
            console.log($event);
            $event.currentTarget.focus();
        }
        $scope.delItem = function (item) {
            if (confirm('是否确定删除该条数据?')) {
                var url = window.basePath + '/common/MData/del'
                $http.post(url, {
                    _id: item._id
                }).success(function (re) {
                    malert('操作成功!');
                    getSyncList();
                });
            }
        }
        $scope.delRemoveItem = function (item) {
            if (confirm('是否确定删除远程数据?')) {
                var url = window.basePath + '/common/MData/delRemoveItem'
                $http.post(url, {
                    _id: item._id
                }).success(function (re) {
                    malert('操作成功!');
                    getSyncList();
                });
            }
        }
        $scope.upload = function (item) {
            if (confirm('是否确定使用本地数据覆盖远程?')) {
                var url = window.basePath + '/common/MData/upload'
                $http.post(url, {
                    _id: item._id
                }).success(function (re) {
                    malert('操作成功!');
                    getSyncList();
                });
            }
        }

        $scope.download = function (item) {
            if (confirm('是否确定使用远程数据覆盖本地?')) {
                var url = window.basePath + '/common/MData/download'
                $http.post(url, {
                    _id: item._id
                }).success(function (re) {
                    malert('操作成功!');
                    getSyncList();
                });
            }
        }

        var getSyncList = function () {
            var url = window.basePath + '/common/MData/getSyncMDataByPid?pid=' + $scope.selectParent._id;
            $http.get(url).success(function (re) {
                //已本地为基础
                $scope.result = re.content;
                $scope.remoteMap = {};
                $.each($scope.result.remoteList, function (k, v) {
                    $scope.remoteMap[v._id] = v;
                });

                $.each($scope.result.localList, function (k, v) {
                    var r = $scope.remoteMap[v._id];
                    if (r == null) {
                        v.$$localAdd = true;
                        return true;
                    }
                    v.desc = window.isEmpty(v.desc) ? '' : v.minLength;
                    v.minLength = window.isEmpty(v.minLength) ? 0 : v.minLength;
                    v.maxLength = window.isEmpty(v.maxLength) ? 64 : v.maxLength;
                    v.type = window.isEmpty(v.type) ? 'string' : v.type;
                    v.inputType = window.isEmpty(v.inputType) ? 'input' : v.type;

                    r.desc = window.isEmpty(r.desc) ? '' : r.minLength;
                    r.minLength = window.isEmpty(r.minLength) ? 0 : r.minLength;
                    r.maxLength = window.isEmpty(r.maxLength) ? 64 : r.maxLength;
                    r.type = window.isEmpty(r.type) ? 'string' : r.type;
                    r.inputType = window.isEmpty(r.inputType) ? 'input' : r.type;
                    //比对规则
                    if (v.level == 0) {//比对:name,title,desc
                        v.$$isUpdate = !compare(v, r, '_id,name,title,desc');
                    } else if (v.level == 1) {
                        v.$$isUpdate = !compare(v, r, '_id,name,title,desc,sellerOwner,memberOwner');
                    } else if (v.level == 2) {
                        v.$$isUpdate = !compare(v, r, '_id,name,title,desc,maxLength,minLength,modelName,entityName,type,inputType');
                    }
                    $scope.remoteMap[v._id].$$localExist = true;
                });

                $.each($scope.remoteMap, function (k, v) {
                    if (!v.$$localExist) {
                        v.$$remoteAdd = true;
                        $scope.result.localList.push(v);
                    }
                });
            })
        }
        var selectTree = function (event, obj) {
            $scope.selectParent = obj;
            if ($scope.selectParent.level == 2) {
                return;
            }

            $scope.allWidth = 50 + 60 + 100;
            $.each($scope.mDataDef, function (k, v) {
                if ($scope.isShowField(v)) {
                    $scope.allWidth += v.width;
                }

            })

            getSyncList();

        }

        $scope.showSyncCon = function (item) {

        }
        $scope.inputKeyUp = function ($event, item, field) {
            if ($event.keyCode == 27) {//取消
                if (field != null && item['$$old_' + field]) {
                    item[field] = item['$$old_' + field];
                }
                delete $scope.isEditObj[item._id + '_' + field];
            }
            if ($event.keyCode == 13) {//取消
                $scope.saveData(item, field);
                $event.stopPropagation();
                return false;
            }
        }
        $scope.getItemClass = function (item) {
            if (item.$$isUpdate) {
                return 'isUpdate';
            }
            if (item.$$remoteAdd) {
                return 'remoteAdd';
            }
            if (item.$$localAdd) {
                return 'localAdd';
            }
        }

        $scope.$on('tree_left/itemClicked_after', selectTree)

        $scope.$on('tree_left/init_end', function (event, obj) {
            window.reqUIAction($scope, 'tree_left/itemExpended');
            window.reqUIAction($scope, 'tree_left/itemClicked');
        });
        $scope.$on('grid_right/init_end', function (event, obj) {

        });
    });
})(angular);
