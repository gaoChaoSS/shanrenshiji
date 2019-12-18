(function (angular, undefined) {

    var model = 'store';
    var entity = 'addStoreCommodity';
    window.app.register.controller(model + '_' + entity + '_Ctrl', function ($rootScope, $scope, $location, $http, $element, $compile) {
        $rootScope.isFirst=true;

        $scope.queryData = function () {
            if ($rootScope.pathParams._id != null && $rootScope.isFirst) {
                var url = window.basePath + "/order/ProductInfo/queryCommodity?_id=" + $rootScope.pathParams._id;
                $http.get(url).success(function (re) {
                    $scope.commodity = re.content;
                    if ($scope.isSelectType == true) {
                        $scope.commodity.operateType = $rootScope['$$addCommodity'].operateType;
                        $scope.commodity.operateValue = $rootScope['$$addCommodity'].operateValue;
                    }
                    $scope.queryProductInfoImgList('imgList');
                    $scope.queryProductInfoImgList('thumbnail');
                    $rootScope.isFirst=false;
                    if(window.isEmpty($scope.commodity.spec)){
                        $scope.commodity.spec=[];
                    }
                });
            } else if($rootScope.isFirst){
                if ($scope.commodity == null) {
                    $scope.commodity = {spec:[]};
                }
                if ($scope.commodity._id == null) {
                    $scope.commodity._id = genUUID();
                }
                $scope.isAdd = true;
                $rootScope.isFirst=false;
            }
            if ($scope.isSelectType == true) {
                $scope.commodity.operateType = $rootScope['$$addCommodity'].operateType;
                $scope.commodity.operateValue = $rootScope['$$addCommodity'].operateValue;
            }
        };
        $scope.checkPrice = function () {
            if ($scope.commodity.salePrice != null && $scope.commodity.salePrice < 0 && !/^d*(?:.d{0,2})?$/.test($scope.commodity.salePrice)) {
                malert("商品售价不能小于0,且最多两位小数");
                $scope.commodity.salePrice = 0;
                return;
            }
            if ($scope.commodity.oldPrice != null && $scope.commodity.oldPrice < 0 && !/^d*(?:.d{0,2})?$/.test($scope.commodity.oldPrice)) {
                malert("商品售价不能小于0,且最多两位小数");
                $scope.commodity.salePrice = 0;
                return;
            }
        }
        $scope.save = function (notBack) {
            if(window.isEmpty($scope.commodity.name)){
                malert("请输入商品名字");
                return;
            }
            if(window.isEmpty($scope.commodity.tag) || $scope.commodity.tag.length>20){
                malert("请输入商品标签,且在20个字符以内");
                return;
            }
            if (window.isEmpty($scope.commodity.salePrice) || $scope.commodity.salePrice < 0 || !/^[0-9]+(.[0-9]{1,2})?$/.test($scope.commodity.salePrice)) {
                malert("请输入商品售价,且不能小于0");
                //$scope.commodity.salePrice = 0;
                return;
            }
            if (window.isEmpty($scope.commodity.oldPrice) || $scope.commodity.oldPrice < 0 || !/^[0-9]+(.[0-9]{1,2})?$/.test($scope.commodity.oldPrice)
                || parseFloat($scope.commodity.salePrice)>=parseFloat($scope.commodity.oldPrice)) {
                malert("请输入商品原价,不能小于或等于售价");
                //$scope.commodity.salePrice = 0;
                return;
            }
            if(window.isEmpty($scope.commodity.operateType)){
                malert("请选择商品类别");
                return;
            }
            if(parseInt($scope.commodity.salePrice)>parseInt($scope.commodity.oldPrice)){
                malert("商品售价不能大于原价");
                return;
            }
            if(window.isEmpty($scope.commodity.spec) || $scope.commodity.spec.length==0){
                malert("请创建至少一条规格");
                return;
            }
            var url = window.basePath + "/order/ProductInfo/saveCommodity";
            $http.post(url, $scope.commodity).success(function (re) {
                if (!notBack) {
                    $rootScope.goBack();
                }
                //$scope.commodity = re.content;
            });
        };
        $scope.queryProductInfoImgList = function (field) {
            var entityId = $scope.commodity._id;
            var entityName = 'ProductInfo';
            var entityField = field;

            var url = window.basePath + '/file/FileItem/queryEntityFiles' +
                '?_entityName=' + entityName + '&_entityField=' + entityField + '&_entityId=' + entityId;
            $http.get(url).success(function (re) {
               $scope[field] = re.content.items;

           });
        };

        $scope.uploadFile = function (inputObj, type) {

            if(type == 'thumbnail'){
                if(!window.isEmpty($scope.thumbnail) && $scope.thumbnail.length>=4){
                    malert('缩略图最多只能上传4张');
                    return;
                }
            }
            window.uploadWinObj = {
                one: true,
                entityName: 'ProductInfo',
                entityField: type,
                entityId: $scope.commodity._id,
                callSuccess: function (options) {
                    $rootScope.$apply(function () {
                        if (type == 'icon') {
                            $scope.commodity.icon = options.fileId;
                        } else {
                            $scope.queryProductInfoImgList(type);
                        }
                    });
                }
            };

            window.uploadWinObj.files = inputObj.files;
            if(type=='thumbnail'){
                window.uploadFileBig();
            }else{
                window.uploadFile();
            }
        };

        $scope.delFileItem = function (index,name) {
            if (confirm('是否确认删除该图片!')) {
                $http.post(window.basePath + '/file/FileItem/deleteEntityFiles', {_id: $scope[name][index]._id}).success(function () {
                    $scope[name].splice(index, 1);
                })
            }
        };

        $scope.selectProductType = function () {
            $scope.isSelectType = true;
            $rootScope.goPage('store/operateType/userApply/addCommodity');
        };

        //创建一个规格
        $scope.addSpec=function(){
            $scope.showAddSpec=true;
            $scope.isShowSubmit=true;
            $scope.modifySpecId='';
            $scope.$$tempSpec={
                name:'',
                items:[],
                addMoney:[],
                isModify:[]
            };
            $scope.$$tempRow={
                items:'',
                addMoney:''
            };
        };

        //新建某一个规格的一种类别
        $scope.addSpecTrTemp=function(){
            if($scope.$$tempSpec.items.length>9){
                malert("最多建立10个种类!");
                return;
            }

            if(window.isEmpty($scope.$$tempRow.items) || $scope.$$tempRow.items.length>20) {
                malert("请输入正确的种类,且不能超过20个字符!");
                return;
            }

            if(window.isEmpty($scope.$$tempRow.addMoney)){
                $scope.$$tempRow.addMoney=0;
            }

            for(var i= 0,len=$scope.$$tempSpec.items.length;i<len;i++){
                if($scope.$$tempSpec.items[i]==$scope.$$tempRow.items){
                    malert("已经存在相同种类!");
                    return;
                }
            }

            if(!/^\d{0,5}(\.\d{1,2})?$/.test($scope.$$tempRow.addMoney)){
                malert("最多输入五位金额,两位小数!");
                return;
            }

            $scope.$$tempSpec.items.push($scope.$$tempRow.items);
            $scope.$$tempSpec.addMoney.push(parseFloat($scope.$$tempRow.addMoney));
            $scope.$$tempSpec.isModify.push({status:false});
            $scope.$$tempRow={
                items:'',
                addMoney:''
            };

            if($scope.$$tempSpec.items.length>0 &&
                ($scope.modifySpecId==null || $scope.modifySpecId=='') && $scope.modifySpecId!='0'){
                $scope.isShowSubmit=true;
            }else{
                $scope.isShowSubmit=false;
            }
        };

        //获取输入框焦点
        $scope.setFocus=function(index){
            $scope.$$tempSpec.isModify[index]={status:true};
        };

        //失去输入框焦点
        $scope.setBlur=function(entity,val,index){
            $scope.$$tempSpec[entity][index]=val;
            $scope.$$tempSpec.isModify[index]={status:false};
        };

        //删除种类
        $scope.delTr=function(index){
            $scope.$$tempSpec.items.remove(index);
            $scope.$$tempSpec.addMoney.remove(index);
            $scope.$$tempSpec.isModify.remove(index);

            if($scope.$$tempSpec.items.length>0 &&
                ($scope.modifySpecId==null || $scope.modifySpecId=='') && $scope.modifySpecId!='0'){
                $scope.isShowSubmit=true;
            }else{
                $scope.isShowSubmit=false;
            }
        };

        //删除规格
        $scope.delSpec=function(){
            if(!window.isEmpty($scope.modifySpecId) || $scope.modifySpecId===0){
                $scope.isShowDelWin=false;
                $scope.showAddSpec=false;
                $scope.commodity.spec.remove($scope.modifySpecId);
            }
        };

        //检查数据,并删除用于判断的字段
        $scope.checkFiled=function(entity,entityName){
            for(var i= 0,len=$scope.$$tempSpec[entity].length;i<len;i++){
                if(window.isEmpty($scope.$$tempSpec[entity][i]) && $scope.$$tempSpec[entity][i]!=0){
                    malert("获取数据空,请重新输入第"+(i+1)+"行'"+entityName+"'的数据");
                    return false;
                }
            }
            delete $scope.$$tempSpec.isModify;
            return true;
        };

        $scope.goBackWin=function(){
            if(!$scope.isShowSubmit){
                $scope.submitSpec();
            }else{
                $scope.showAddSpec=false;
                $scope.selectedIndex=-1;
            }
        };

        //检查规格名字
        $scope.checkSpecName=function(){
            if(window.isEmpty($scope.$$tempSpec.name)){
                malert("请输入规格名称");
                return false;
            }
            var len=$scope.commodity.spec.length;
            if(len==1 || len==0){
                return true;
            }
            for(var i= 0;i<len;i++){
                if($scope.selectedIndex!=-1 && $scope.selectedIndex==i){
                    continue;
                }
                if($scope.$$tempSpec.name==$scope.commodity.spec[i].name){
                    malert("已存在相同名称的规格,请更改");
                    return false;
                }
            }
            return true;
        };

        //提交
        $scope.submitSpec=function(){
            if(!$scope.checkSpecName()){
                return;
            }

            if($scope.$$tempSpec.items.length<1 || $scope.$$tempSpec.items.length>10){
                malert("请建立1~10个种类!");
                return;
            }
            if(window.isEmpty($scope.$$tempSpec.name) && $scope.$$tempSpec.name.length>10){
                malert("请输入10个字符以内的规格名称!");
                return;
            }
            if(!$scope.checkFiled('items','种类')){
                return;
            }
            if(!$scope.checkFiled('addMoney','加价')){
                return;
            }

            if($scope.isShowSubmit){
                $scope.commodity.spec.push($scope.$$tempSpec);
            }else{
                $scope.commodity.spec[$scope.selectedIndex]=$scope.$$tempSpec;
            }
            $scope.selectedIndex=-1;
            $scope.showAddSpec=false;
        };

        //修改一个规格
        $scope.modifySpec=function(index){
            $scope.selectedIndex=index;
            $scope.modifySpecId=index;
            $scope.showAddSpec=true;
            $scope.isShowSubmit=false;
            $scope.$$tempSpec=$scope.commodity.spec[index];

            $scope.$$tempSpec["isModify"]=[{}];
            for(var i= 0,len=$scope.$$tempSpec.items.length;i<len;i++){
                $scope.$$tempSpec["isModify"][i]={status:false};
            }
            $scope.$$tempRow={
                items:'',
                addMoney:''
            };
        };

        $rootScope.pageActionMap[$rootScope.currentPageUrl] = {
            onResume: function () {
                window.setWindowTitle($rootScope, $rootScope.pathParams._id == null ? '添加商品' : '保存商品');

                $scope.isShowDelWin=false;
                //if ($rootScope.isFirst){
                    $scope.queryData();
                //}
            }
        };
        $rootScope.pageActionMap[$rootScope.currentPageUrl].onResume();
    });
})(angular);

