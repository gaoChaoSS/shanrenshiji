<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/join&register.css?_v=${_v}" rel="stylesheet">
    <link href="css/index.css?_v=${_v}" rel="stylesheet">
    <script charset="utf-8" src="https://map.qq.com/api/js?v=2.exp"></script>
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>

<div class="conDiv">
    <div class="con where">
        <a class="colorRed1" href="index.jsp">首页</a> &gt; <span>商家加盟</span>
        <div style="text-align: center;padding:55px 0;">
            <img style="" src="img/seller.png">
        </div>
    </div>
</div>

<div id="edit" class="conDiv" style="min-height: 450px">
    <div class="con joinCon" style="border-radius: 8px;margin-bottom: 30px;padding:25px 0">
        <%--<div class="title">商家加盟</div>--%>
        <div>
            <span>代理商名称</span>
            <div>
                <input id="name" type="text">
                <div class="prompt"><img src="img/error.png">商家名称在100字以内</div>
            </div>
        </div>
        <div>
            <span>联系人</span>
            <div>
                <input id="contactPerson" type="text">
                <div class="prompt"><img src="img/error.png">请填写2~64位中文汉字之间的联系人名字!</div>
            </div>
        </div>
        <div>
            <span>联系手机</span>
            <div>
                <input id="phone" type="text">
                <div class="prompt"><img src="img/error.png">手机号格式错误</div>
            </div>
        </div>

        <div>
            <span>所在地区</span>
            <div>
                <div id="area">
                    <select>
                        <option name="" value="-1">选择省份</option>
                    </select>
                    <select>
                        <option name="" value="-1">选择城市</option>
                    </select>
                    <select>
                        <option name="" value="-1">选择区域</option>
                    </select>
                </div>
                <div class="prompt" ><img src="img/error.png"><span>请选择完整的区域</span></div>
            </div>
            <div>
                <input id="address" placeholder="填写详细街道地址" style="margin-top: 10px;width:460px;" type="text" maxlength="64">
                <div class="prompt" style="margin-top: 10px"><img src="img/error.png"><span>请填写完整的街道地址</span></div>
            </div>
        </div>

        <div>
            <span>银行账号</span>
            <div>
                <input id="bankId" type="text" maxlength="64">
                <div class="prompt"><img src="img/error.png">请输入正确的银行账号</div>
            </div>
        </div>
        <div>
            <span>开户行</span>
            <div>
                <input id="bankName" type="text" maxlength="64">
                <div class="prompt"><img src="img/error.png">请填写开户行</div>
            </div>
        </div>
        <div>
            <span>户名</span>
            <div>
                <input id="bankUser" type="text" maxlength="64">
                <div class="prompt"><img src="img/error.png">请填写2-64位户名</div>
            </div>
        </div>
        <div>
            <span>银行卡正反面/开户许可证(多张)</span>
            <div>
                <div class="upLoad">
                    上传
                    <input class="upLoadInput" style="opacity: 0" data-type="bankImg" type="file">
                </div>
                <div id="bankImgMore"></div>
                <div class="prompt" style="margin-left: 100px;"><img src="img/error.png">请上传银行卡正反面/开户许可证（1~10张以内）</div>
            </div>
        </div>

        <div>
            <span>法人名称</span>
            <div>
                <input id="legalPerson" type="text">
                <div class="prompt"><img src="img/error.png">请填写法人名称</div>
            </div>
        </div>
        <div>
            <span>法人身份证</span>
            <div>
                <input id="realCard" type="text">
                <div class="prompt"><img src="img/error.png">请输入正确的法人身份证号码</div>
            </div>
        </div>
        <div>
            <span>营业执照(单张)</span>
            <div>
                <div class="upLoad">
                    上传
                    <input class="upLoadInput" style="opacity: 0" data-type="businessLicense" type="file">
                </div>
                <div class="imgItem" id="businessLicenseItem">
                    <div class="delImg">×</div>
                    <img id="businessLicense" class="showImg"/>
                </div>

                <div class="prompt" style="margin-left: 100px;"><img src="img/error.png">请上传营业执照(单张)</div>
            </div>
        </div>

        <div>
            <span>法人身份证正面照(单张)</span>
            <div>
                <div class="upLoad">
                    上传
                    <input class="upLoadInput" style="opacity: 0" data-type="idCardImgFront" type="file">
                </div>
                <div class="imgItem" id="idCardImgFrontItem">
                    <div class="delImg">×</div>
                    <img id="idCardImgFront" class="showImg"/>
                </div>

                <div class="prompt" style="margin-left: 100px;"><img src="img/error.png">请上传法人身份证正面照(单张)</div>
            </div>
        </div>
        <div>
            <span>法人身份证背面照(单张)</span>
            <div>
                <div class="upLoad">
                    上传
                    <input class="upLoadInput" style="opacity: 0" data-type="idCardImgBack" type="file">
                </div>
                <div class="imgItem" id="idCardImgBackItem">
                    <div class="delImg">×</div>
                    <img id="idCardImgBack" class="showImg"/>
                </div>

                <div class="prompt" style="margin-left: 100px;"><img src="img/error.png">请上传法人身份证背面照(单张)</div>
            </div>
        </div>
        <div>
            <span>法人身份证手持照(单张)</span>
            <div>
                <div class="upLoad">
                    上传
                    <input class="upLoadInput" style="opacity: 0" data-type="idCardImgHand" type="file">
                </div>
                <div class="imgItem" id="idCardImgHandItem">
                    <div class="delImg">×</div>
                    <img id="idCardImgHand" class="showImg"/>
                </div>

                <div class="prompt" style="margin-left: 100px;"><img src="img/error.png">请上传法人身份证手持照(单张)</div>
            </div>
        </div>

        <button onclick="submit()" id="submitForm" style="margin-top: 35px;margin-bottom: 100px;">提交</button>
    </div>
</div>

<div id="success" class="conDiv" style="min-height: 410px;display: none">
    <div class="con joinCon">
        <img src="img/success.png">
        <p class="title" style="margin-top: 40px;">提交成功!<br/>审核通过后会以短信的形式通知您</p>
    </div>
</div>

<div id="error" class="conDiv" style="min-height: 410px;display: none">
    <div class="con joinCon">
        <img src="img/error.png">
        <p class="title" style="margin-top: 40px">提交失败!</p>
        <button class="errorButton" onclick="reSubmit()">重新填写</button>
    </div>
</div>

<div style="background:#F2F2F2;overflow:hidden;margin-top:100px;">
    <div class="conTitle pagesDiv" style="margin-top:50px;">合作机构</div>
    <div class="conTitle2 pagesDiv">INTRODUCE</div>
    <div class="mod-guild5 flex1">
        <img src="./img/together_1.png" >
        <img src="./img/together_2.png" >
    </div>
</div>

<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/agentJoin.js?_v=${_v}" type="text/javascript"></script>
<script src="./js/lib/imageUpload.js?_v=${_v}" type="text/javascript"></script>
<script src="./js/lib/canvasResize.js?_v=${_v}" type="text/javascript"></script>
<script src="./js/lib/binaryajax.js?_v=${_v}" type="text/javascript"></script>
<script src="./js/lib/exif.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>