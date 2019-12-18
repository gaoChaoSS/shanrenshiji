/**
 * 工作流设计
 *
 * @param angular
 * @param undefined
 */
(function (angular, undefined) {
    "use strict";
    window.app.directive('workflow', function () {
        //选择的对象
        var selectObjList = [], dragStatus = 0, dragStartX = null, dragStartY = null, startNode = null, endNode = null;

        var nodeMousedown = function (event, x, y) {//节点被按下
                if (event.shiftKey || event.ctrlKey || event.altKey || event.metaKey) {

                } else {
                    if (selectObjList.length > 0 && this.set.isSelected()) {

                    } else {
                        this.set.selectedOne();
                    }
                }
                dragStartX = x;
                dragStartY = y;
                dragStatus = 1;
                $.each(selectObjList, function (k, v) {
                    $.each(v, function (key, item) {
                        item.ox = item.type != "circle" ? item.attr("x") : item.attr("cx");
                        item.oy = item.type != "circle" ? item.attr("y") : item.attr("cy");

                        console.log('w_type', item.data());
                        //item.animate({"fill-opacity": .6}, 300);
                    });
                });
                event.stopPropagation();//停止冒泡
            },
            nodeMouseup = function (event) {
                //if (dragStatus == 1 && !(event.shiftKey || event.ctrlKey || event.altKey || event.metaKey)) {//未拖动且非多选模式
                //    this.set.selectedOne();
                //}
                if (dragStatus == 1) {
                    if (event.shiftKey || event.ctrlKey || event.altKey || event.metaKey) {
                        if (this.set.isSelected()) {
                            selectObjList.removeByObj(this.set);
                        } else {
                            selectObjList.addOrUpdate(this.set);
                        }
                        this.set.selected(!this.set.isSelected());
                    }
                } else if (dragStatus == 2) {

                }
            },
            docMouseMove = function (e) {
                //console.log('move', e);
                var dx = e.clientX - dragStartX, dy = e.clientY - dragStartY;
                if (dragStatus == 1 && (Math.abs(dx) > 5 || Math.abs(dy) > 5)) {
                    dragStatus = 2;
                }
                if (dragStatus == 2 && selectObjList.length > 0) {
                    $.each(selectObjList, function (kk, vv) {
                        $.each(vv, function (k, item) {
                            var att = item.type != "circle" ? {x: item.ox + dx, y: item.oy + dy} : {
                                cx: item.ox + dx,
                                cy: item.oy + dy
                            };
                            item.attr(att);
                        })
                    });
                }
            },
            docMouseUp = function (e) {
                console.log('up', e);
                dragStatus = 0;
            },
            dragger = function (x, y, event) {
                console.log('mouse drag');
                if (event.shiftKey || event.ctrlKey || event.altKey || event.metaKey) {
                    if (this.set.isSelected()) {
                        selectObjList.removeByObj(this.set);
                    } else {
                        selectObjList.addOrUpdate(this.set);
                    }
                    this.set.selected(!this.set.isSelected());
                } else {
                    if (selectObjList.length > 0) {

                    } else {
                        this.set.selectedOne();
                    }
                }
                dragStatus = 1;
                $.each(selectObjList, function (k, v) {
                    $.each(v, function (key, item) {
                        item.ox = item.type != "circle" ? item.attr("x") : item.attr("cx");
                        item.oy = item.type != "circle" ? item.attr("y") : item.attr("cy");
                        console.log('w_type', item.data());
                        //item.animate({"fill-opacity": .6}, 300);
                    });
                });
                console.log(event);
                event.stopPropagation();//停止冒泡
                return false;
            },
            move = function (dx, dy, event) {
                //var set = this.set;
                dragStatus = 2;

                $.each(selectObjList, function (kk, vv) {
                    $.each(vv, function (k, item) {
                        var att = item.type != "circle" ? {x: item.ox + dx, y: item.oy + dy} : {
                            cx: item.ox + dx,
                            cy: item.oy + dy
                        };
                        item.attr(att);
                    })
                });
            },
            up = function (event) {
                if (dragStatus != 2 && !(event.shiftKey || event.ctrlKey || event.altKey || event.metaKey)) {//未拖动的情况,只选中自己
                    this.set.selectedOne();
                }
                dragStatus = 0;
                console.log(event);
            };

        return {
            restrict: 'AE',
            transclude: true,
            replace: true,
            scope: {
                id: '@'
            },
            templateUrl: '/temp/workflow.html?_v=' + window.angular_temp_version,
            controller: function ($rootScope, $scope, $element, $http, $q, $compile, $popWindow, $templateCache) {
                if (window.isEmpty($scope.id)) {
                    $scope.id = 'workflow_' + getRandom(100000000);
                    $element.attr('id', $scope.id);
                }

                $scope.toolbar = [
                    {
                        _id: 'save',
                        iconClass: 'icon-floppy-disk',
                        name: '保存',
                        title: '保存到服务器',
                        clickAction: $scope.saveData
                    }
                    , {type: 'hr'}
                    , {
                        _id: 'line',
                        iconClass: 'icon-arrow-down-right2',
                        name: '箭头',
                        title: '连接箭头',
                        clickAction: $scope.saveData
                    }
                    , {
                        _id: 'start',
                        iconClass: 'icon-radio-checked green',
                        name: '启动',
                        title: '流程启动节点',
                        clickAction: function () {
                            $scope.r.addNode({
                                type: 'start',
                                x: 200 + Math.random() * 100,
                                y: 200 + Math.random() * 100
                            })
                        }
                    }
                    , {
                        _id: 'node',
                        iconClass: 'icon-checkbox-unchecked nodeToolBtn',
                        name: '节点',
                        title: '任务节点',
                        clickAction: function () {
                            $scope.r.addNode({type: 'node', x: 200 + Math.random() * 100, y: 200 + Math.random() * 100})
                        }
                    }
                    , {
                        _id: 'end',
                        iconClass: 'icon-radio-checked2 high',
                        name: '结束',
                        title: '结束节点',
                        clickAction: function () {
                            $scope.r.addNode({type: 'end', x: 200 + Math.random() * 100, y: 200 + Math.random() * 100})
                        }
                    }
                ];


                var nodeSet = {};
                //扩展方法
                Raphael.fn.addNode = function (json) {
                    json || (json = []);
                    if (json instanceof Object) {
                        json = [json];
                    }
                    json.forEach(function (v, k) {
                        var rJson = null;
                        if (v.type == 'start') {
                            if (startNode != null) {
                                return true;
                            }
                            var max = 25;
                            rJson = [
                                {
                                    type: 'rect',
                                    x: v.x - max,
                                    y: v.y - max,
                                    width: max * 2,
                                    height: max * 2,
                                    fill: 'rgba(70%,70%,70%,30%)',
                                    stroke: 'rgba(255,0,0,0.4)',
                                    'stroke-dasharray': '-',
                                    'stroke-width': 2,
                                    r: 4,
                                    opacity: 0,
                                    data: {'type': 'back'}
                                },
                                {
                                    type: "circle",
                                    cx: v.x,
                                    cy: v.y,
                                    r: max - 5,
                                    fill: '#fff',
                                    'stroke': 'green',
                                    'stroke-width': 6
                                },
                                {
                                    type: "circle",
                                    cx: v.x,
                                    cy: v.y,
                                    r: 8,
                                    fill: 'green',
                                    'stroke-width': 0
                                }
                            ];
                        } else if (v.type == 'node') {
                            var w = 120, h = 40;
                            rJson = [
                                {
                                    type: 'rect',
                                    x: v.x - 6,
                                    y: v.y - 6,
                                    width: w + 12,
                                    height: h + 12,
                                    fill: 'rgba(70%,70%,70%,30%)',
                                    stroke: 'rgba(255,0,0,0.4)',
                                    'stroke-dasharray': '-',
                                    'stroke-width': 2,
                                    r: 4,
                                    opacity: 0,
                                    data: {'type': 'back'}
                                },
                                {
                                    type: "rect",
                                    x: v.x,
                                    y: v.y,
                                    width: w,
                                    height: h,
                                    r: 6,
                                    fill: '#fff',
                                    'stroke': 'royalblue',
                                    'stroke-width': 3
                                }, {
                                    type: "text",
                                    x: v.x,
                                    y: v.y,
                                    text: '领导审批'
                                }
                            ]
                        } else if (v.type == 'end') {
                            if (endNode != null) {
                                return true;
                            }
                            var max = 25;
                            rJson = [
                                {
                                    type: 'rect',
                                    x: v.x - max,
                                    y: v.y - max,
                                    width: max * 2,
                                    height: max * 2,
                                    fill: 'rgba(70%,70%,70%,30%)',
                                    stroke: 'rgba(255,0,0,0.4)',
                                    'stroke-dasharray': '-',
                                    'stroke-width': 2,
                                    r: 4,
                                    opacity: 0,
                                    data: {'type': 'back'}
                                },
                                {
                                    type: "circle",
                                    cx: v.x,
                                    cy: v.y,
                                    r: max - 5,
                                    fill: 'red',
                                    'stroke': 'red',
                                    'stroke-width': 0
                                },
                                {
                                    type: "circle",
                                    cx: v.x,
                                    cy: v.y,
                                    r: 8,
                                    fill: '#fff',
                                    'stroke': 'red',
                                    'stroke-width': 0
                                }
                            ];
                        } else if (v.type == 'line') {
                            rJson = [
                                {
                                    type: 'path',
                                    x: v.x - max,
                                    y: v.y - max,
                                    width: max * 2,
                                    height: max * 2,
                                    fill: 'rgba(70%,70%,70%,30%)',
                                    stroke: 'rgba(255,0,0,0.4)',
                                    'stroke-dasharray': '-',
                                    'stroke-width': 2,
                                    r: 4,
                                    opacity: 0,
                                    data: {'type': 'back'}
                                }]
                        }
                        if (rJson) {
                            var st = $scope.r.add(rJson).attr({cursor: "move"}).mousedown(nodeMousedown).mouseup(nodeMouseup).selectedOne();

                            if (v.type == 'start') {
                                startNode = st;
                            } else if (v.type == 'end') {
                                endNode = st;
                            } else if (v.type == 'node') {
                                console.log('font pos', st.items[2].getBBox());
                                st.fixTextPos();
                            }
                        }
                    });
                }
                //对一个对象执行选择或取消
                Raphael.st.mousedown = function (h) {
                    var my = this;
                    my.forEach(function (v, k) {
                        v.mousedown(h)
                    });
                    return this;
                }

                //修正文字在框中的位置
                Raphael.st.fixTextPos = function () {
                    var p = this.items[0].getBBox();
                    var t = this.items[2].getBBox();
                    var width = Math.min(p.width, t.width), height = Math.min(p.height, t.height),
                        x = p.x + (p.width) / 2, y = p.y + (p.height) / 2;
                    this.items[2].attr({width: width, height: height, x: x, y: y})
                }

                //选择唯一某个对象
                Raphael.st.selectedOne = function () {
                    if (selectObjList.length > 0) {
                        $.each(selectObjList, function (k, v) {
                            v.selected(false);
                        });
                    }
                    selectObjList.length = 0;
                    selectObjList.push(this);
                    return this.selected(true);
                }
                Raphael.st.selected = function (isSelected) {
                    var my = this;
                    if (my.items.length > 0) {
                        my.items[0].attr('opacity', isSelected ? 1 : 0);
                    }
                    my.forEach(function (v, k) {
                        v.set || (v.set = my);
                    });
                    return this;
                }
                Raphael.st.isSelected = function () {
                    var my = this;
                    if (my.length > 0) {
                        return my.items[0].attr('opacity') == 1;
                    }
                    return false;
                }

                $scope.saveData = function () {
                }

                $(document).on('mousemove', docMouseMove).on('mouseup', docMouseUp);
                window.exportUiApi($scope, ['saveData']);
                $rootScope.$broadcast($scope.id + '/init_end');
            },

            compile: function (element, attrs, transclude) {
                return function ($scope, element, attrs, ctrl, transclude) {
                    //console.log(, element.height());
                    $scope.r = Raphael(element[0], parseInt(element.width()), parseInt(element.height()));
                    $scope.r.canvas.addEventListener('mousedown', function () {
                        malert('end');
                        if (selectObjList.length > 0) {
                            $.each(selectObjList, function (k, v) {
                                v.selected(false);
                            });
                        }
                        selectObjList.length = 0;
                    }, false);
                    $scope.r.canvas.addEventListener('mouseup', function () {
                    }, false);

                    $scope.r.addNode([{type: 'start', x: 100, y: 300}])
                };
            }
        }
    });

})(angular);