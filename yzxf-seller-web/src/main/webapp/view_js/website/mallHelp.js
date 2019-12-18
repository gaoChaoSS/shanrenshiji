(function (angular, undefined) {
    var path = window.location.href.split('#')[1];
    var names = path.substring(1).split('/');
    var model = names[0];
    var entity = names[1];
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $script(['/js/canvasResize.js', '/js/binaryajax.js', '/js/exif.js', '/js/imageUpload.js']);

        $scope.openParent = function(index){
            if(window.isEmpty($scope.menuList[index].status) || $scope.menuList[index].status===0){
                $scope.menuList[index].status=-1;
            }else{
                $scope.menuList[index].status=0;
            }
        };

        $scope.setMenuBtn = function(index,num,status){
            var cur = $scope.menuList[index].status;
            if(window.isEmpty(status)){
                if(window.isEmpty(cur) || cur===0){
                    $scope.menuList[index].status=num;
                }else{
                    $scope.menuList[index].status=0;
                }
            }else{
                $scope.menuList[index].status=status;
            }

            $scope.addMenuName[1]="";
            // 同时只能有一个输入框存在，打开一个，关闭其他所有输入框
            // if(num===1){
            //     for(var i=0,len=$scope.menuList)
            // }
        };

        //获取目录
        $scope.getMenu = function(){
            var url = window.basePath + "/crm/Article/getMenu?name='商城帮助'";
            $http.get(url).success(function(re){
                $scope.parentMenu = re.content;
                $scope.menuList = re.content.items;
                for(var i=0,len=$scope.menuList.length;i<len;i++){
                    $scope.menuList[i]["status"] = -1;
                    for(var j=0,jlen=$scope.menuList[i].length;j<jlen;j++){
                        $scope.menuList[i][j]["status"] = -1;
                    }
                }
            });
        }
        //添加目录
        $scope.addMenu = function(entity,curIndex){
            var addIndex = entity._id === $scope.parentMenu._id?0:1;
            var newName = $scope.addMenuName[addIndex];
            if(entity===null || window.isEmpty(entity.pid)){
                malert("请输入目录上级");
                return;
            }
            if(window.isEmpty(newName)){
                $scope.setMenuBtn(curIndex,1);
                return;
            }

            if(!window.isEmpty(entity.items)){
                var flag = true;
                entity.items.forEach(function(val,index,arr){
                    if(arr[index].name===newName){
                        malert("该目录下已存在相同名称的子目录");
                        flag = false;
                    }
                });
                if(!flag){
                    return;
                }
            }

            var url = window.basePath + "/crm/Article/addMenu";
            var data = {
                name:newName,
                pid:entity._id
            };
            $http.post(url,data).success(function(re){
                if(window.isEmpty(curIndex) && curIndex!==0){
                    re.content["status"]=-1;
                    $scope.menuList.push(re.content);
                }else{
                    if(window.isEmpty($scope.menuList[curIndex].items)){
                        $scope.menuList[curIndex]["items"]=[re.content];
                    }else{
                        $scope.menuList[curIndex].items.push(re.content);
                    }
                    $scope.setMenuBtn(curIndex,1);
                }

                $scope.addMenuName[addIndex]="";
                malert("添加成功");
            });
        };

        //删除顶级目录
        $scope.delMenu=function(_id,index){
            var url = window.basePath + "/crm/Article/delMenu";
            $http.post(url,{_id:_id}).success(function(){
                $scope.menuList.splice(index,1);
                $scope.curArt='';
                malert("删除成功");
            });
        };

        //删除文章段落
        $scope.delContent=function(index){
            $scope.curArt.contents.splice(index,1);
        };

        //自动获取焦点
        // app.directive('autoFocus', function(){
        //     return function(scope, element){
        //         setTimeout(function() {
        //             element[0].focus();
        //         },200);
        //     };
        // });

        //显示文章
        $scope.showArt=function(item,outIndex,index){
            if(!window.isEmpty($scope.curArt) && $scope.curArt.status===1 && $scope.curArt.pId!==item._id
                && (window.isEmpty($scope.curArt.isInit) || !$scope.curArt.isInit)){
                $scope.getWin(3);
                return;
            }
            if(window.isEmpty(item._id)){
                $scope.curArt=[];
                malert("获取文章失败");
                return;
            }
            $scope.curItem = item;
            $scope.curItem["outIndex"]=outIndex;
            $scope.curItem["index"]=index;
            var url = window.basePath + "/crm/Article/getArticle?pId="+item._id;
            $http.get(url).success(function(re){
                if(!window.isEmpty(re.content) && !window.isEmpty(re.content._id)){
                    $scope.curArt=re.content;
                    $scope.updateMenuByIndex();
                    $scope.curArt["status"]=0;
                }else{
                    $scope.initArt(item);
                }
            });
        };

        //更新目录：更新文章后，若名字变动，更改对应的目录名称
        $scope.updateMenuByIndex=function(){
            var outIndex = $scope.curItem.outIndex;
            var index = $scope.curItem.index;
            if(window.isEmpty($scope.curArt)){
                $scope.menuList[outIndex].items.splice(index,1);

            }else if($scope.menuList[outIndex].items[index].name!==$scope.curArt.title){
                $scope.menuList[outIndex].items[index].name=$scope.curArt.title;
            }
        };

        //添加一段 0文字或 1图片，
        $scope.addContents=function(type){
            $scope.curArt.contents.push({desc:'',type:type});
        };

        //初始化新文章
        $scope.initArt=function(item){
            //注意，目录是pid，文章是pId
            $scope.curArt = {
                title:item.name,
                pId:item._id,
                status:1,
                canUse:false,
                isInit:true,
                contents:[{
                    desc:'',type:0
                }]
            };
        };

        // $scope.getWord=function(){
        //     var text = $(".art-edit").eq(0).html();
        // };

        $scope.uploadFile = function (inputObj,index) {
            window.uploadWinObj = {
                one: true,
                entityName: 'User',
                entityField: 'icon',
                entityId: genUUID(),
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        $scope.curArt.contents[index].desc = options.fileId;
                    });
                }
            };
            window.uploadWinObj.files = inputObj.files;
            window.uploadFile();
        };

        // 将富文本转换为纯文本；后续考虑富文本
        $scope.checkEdit = function(){
            for(var i=0,count=0,len=$scope.curArt.contents.length;i<len;i++){
                if($scope.curArt.contents[i].type===0){
                    $scope.curArt.contents[i].desc = $(".art-edit").eq(count).text();
                    count++;
                }
            }
        };

        // 保存
        $scope.saveArt=function(){
            if(window.isEmpty($scope.curArt.pId)){
                malert("获取上级目录失败");
                return;
            }
            if(window.isEmpty($scope.curArt.title)){
                malert("请输入标题");
                return;
            }
            if(window.isEmpty($scope.curArt.contents)){
                malert("文章内容为空");
                return;
            }
            if($scope.curArt.contents.length>1024000){
                malert("文章内容过多");
                return;
            }

            $scope.checkEdit();
            var url = window.basePath + "/crm/Article/saveArticle";
            $http.post(url,$scope.curArt).success(function(re){
                $scope.curArt.status=0;
                $scope.curArt["updateTime"]=re.content.updateTime;
                $scope.updateMenuByIndex();
                malert("保存成功");
            });
        };

        //删除文章和子目录
        $scope.delArt=function(){
            var url = window.basePath + "/crm/Article/delArticle";
            var data={
                _id:$scope.curArt._id,
                pId:$scope.curArt.pId
            }
            $http.post(url,data).success(function(re){
                $scope.curArt='';
                $scope.updateMenuByIndex();
                malert("保存成功");
            });
        };

        // 打开窗口
        $scope.getWin=function(index) {
            $scope.curWin=$scope.winList[index];
            $scope.curWin.isShow=true;
        };

        $scope.addMenuName = new Array(2);
        $scope.curWin = {};
        $scope.winList=[
            {
                title:'是否退出编辑？',
                isShow:true,
                action:function(){
                    $scope.curArt.status=0;
                    $scope.curWin.isShow=false;
                }
            },{
                title:'是否保存？',
                isShow:true,
                action:function(){
                    $scope.curWin.isShow=false;
                    $scope.saveArt();
                }
            },{
                title:'是否删除？',
                isShow:true,
                action:function(){
                    $scope.curWin.isShow=false;
                    $scope.delArt();
                }
            },{
                title:'当前文章尚未保存，是否退出编辑？',
                isShow:true,
                action:function(){
                    $scope.curArt.status=0;
                    $scope.curWin.isShow=false;
                    $scope.curArt=[];
                }
            }
        ];

        $scope.getMenu();
    });
})(angular);
