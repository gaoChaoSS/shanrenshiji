
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

        var userInfo = {
            pendingId: "",//待审表ID
            _id: "",
            address: "",
            area: "",
            areaValue: "",
            bankId: "",
            bankName: "",
            bankType:"",
            bankTypeValue:"",
            bankProvince: "",
            bankProvinceValue: "",
            bankCity: "",
            bankCityValue: "",
            bankUser: "",
            bankImg: "",
            businessLicense: "",
            contactPerson: "",
            idCardImgBack: "",
            idCardImgFront: "",
            idCardImgHand: "",
            contractImg:"",
            name: "",
            phone: "",
            legalPerson:"",
            realCard:"",
            realAreaValue:""
        };

        window.submit = function (){
            $("#submitForm").attr("disabled","disabled");
            if(checkForm()){
                $("#submitForm").attr("disabled",false);
                return;
            }
            $.get(window.basePath+"/account/UserPending/getOtherIsApply?ownerType=Agent&createId=_"+genUUID()).success(function(re){
                userInfo.pendingId = re.content._id;
                userInfo._id=re.content.ownerId;
                userInfo["creatorType"] = "other";
                $.post(window.basePath + "/account/UserPending/submitAgent", JSON.stringify(userInfo)).success(function (){
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
            } else if(entityName == "phone"){
                checkField(entityName,!(/^1[34578]\d{9}$/.test($("#phone").val())));
            } else if(/^(contactPerson)|(bankUser)|(legalPerson)$/.test(entityName)){
                checkName(entityName);
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
            } else if(/^(bankImg)$/.test(entityName)){
                if(window.isEmpty(imgMore[entityName])){
                    $("#"+entityName+"More").next().show();
                }else{
                    $("#"+entityName+"More").next().hide();
                    formatImgMore(entityName);
                }
            } else {
                checkEmpty(entityName);
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
                userInfo.realAreaValue= valueStr;
            }else{
                $("#area").next().show();
            }
        }

        $(".upLoadInput").change(function (){
            var inputObj = $(this);
            var type = $(this).attr('data-type');
            if (type == 'bankImg') {
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
                    if (type == 'bankImg') {
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

        window.reSubmit = function (){
            $("#error").hide();
            $("#edit").show();
        };

        initDelImg();
    })
})();