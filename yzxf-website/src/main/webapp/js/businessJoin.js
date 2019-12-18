
(function () {
    $(document).ready(function () {
        var _id = genUUID();
        window.basePath = "/s_member/api";
        var areaList=[];
        var areaSelect=[];
        var areaDefaultList = ["选择省份","选择城市","选择区域"];
        var typeList=[];
        var typeSelect=[];
        var singleImg = ['businessLicense','idCardImgFront','idCardImgBack','idCardImgHand'];
        var imgMore={
            bankImg: [],
            contractImg:[],
            doorImg:[],
        };

        $.getJSON(window.basePath + "/crm/Member/getLocation?pid=-1", function(data){
            var province = data.content.items;
            areaList[0]=province;
            for (var i=0;i<province.length;i++){
                $("#area select").eq(0).append("<option name="+province[i].name+" value="+province[i]._id+">"+province[i].name+"</option>");
            }
        });

        $("#area select").eq(0).change(function(){
            $.getJSON(window.basePath + "/crm/Member/getLocation?pid="+$(this).val(), function(data){
                areaList[1]=data.content.items;
                var selectedIndex=$("#area select").eq(0).prop("selectedIndex")-1;
                if(selectedIndex>=0){
                    areaSelect[0]=areaList[0][selectedIndex];
                }else{
                    areaSelect[1]=[];
                    areaSelect[2]=[];
                    areaList[1]=[];
                    areaList[2]=[];
                }
                setAreaOption(areaList[1],1);
                setAreaOption(areaList[2],2);
            });
        });

        $("#area select").eq(1).change(function(){
            $.getJSON(window.basePath + "/crm/Member/getLocation?pid="+$(this).val(), function(data){
                areaList[2]=data.content.items;
                var selectedIndex=$("#area select").eq(1).prop("selectedIndex")-1;
                if(selectedIndex>=0){
                    areaSelect[1]=areaList[1][selectedIndex];
                }else{
                    areaSelect[2]=[];
                    areaList[2]=[];
                }
                setAreaOption(areaList[2],2);
            });
        });
        $("#area select").eq(2).change(function(){
            var selectedIndex=$("#area select").eq(2).prop("selectedIndex")-1;
            areaSelect[2]=areaList[2][selectedIndex];
            if(selectedIndex===-1){
                $("#area").next().show();
            }else{
                $("#area").next().hide();
            }
        });

        // 设置地区
        var setAreaOption=function(item,eq){
            $("#area select").eq(eq).html("<option name=\"\" value=\"-1\">"+areaDefaultList[eq]+"</option>");
            if(!window.isEmpty(item) && item.length!==0){
                var len = item.length;
                for (var i=0;i<len;i++){
                    $("#area select").eq(eq).append("<option name="+item[i].name+" value="+item[i]._id+">"+item[i].name+"</option>");
                }
            }
            $("#area").next().show();
        }

        $.getJSON(window.basePath + "/account/Seller/getOperate?pid=-1", function(data){
            var one = data.content.items;
            typeList[0]=one;
            for (var i=0;i<one.length;i++){
                $("#operateType select").eq(0).append("<option contactPerson="+one[i].name+" value="+one[i]._id+">"+one[i].name+"</option>");
            }
        });

        $("#operateType select").eq(0).change(function(){
            $.getJSON(window.basePath + "/account/Seller/getOperate?pid="+$(this).val(), function(data){
                typeList[1] = data.content.items;
                var selectedIndex = $("#operateType select").eq(0).prop("selectedIndex")-1;
                if(selectedIndex>=0){
                    typeSelect[0]=typeList[0][selectedIndex];
                }else{
                    typeSelect[0]=[];
                    typeSelect[1]=[];
                    typeList[1]=[];
                    typeList[2]=[];
                }
                setOperateTypeOption(typeList[1],1);
                setOperateTypeOption(typeList[2],2);
                typeSelect[1]=[];
                typeSelect[2]=[];
            });
        });

        $("#operateType select").eq(1).change(function(){
            $.getJSON(window.basePath + "/account/Seller/getOperate?pid="+$(this).val(), function(data){
                typeList[2] = data.content.items;

                var selectedIndex = $("#operateType select").eq(1).prop("selectedIndex")-1;
                if(selectedIndex>=0){
                    typeSelect[1]=typeList[1][selectedIndex];
                }else{
                    typeSelect[1]=[];
                    typeList[2]=[];
                }
                setOperateTypeOption(typeList[2],2);
                typeSelect[2]=[];
            });
        });
        $("#operateType select").eq(2).change(function(){
            typeSelect[2]=typeList[2][$("#area select").eq(2).prop("selectedIndex")-1];
            $("#operateType").next().hide();
        });

        var setOperateTypeOption=function(item,eq){
            if(window.isEmpty(item) || item.length===0){
                $("#operateType select").eq(eq).html("<option name=\"\" value=\"-1\">选择"+(parseInt(eq)+1)+"级分类</option>");
                if(eq===1){
                    $("#operateType").next().show();
                }
            }else{
                var len = item.length;
                for (var i=0;i<len;i++){
                    $("#operateType select").eq(eq).append("<option contactPerson="+item[i].name+" value="+item[i]._id+">"+item[i].name+"</option>");
                }
                $("#operateType").next().hide();
            }
        }

        window.setOpenTime = function(field,num){
            var time = {
                openTime : parseInt($("#openTimeId").text()),
                closeTime : parseInt($("#closeTimeId").text())
            }
            time[field]+=num;
            if (field=="openTimeId") {
                if (time.openTime < 0 || time.closeTime <= time.openTime) {
                    time.openTime -= num;
                }
            } else {
                if (time.closeTime <= time.openTime || time.closeTime > 24) {
                    time.closeTime -= num;
                }
            }
            $("#"+field).text(time[field]  +":00");
        };

        var userInfo = {
            pendingId: "",//待审表ID
            _id: "",
            address: "",
            area: "",
            areaValue: "",
            bankId: "",
            bankName: "",
            bankImg:"",
            bankUser: "",
            //bankUserCardId: "",
            //bankUserPhone: "",
            businessLicense: "",
            closeTime: 21,
            contactPerson: "",
            idCardImgBack: "",
            idCardImgFront: "",
            idCardImgHand: "",
            contractImg:"",
            integralRate: "",
            intro: "",
            isOnlinePay:false,
            legalPerson: "",
            name: "",
            openTime: 9,
            openWeek: "一,二,三,四,五,六,日",
            operateType: "",
            operateValue: "",
            phone: "",
            realCard: "",
            serverPhone: "",
            doorImg:"",
            latitude:"",
            longitude:"",
            email:"",
            bankAddress:""
        };

        window.submit = function (){
            $("#submitForm").attr("disabled","disabled");
            if(checkForm()){
                $("#submitForm").attr("disabled",false);
                return;
            }
            $.get(window.basePath+"/account/UserPending/getOtherIsApply?ownerType=Seller&createId=_"+genUUID()).success(function(re){
                userInfo.pendingId = re.content._id;
                userInfo._id=re.content.ownerId;
                userInfo["creatorType"] = "other";
                $.post(window.basePath + "/account/UserPending/submitSeller", JSON.stringify(userInfo)).success(function (){
                    $("#edit").hide();
                    $("#success").show();

                }).error(function(data){
                    $("#edit").hide();
                    $("#error").show();
                    alert(data.responseJSON.content.errMsg);
                    $("#submitForm").attr("disabled",false);
                });
            }).error(function(){
                $("#submitForm").attr("disabled",false);
            });
        };

        $(".joinCon input,.joinCon textarea").focus(function (){
            $(this).next(".prompt").hide();
        });

        $(".joinCon input,.joinCon textarea").blur(function (){
            var idName = $(this).attr("id");
            checkAll(idName);
        });

        // 公共方法：判断空字段
        var checkEmpty=function(field){
            if (window.isEmpty($("#"+field).val())){
                $("#"+field).next().show();
            }else{
                userInfo[field] = $("#"+field).val();
            }
        }
        var checkName=function(entityName){
            checkField(entityName,!(/[\u4e00-\u9fa5·]{2,64}/g.test($("#"+entityName).val())));
        }

        // 循环检查表单所有数据
        var checkForm = function(){
            var flag = false;
            $.each(userInfo,function(k,v){
                checkAll(k);
                if($("#"+k).next().css("display")=="none"){
                    // userInfo[k]=$("#"+k).val();
                }else if(($("#"+k).next().css("display")=="block" || $("."+k).next().css("display")=="block") && !flag){
                    flag = true;
                }
            });
            return flag;
        };

        var checkField = function(field,flag,val){
            if(flag){
                $("#"+field).next().show();
            }else{
                $("#"+field).next().hide();
            }
            if(window.isEmpty(val)){
                userInfo[field]=$("#"+field).val();
            }else{
                userInfo[field]=val;
            }
        }

        // 检查单个字段数据
        var checkAll = function(entityName){
            if(entityName == "name"){
                var name = $("#name").val();
                checkField(entityName,window.isEmpty(name) || name.length>100);
            } else if(entityName == "integralRate"){
                checkField(entityName,!/^(([123456789]|[123456789]\d)(\.\d+)?)$/.test($("#integralRate").val()));
            } else if(entityName == "phone"){
                checkField(entityName,!(/^1[34578]\d{9}$/.test($("#phone").val())));
            } else if(entityName == "email"){
                checkField(entityName,!/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/.test($("#email").val()));
            } else if(/^(contactPerson)|(bankUser)|(legalPerson)$/.test(entityName)){
                checkName(entityName);
            } else if(entityName == "serverPhone"){
                checkField(entityName,window.isEmpty($("#serverPhone").val()));
            } else if(entityName == "openWeek"){
                getOpenWeek();
            }else if(entityName == "operateType"){
                getOperate();
            } else if(entityName == "area"){
                getArea();
            } else if(entityName == "bankId"){
                checkField(entityName,!/^[0-9]{16,19}$/.test($("#bankId").val()));
            } else if(entityName == "realCard"){
                checkField(entityName,!/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test($("#realCard").val()));
            } else if(/^(businessLicense)|(idCardImgFront)|(idCardImgBack)|(idCardImgHand)$/.test(entityName)){
                if(window.isEmpty(userInfo[entityName])){
                    $("#"+entityName+"Item").next().show();
                }else{
                    $("#"+entityName+"Item").next().hide();
                }
            } else if(/^(bankImg)|(doorImg)$/.test(entityName)){
                if(window.isEmpty(imgMore[entityName])){
                    $("#"+entityName+"More").next().show();
                }else{
                    $("#"+entityName+"More").next().hide();
                    formatImgMore(entityName);
                }
            } else if(/^(latitude)|(longitude)$/.test(entityName)){
                if(window.isEmpty(userInfo.latitude) || window.isEmpty(userInfo.longitude)){
                    $("#containerBtn").next().show();
                }
            } else if(/^(openTime)|(closeTime)$/.test(entityName)){
                userInfo.openTime = parseInt($("#openTimeId").text());
                userInfo.closeTime = parseInt($("#closeTimeId").text());
                if(window.isEmpty(userInfo.openTime) && window.isEmpty(userInfo.closeTime)){
                    $("#openTime").next().show();
                }else{
                    $("#openTime").next().hide();
                }
            } else {
                checkEmpty(entityName);
            }
        };

        $("#openWeek input,#openWeek label").click(function(){
            $("#openWeek").next().hide();
        });

        var getOpenWeek=function(){
            var weekStr='';
            var weekList =document.getElementsByName("week");
            for(var x=0; x<weekList.length; x++){
                if(weekList[x].checked){
                    weekStr+=weekList[x].value+','; //如果选中，将value添加到变量s中
                }
            }
            checkField("openWeek",window.isEmpty(weekStr),weekStr.substring(0,weekStr.length-1));
        };

        var getOperate=function(){
            var typeSelectStr="";
            var typeSelectName="";
            if(!window.isEmpty(typeSelect)){
                for(var i= 0,len=typeSelect.length;i<len;i++){
                    if(!window.isEmpty(typeSelect[i].value)){
                        typeSelectStr+=typeSelect[i].value+"_";
                        typeSelectName=typeSelect[i].name;
                    }
                }
            }
            if(!window.isEmpty(typeSelectName) && !window.isEmpty(typeSelectStr)){
                typeSelectStr="_"+typeSelectStr;
                userInfo.operateValue= typeSelectStr;
                userInfo.operateType= typeSelectName;
            }else{
                $("#operateType").next().show();
            }
        };

        var getArea=function(){
            var valueStr="";
            var areaStr="";
            if(!window.isEmpty(areaSelect)){
                for(var i= 0,len=areaSelect.length;i<len;i++){
                    if(!window.isEmpty(areaSelect[i].value)){
                        valueStr+=areaSelect[i].value+"_";
                        areaStr+=areaSelect[i].name;
                    }else{
                        $("#area").next().show();
                    }
                }
            }
            if(!window.isEmpty(areaStr) && !window.isEmpty(valueStr)){
                valueStr="_"+valueStr;
                userInfo.area= areaStr;
                userInfo.areaValue= valueStr;
            }else{
                $("#area").next().show();
            }
        }

        $(".upLoadInput").change(function (){
            var inputObj = $(this);
            var type = $(this).attr('data-type');
            if (type == 'bankImg' || type == 'doorImg') {
                if(imgMore[type].length>=10){
                    $("#"+type+"More").next().show();
                    return;
                }else{
                    $("#"+type+"More").next().hide();
                }
            }
            window.uploadWinObj = {
                one: true,
                entityName: 'Seller',
                entityField: type,
                entityId: _id,
                callSuccess: function (options) {
                    if (type == 'bankImg' || type == 'doorImg') {
                        uploadShowMore(type,options.fileId);
                    }else{
                        uploadShowSingle(type,options.fileId);
                    }
                }
            };

            window.uploadWinObj.files = inputObj[0].files;
            window.uploadFile();
        });

        //显示上传的单张图片
        var uploadShowSingle=function(imgName,fileId){
            $("#"+imgName+'Item').show();
            $("#"+imgName+'Item').next().hide();
            var src = "/s_img/icon.jpg?_id="+fileId+"&wh=300_300";
            $("#"+imgName).attr("src",src);
            $("#"+imgName).show();
            userInfo[imgName] = fileId;
        };

        //显示上传的多张图片
        var uploadShowMore=function(imgName,fileId){
            imgMore[imgName].push(fileId);

            $("#"+imgName+"More").append("<div class=\"imgItem "+imgName+"Item\" id=\""+fileId+"\">" +
                "<div class=\"delImg\">×</div>" +
                "<img class=\"showImg "+imgName+"\" src=\"/s_img/icon.jpg?_id="+fileId+"&wh=300_300\"/>" +
                "</div>"
            );
            $("."+imgName+"Item").show();
            //添加删除方法
            $("#"+fileId).click(function(){
                var delIndex = $.inArray(fileId,imgMore[imgName]);
                imgMore[imgName].splice(delIndex,1);
                $("#"+fileId).remove();
            });
        };

        // 初始化删除图片方法
        var initDelImg=function(){
            //删除'单张图片上传'的图片
            $.each(singleImg,function(k,v){
                $("#"+v+"Item").click(function(){
                    userInfo[v] = "";
                    $("#"+v).attr("src","");
                    $("#"+v+'Item').hide();
                });
            });
        }

        //格式化图片编码
        var formatImgMore=function(fileName){
            if (imgMore[fileName]!=null){
                userInfo[fileName]=imgMore[fileName].join("_");
                if(userInfo[fileName].substring(0,1)=="_"){
                    userInfo[fileName]=userInfo[fileName].substring(1,userInfo[fileName].length);
                }
            }
        }

        $("#containerBtn").click(function(){
            getLatAndLong();
        });

        //根据地址获取店铺经纬度
        var getLatAndLong = function(){
            $("#container").hide();
            getArea();
            userInfo.address = $("#address").val();
            if(window.isEmpty(userInfo.area) || window.isEmpty(userInfo.address)){
                $("#containerBtn").next().show();
                $("#address").next().show();
                return;
            }
            var url = 'https://apis.map.qq.com/ws/geocoder/v1/?address='+userInfo.area+userInfo.address+'&key=ZGWBZ-7CWW4-OG2UV-DA5LD-CAJTV-RKBXI&output=jsonp';
            $.ajax({
                async:false,
                url: url,
                type: "GET",
                dataType: 'jsonp',
                jsonp: 'callback',
                jsonpCallback: "QQmap",
                beforeSend: function(){
                },success: function (json) {//客户端jquery预先定义好的callback函数,成功获取跨域服务器上的json数据后,会动态执行这个callback函数
                    if(json.status==347){
                        userInfo.longitude='';
                        userInfo.latitude='';
                        $("#containerBtn").next().show();
                    }else{
                        userInfo.longitude = json.result.location.lng;
                        userInfo.latitude = json.result.location.lat;
                        $("#container").show();
                        $("#containerBtn").next().hide();
                    }
                    initMap();
                }
            });
        };

        //初始化地图
        var initMap = function (check) {
            $("#container").show();
            if(window.isEmpty(userInfo.latitude) || window.isEmpty(userInfo.longitude)){
                return;
            }
            var center = new qq.maps.LatLng(userInfo.latitude, userInfo.longitude);
            var map = new qq.maps.Map(document.getElementById("container"), {
                center: center,
                zoom: 18,
            });
            var marker = new qq.maps.Marker({
                position: center,
                map: map
            });
            var markerCluster = new qq.maps.MarkerCluster({
                map: map,
                minimumClusterSize: 2, //默认2
                markers: [],
                zoomOnClick: true, //默认为true
                gridSize: 60, //默认60
                averageCenter: true, //默认false
                maxZoom: 16 //默认18
            });
            if(check!='noClick'){
                //点击地图,更新坐标位置
                qq.maps.event.addListener(map, 'click', function(event) {
                    userInfo.latitude=event.latLng.getLat();
                    userInfo.longitude=event.latLng.getLng();
                    initMap();
                    //getAreaAddress();
                });
            }
        };

        //window.save = function (type, fileId){
        //
        //    var data = {
        //        "_id": _id,
        //    };
        //    data[type] = fileId;
        //    var url = window.basePath + "/account/Seller/saveApplySeller?_id="+_id;
        //    $.post(url, JSON.stringify(data)).success(function () {
        //
        //    });
        //};

        window.reSubmit = function (){
            $("#error").hide();
            $("#edit").show();
        };

        initDelImg();
    })
})();