(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);
        $scope.tempGridFilter = '/view/website/t_websiteNewsManage_filter.jsp';
        $scope.entityTitle = "官网新闻管理";
        $scope.tempGridList = '/view/website/t_websiteNewsManage_grid.jsp';
        $scope.contentInfo = '/view/website/t_websiteNewsManage_editor.jsp';
        $scope.fullQueryApi = window.basePath + "/crm/Article/getWebSiteNewsList";
        $scope.descListJson={title:'',contents:[{desc:''}]};
        $scope.addFlag = true;
        $scope.showImg = '';
        $scope.uploadIndex=-1;
        initGrid($rootScope, $scope, $http);
        $scope.showAddInfo=function(){
            $scope.getArticleList();
            $scope.popWindowTemp =$scope.contentInfo;
            $scope.descListJson={
                title:'',
                contents:[{desc:''}],
                newsId:'',
                selectedAl:'1',
                source:'',
                desc:''
            };
            $scope.changeImg = '';
            $rootScope.showPopWin = true;
            $rootScope.showEditor = true;
        }
        $scope.getArticleList = function(){
            var url = window.basePath+'/crm/Article/getArticleList';
            $http.get(url).success(function(re){
                $scope.articleList = re.content.items;
                $scope.articleList.unshift({_id:'1',name: '请选择分类'});
            })
        }
        $scope.deleteNews = function(newsId){
            if(!confirm("是否删除该文章?")){
                return;
            }
            var url = window.basePath+'/crm/Article/deleteNews?newsId='+newsId;
            $http.get(url).success(function(re){
                malert("删除成功!");
                $scope.queryCurrentList();
            })
        }
        $scope.saveNews = function(){
            var contents=[];
            for(var i=0;i<$scope.descListJson.contents.length;i++){
                contents.push({"desc":$scope.descListJson.contents[i].desc,"img":$scope.descListJson.contents[i].img})
            }
            var id = window.isEmpty($scope.descListJson.newsId)?genUUID():$scope.descListJson.newsId;
            if(window.isEmpty($scope.descListJson.selectedAl)||$scope.descListJson.selectedAl=='undefined'||$scope.descListJson.selectedAl==1){
                malert('请选择分类');
                return;
            }
            if(window.isEmpty($scope.descListJson.title)) {
                malert('标题不能为空');
                return;
            }
            if(contents[0].desc==null||contents[0].desc=='undefined'){
                malert('请填写内容');
                return;
            }
            var url = window.basePath + '/crm/Article/saveNews';
            var data = {
                newsId:id,
                icon:$scope.changeImg,
                title:$scope.descListJson.title,
                contents:contents,
                pid:$scope.descListJson.selectedAl,
                source:$scope.descListJson.source,
                desc:$scope.descListJson.desc
            };
            $http.post(url,data).success(function(re){
                $rootScope.showPopWin = false;
                $rootScope.showEditor = false;
                $scope.queryCurrentList();
            })
        }
        $scope.showInfo=function(obj){
            $scope.getArticleList();
            $scope.popWindowTemp =$scope.contentInfo;
            $scope.descListJson={
                title:obj.title,
                contents:obj.contents,
                newsId:obj._id,
                selectedAl:obj.pid,
                source:obj.source,
                desc:obj.desc
            }
            $scope.changeImg=obj.icon;
            $rootScope.showPopWin = true;
            $rootScope.showEditor = true;
        }
        $scope.addText = function () {
            $scope.descListJson.contents.push({"desc": ""});
            $scope.addFlag = false;
        }

        $scope.setUploadIndex=function(index){
            $scope.uploadIndex=index;
        }

        $scope.removeText = function (index) {
            $scope.descListJson.contents.splice(index, 1);
        }
        $scope.newsUpload = function (inputObj) {
            window.uploadWinObj = {
                one: true,
                entityName: 'User',
                entityField: 'icon',
                entityId: genUUID(),
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.descListJson.contents[$scope.uploadIndex].img=options.fileId;
                    });
                }
            };

            window.uploadWinObj.files = inputObj.files;
            window.uploadFileBig();
        };
        $scope.uploadFile1 = function (inputObj) {
            window.uploadWinObj = {
                one: true,
                entityName: 'User',
                entityField: 'icon',
                entityId: genUUID(),
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.changeImg=options.fileId;
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFileBig();
        };
        $scope.initTextarea = function (e) {

            var self = $(e.target);
            //if (!$scope.hasInitTextarea[self]) {
            //    return;
            //}

            var _hasInitTextarea = self.data("hasInitTextarea");

            if (_hasInitTextarea) {
                return;
            } else {
                self.data("hasInitTextarea", true);
            }

            $("#_textareacopy").val($(self).val());

            var height = $("#_textareacopy")[0].scrollHeight < $("#_textareacopy").outerHeight() ? ($("#_textareacopy").outerHeight())
                : ($("#_textareacopy")[0].scrollHeight + (!browser.browser.mozilla ? 0 : $(self).fGetCha()));
            // $('#debug').append('<div>height:' + height + ',scrollHeight:' +
            // $("#_textareacopy")[0].scrollHeight + ',fGetCha:' +
            // $(this).fGetCha() + ',CHA:' + $("#_textareacopy").data('cha') +
            // '</div>');
            $(self).height((height));

        }
        window.browser = function () {
            function uaMatch(ua) {
                ua = ua.toLowerCase();
                var match = rwebkit.exec(ua) || ropera.exec(ua) || rmsie.exec(ua) || ua.indexOf("compatible") < 0 && rmozilla.exec(ua) || [];
                return {
                    browser: match[1] || "",
                    version: match[2] || "0"
                };
            }

            var rwebkit = /(webkit)\/([\w.]+)/, ropera = /(opera)(?:.*version)?[ \/]([\w.]+)/, rmsie = /(msie) ([\w.]+)/, rmozilla = /(mozilla)(?:.*? rv:([\w.]+))?/, browser = {}, ua = window.navigator.userAgent, browserMatch = uaMatch(ua);
            if (browserMatch.browser) {
                browser[browserMatch.browser] = true;
                browser.version = browserMatch.version;
            }
            return {
                browser: browser
            }
        }();
        window.autoTextArea = function(el) {
            // 新建一个textarea用户计算高度
            if (el.initEvents == null) {
                el.initEvents = true;
                if ($("#_textareacopy").size() == 0) {
                    var t = $('<textarea id="_textareacopy"></textarea>');
                    t.css({
                        position: "fixed",
                        zIndex: "20",
                        overflow: 'auto',
                        top: '-200px',
                        left: '500px'// -9999px
                    })
                    $('body').append(t);
                    // alert($(el).fOutHeight());
                }

                function change() {
                    $("#_textareacopy").val($(this).val());
                    console.log("fGetCha:"+$(this).fGetCha());
                    var height = $("#_textareacopy")[0].scrollHeight < $("#_textareacopy").outerHeight() ? ($("#_textareacopy").outerHeight())
                        : ($("#_textareacopy")[0].scrollHeight + (!browser.browser.mozilla ? 0 : $(this).fGetCha()));
                    // $('#debug').append('<div>height:' + height + ',scrollHeight:' +
                    // $("#_textareacopy")[0].scrollHeight + ',fGetCha:' +
                    // $(this).fGetCha() + ',CHA:' + $("#_textareacopy").data('cha') +
                    // '</div>');
                    $(this).height((height));
                }

                $(el).bind("propertychange", change);// for IE
                $(el).bind("input", change);// for !IE
                $(el).css({
                    'overflow': 'hidden',
                    'resize': 'none'
                });// 一处隐藏，必须的。// 去掉textarea能拖拽放大/缩小高度/宽度功能
                var cha = $("#_textareacopy")[0].scrollHeight - $("#_textareacopy").height();
                $("#_textareacopy").data('cha', cha < 0 ? 0 : cha);
                $(el).data('oldfWidth', $(el).outerWidth()).data('oldfHeight', $(el).outerHeight() + 4);
            }
            $("#_textareacopy").height($(el).data('oldfHeight')).width($(el).data('oldfWidth'));
        };

        $scope.showImgFun= function (fieldId) {
            $scope.showImg=fieldId;
        };
        $scope.closeImgFun=function(){
            $scope.showImg='';
        };
        $scope.delFileItem = function (imgId) {
            $scope[imgId] = "";
        };

    });
})(angular);
