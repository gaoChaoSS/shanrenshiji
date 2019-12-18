<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="home_index_Ctrl" class="d_content title_section form_section index_page_globalDiv store_panel">
    <%--顶部标题--%>
    <div class="title indexTitle" style="border:0;position:absolute;left:0;z-index:1;background:rgba(0, 0, 0, 0.2);">
        <span class="titleAddress titleLeft widthPercent25 textEllipsis"
              ng-click="goPage('/home/location/locationQuery/1/selectedArea/storeArea')">
            <span class="icon-dizhi iconfont"></span>
            <span ng-bind="locationName==null || locationName==''?'地址':locationName"></span>
        </span>
        <form id="searchForm" ng-submit="search()">
            <div id="indexRow">
                <span class="icon-fangdajing iconfont iconImg6" ng-click="search()"></span>
                <input type="text" placeholder="请输入要搜索的关键词" name="keyWord" ng-model="keywords"/>
                <button type="submit" style="display: none;">搜索</button>
            </div>
        </form>
        <div class="icon-plus iconfont titlePlus" ng-click="indexBtnDiv=!indexBtnDiv"></div>
        <div class="indexBtnDiv" ng-show="indexBtnDiv" ng-click="indexBtnDiv=false">
            <div class="triangle"></div>
            <div class="icon-icon1 iconfont indexBtnRow" ng-click="goPage('/my/my2DBarcode')"><span>个人信息</span></div>
            <%--<div class="icon-qianbao iconfont indexBtnRow" ng-click="goPage('/my/wallet')"><span>我的钱包</span></div>--%>
            <div class="icon-qiaquan1 iconfont indexBtnRow" ng-click="goPage('/my/coupon')"><span>我的卡券</span></div>
            <div class="icon-wodewodeshoucang iconfont indexBtnRow" ng-click="goPage('/my/collection')">
                <span style="margin-left:0">我的收藏</span>
            </div>
        </div>
    </div>
    <div class="overflowPC" id="indexScroll">
        <%--首页快捷按钮--%>

        <%--头部图片--%>
        <div class="index_page_headImgDiv" style="position: relative">
            <img class="imgCarousel" ng-repeat="img in imgList" ng-src="{{iconImg(img.icon)}}" ng-show="imgCur==$index" ng-click="advertisingGoPage(img)">
            <div style="display: flex;align-items: center;justify-content: center;position: absolute;bottom: 0;width: 100%;height: 30px;">
                <div class="imgSelectBtn" ng-repeat="img in imgList" ng-click="selectImg($index)" ng-class="imgCur==$index?'imgSelected':''"></div>
            </div>
        </div>

        <%--中部按钮--%>
        <%--<div class="index_page_middle_btn">--%>
            <%--<div class="index_page_btn_left" ng-click="pensionCanUse()" style="position: relative">--%>
                <%--<img src="/yzxfMember_page/img/yanglaojin.png" style="height: 31px;margin-top: 7px;"/>--%>
                <%--<p style="margin-top: -2px;color:#fff">我的养老金</p>--%>
                <%--<span style="color: #ffbf59" class="isCertification iconfont icon-version"--%>
                      <%--ng-show="certification==null||!certification"></span>--%>

            <%--</div>--%>
            <%--<div class="index_page_btn_middle" ng-click="goPage('/my/coupon')">--%>
                <%--<img src="/yzxfMember_page/img/kaquan.png" style="height: 31px;margin-top: 7px;"/>--%>
                <%--<p style="margin-top: -2px;color: #fff;">我的卡券</p>--%>
            <%--</div>--%>
            <%--&lt;%&ndash;<div class="index_page_btn_right">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<img src="/yzxfMember_page/img/saoyisao.png" style="height: 31px;margin-top: 7px;"/>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<p style="margin-top: -2px;color: #fff;">扫一扫</p>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--</div>--%>
        <%--商家分类--%>
        <div class="index_page_middleTwo_btn">
            <div class="index_page_middleTwo_btn_function" ng-repeat="itemType in operate"
                 ng-click="goPageType(itemType.name,itemType.value,itemType.pvalue)">
                <div><img ng-src="{{iconImgUrl(itemType.img)}}" class="imgStyle"></div>
                <div class="index_page_middleTwo_btnText" ng-bind="itemType.name"></div>
            </div>
        </div>
        <%--公益专区 和天天特价使用的同一个CSS--%>
        <div class="index_page_dayDay">
            <%--标题--%>
            <div class="index_page_dayDay_title">
                <div style="position:relative;">
                    <img src="/yzxfMember_page/img/zhuanqu.png" style="width: 100%;height: 38px;"/>
                    <div class="tejiaStyle">公益专区</div>
                </div>
                <div class="index_page_dayDay_title_divTwo">
                    <div class="index_page_dayDay_titleGray" ng-click="goPage('/other/commonweal')">
                        更多<span class="iconfont icon-right-1-copy"
                                style="color: #fff;font-size: 14px;"></span></div>
                </div>
            </div>
            <%--商品--%>
            <div class="index_page_dayDayGoods">
                <%--图片1--%>
                <div class="index_page_dayDayGoodsInfo" ng-repeat="g in productGongyi"
                     ng-click="goPage('/store/commodity/goodsId/'+g._id+'/_id/'+g.sellerId)">
                    <p class="teJiaP">{{g.tag}}</p>
                    <img ng-src="{{iconImgUrl(g.icon)}}" err-src="/yzxfMember_page/img/notImg02.jpg"
                         class="index_page_dayDayGoodsInfoImg">
                    <%--<div class="redYun">--%>
                    <%--<span>天天搞机</span>--%>
                    <%--</div>--%>
                </div>
            </div>
        </div>
        <%--天天特价    与公益专区使用的同一CSS样式--%>
        <div class="index_page_dayDay">
            <%--标题--%>
            <div class="index_page_dayDay_title">
                <div style="position:relative;">
                    <img src="/yzxfMember_page/img/titleBK1.png" style="width: 100%;height: 38px;"/>
                    <div class="tejiaStyle">天天特价</div>
                </div>
                <div class="index_page_dayDay_title_divTwo">
                    <div class="index_page_dayDay_titleGray" ng-click="goPage('/other/special')">
                        更多<span class="iconfont icon-right-1-copy"
                                style="color: #fff;font-size: 14px;"></span>
                    </div>
                </div>
            </div>
            <%--商品--%>
            <div class="index_page_dayDayGoods">
                <%--图片1--%>
                <div class="index_page_dayDayGoodsInfo" ng-repeat="p in productInfo"
                     ng-click="goPage('/store/commodity/goodsId/'+p._id+'/_id/'+p.sellerId)">
                    <p class="teJiaP">{{p.tag}}</p>
                    <img ng-src="{{iconImgUrl(p.icon)}}" err-src="/yzxfMember_page/img/notImg02.jpg"
                         class="index_page_dayDayGoodsInfoImg">
                    <div>
                        <div style="color:#999;text-decoration: line-through;margin-left: 10px;">&yen;{{p.oldPrice}}</div>
                        <div style="color:red;">&yen;{{p.salePrice}}</div>
                    </div>
                </div>
            </div>

        </div>
        <%--附近的商家--%>
        <div class="index_page_hotTao" ng-show="storeInfo!=null && storeInfo.length>0">
            <div class="index_page_dayDay_title">
                <%--<div class="index_page_hotTaoText black"><img src="/yzxfMember_page/img/fujin.png">--%>
                    <%--<span class="mainRowLeft5">热门商品</span>--%>
                <%--</div>--%>
                <%--<div class="index_page_hotTaoBtnTwo" ng-click="goPage('/other/special/productType/hot')">更多--%>
                    <%--<span class="iconfont icon-right-1-copy" style="color:#888;font-size: 14px;"></span>--%>
                <%--</div>--%>
                <div style="position:relative;">
                    <img src="/yzxfMember_page/img/titleBK2.png" style="width: 100%;height: 38px;"/>
                    <div class="tejiaStyle">热门商品</div>
                </div>
                <div class="index_page_dayDay_title_divTwo">
                    <div class="index_page_dayDay_titleGray" ng-click="goPage('/other/special/productType/hot')">
                        更多<span class="iconfont icon-right-1-copy"
                                style="color: #fff;font-size: 14px;"></span>
                    </div>
                </div>
            </div>
            <div class="index_page_hotTaoInfo">
                <div style="width: 40%;border-right: 1px solid #F2F2F2;"
                     ng-click="goPage('/store/commodity/goodsId/'+storePic._id+'/_id/'+storePic.sellerId)">
                    <div class="indexProduct">
                        <span class="icon-shijian iconfont"> 限时特价</span>
                        </br>限时限量特价好货
                    </div>
                    <div class="firstBigPhoto"
                         style="background-image:url('{{iconImgUrl(storePic.icon)}}');background-size: 100% auto;"></div>
                </div>
                <div style="width: 60%;height: 95px;border-bottom: 1px solid #F2F2F2;" class="indexBox2"
                     ng-click="goPage('/store/commodity/goodsId/'+storeFPic._id+'/_id/'+storePic.sellerId)">
                    <div class="indexProduct"
                         style="width: 55%;float: left; ">
                        <span class="icon-like iconfont" style="color: #EF7100 ;"> 限时特价</span>
                        </br>5星好评大众推荐
                    </div>
                    <div style="width: 45%;height: 100px;
                    background-image:url('{{iconImgUrl(storeFPic.icon)}}');background-repeat: no-repeat;
                    background-size: auto 100%;float: left;"></div>
                    <!--<img ng-src="{{iconImgUrl(storeFPic.icon)}}" style="height: 115px;">-->
                </div>
                <div style="width: 60%;border-right: 1px solid #F2F2F2;">
                    <div class="firstBigPhoto" ng-click="goPage('/store/commodity/goodsId/'+storeInfo[0]._id+'/_id/'+storeInfo[0].sellerId)"
                         style="background-image:url('{{iconImgUrl(storeInfo[0].icon)}}');background-size: 100% auto;width:50%;border-right: 1px solid #f2f2f2;"></div>
                    <div class="firstBigPhoto" ng-click="goPage('/store/commodity/goodsId/'+storeInfo[1]._id+'/_id/'+storeInfo[1].sellerId)"
                         style="background-image:url('{{iconImgUrl(storeInfo[1].icon)}}');background-size: 100% auto;width:50%"></div>
                </div>
                <%--<div ng-repeat="s in storeInfo" ng-show="storeInfo!=null" style="border-right: 1px solid #F2F2F2;border-top:1px solid #F2F2F2">--%>
                    <%--<div class="textCenter">--%>
                        <%--<span class="textSize18" ng-bind="s.tag"></span></br>--%>
                        <%--&lt;%&ndash;<span class="textSize15">xx</span>&ndash;%&gt;--%>
                    <%--</div>--%>
                    <%--<div ng-click="goPage('/store/commodity/goodsId/'+s._id+'/_id/'+s.sellerId)"--%>
                         <%--style="background-image:url('{{iconImgUrl(s.icon)}}');background-repeat: no-repeat;width: 100%;height: 160px;"--%>
                         <%--class="indexBox3">--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>
        </div>
        <%--推荐商家--%>
        <div class="index_page_hotTao">
            <div class="index_page_hotTaoTitle">
                <div class="index_page_hotTaoText black"><img src="/yzxfMember_page/img/tuijian.png"><span
                        class="mainRowLeft5">推荐商家</span></div>
                <div class="index_page_hotTaoBtnTwo" ng-click="goPage('/store/store/selectedIndex/2')">
                    更多<span class="iconfont icon-right-1-copy black" style="color:#888;font-size: 14px;"></span>
                </div>
            </div>
            <div class="index_page_hotTaoInfo">
                <div ng-repeat="t in storeTInfo" ng-show="storeTInfo!=null"
                     ng-click="goPage('/store/storeInfo/sellerId/'+t._id)"
                     style="border-right: 1px solid #f2f2f2;height: 211px;width: 25%;position: relative">
                    <p class="storeTitle textEllipsis" ng-bind="t.name"></p>
                    <div class="indexBox1" style="background-image:url('{{iconImg(getSellerIcon(t.icon,t.doorImg))}}');background-size: 100%;background-size: auto 70%;"></div>
                    <%--<img ng-src="{{iconImg(t.icon)}}" class="tuijianIngCon" err-src="/yzxfMember_page/img/notImg02.jpg">--%>
                </div>
            </div>
        </div>

        <%--商家展示--%>
        <%--<div class="index_page_hotTao" style="padding-left:10px;line-height:35px;margin:0;border-bottom:1px solid #eee">--%>
            <%--<span class="iconfont icon-shangjia2 textSize20" style="color: #138bbe"></span>--%>
            <%--<span class="mainRowLeft5">商家</span>--%>
        <%--</div>--%>
        <%--<div ng-include="storeInclude"></div>--%>
    </div>

    <div class="hideMenu" ng-show="errorPension">
        <div class="errorMain">
            <div class="errorMainRow">您没有绑定会员卡,请绑定</div>
            <div class="errorMainRow">
                <div class="errorMainBtn black" ng-click="errorPension=false">取消</div>
                <div class="errorMainBtn black" ng-click="memberProtocol=true">前往绑定</div>
            </div>
        </div>
    </div>
    <div class="hideMenu" ng-show="memberProtocol">
        <div class="errorMain memberMain">
            <div class="errorMainRow">会员协议</div>

            <div class="errorMainRow content" style="height: 299px;
    overflow: auto;
    text-align: left;
    padding: 6px 10px;">
                <p>1. 会员有权根据本协议的约定，以及普惠生活养老平台网站上发布的相关规则在普惠生活养老平台上询商品信息、
                    订购具体商品、查询物流信息、提供或接受物流服务、发表使用体验、参与品讨论、物流体验、
                    参加普惠生活养老平台网站的有关活动，以及使用普惠生活养老平台提供的其他服务。
                </p>
                <p> 2. 会员应当保证在出售/购买商品、
                    提供/接受物流运输服务过程中遵守诚实信用原则。
                    不扰乱网上交易的正常秩序。 </p>
                <p> 3. 会员同意严格遵守以下义务：</br>
                    ①不得传输或发表：煽动抗拒、破坏宪法和法律、行政法规实施的言论，煽动颠覆国家政权。</br>
                    推翻社会主义制度的言论，煽动分裂国家、破坏国家统一的言论，煽动民族仇恨、民族歧视、破坏民族团结的言论。</br>
                    ②不得利用普惠生活养老平台从事洗钱、窃取商业秘密、窃取个人信息等违法犯罪活动.</br>
                    ③不得捏造或者歪曲事实，散步谣言，扰乱社会秩序。</br>
                    ④不得传输或发表任何封建迷信、邪教、淫秽、色情、赌博、暴力、恐怖、教唆犯罪等不文明的信息资料。</br>
                    ⑤不得公然侮辱他人或者捏造事实诽谤他人或者进行其他恶意共计。</br>
                    ⑥其他违反宪法和法律、行政法规规定的。</br>
                </p>
                <p>
                    4. 未经普惠生活养老平台书面同意，会员不得在普惠生活养老平台网站上发布任何形式的广告。</p>
                <p>
                    5. 遵守普惠生活养老平台制定的规则。</br>
                </p>
            </div>
            <div class="errorMainRow rowBorderTop">
                <div class="errorMainBtn black" ng-click="memberProtocol=false;errorPension=false">返回</div>
                <div class="errorMainBtn black" ng-click="errorPension=false;isRealName()">同意</div>
            </div>
        </div>
    </div>
</div>