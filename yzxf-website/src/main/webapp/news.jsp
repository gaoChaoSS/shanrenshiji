<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <link href="css/news.css?_v=${_v}" rel="stylesheet">
    <%@include file="./inc/top.jsp" %>
</head>
<%@include file="./inc/header.jsp" %>
<body>
<div class="banner">
    <div class="bannerImg" style="background: url('./img/newsBanner.png') 70% 50% no-repeat;height: 475px;"></div>
</div>

<div class="conDiv">
    <div class="con" style="min-height: 700px;text-align: left;">
        <ul class="newsNav">

        </ul>
        <div class="newsCon">
            <div class="rollPicture">
                <img src="img/rollPictrue1.png">
                <img src="img/rollPicture1.jpg">
                <img src="img/rollPicture2.jpg">
                <img src="img/rollPicture3.jpg">
            </div>
            <div class="rollNumber">
                <span class="currentRollNum">1</span>
                <span>2</span>
                <span>3</span>
                <span>4</span>
            </div>
            <div class="line"></div>
            <div id="articles">
                <div class="newsArticle">
                    <div class="articlePicture" style="background: url('img/rollPictrue1.png') 50% 50% no-repeat"></div>
                    <div class="articleTitle">
                        热烈祝贺2016年普惠生活总公司年会隆重召开
                    </div><br>
                    <div class="articleSource">
                        <span style="margin-right: 10px;color: #BDBDBD;cursor: auto">来源:普惠生活</span>发布于:2016-11-12
                    </div><br>
                    <p>2016年1月23日，积分宝总公司“金猴献祥瑞，福满积分宝” 年会在金迈纳大酒店隆重举行。
                        出席本次年会的有积分宝总公司董事长王晓升先生，副总裁高亭达先生以及积分宝总司全体家人。
                        尽管寒潮来袭，年会现场却是热情洋溢，每个人都被温暖包围。
                        年会伊始，凌总上台发表热情洋溢的讲话，让各位家人明确了积分宝的定位和方向，
                        回首2015年，积分宝发展迅猛，意义重大；展望2016希望家人们秉承“大爱天下，强国富民”的企业宗旨，
                        打好系统与App的升级，企业整体包装与营销，团队全方位强化培训的三大战役，为进入资本市场打下扎实的基础，开创全新的篇章。</p>
                </div>

                <div class="newsArticle">
                    <img src="img/newsPictrue2.png">
                    <div class="articleTitle">
                        热烈祝贺2016年普惠生活总公司年会隆重召开
                    </div><br>
                    <div class="articleSource">
                        <span style="margin-right: 10px;color: #BDBDBD;cursor: auto">来源:普惠生活</span>发布于:2016-11-12
                    </div><br>
                    <p>2016年1月23日，积分宝总公司“金猴献祥瑞，福满积分宝” 年会在金迈纳大酒店隆重举行。
                        出席本次年会的有积分宝总公司董事长王晓升先生，副总裁高亭达先生以及积分宝总司全体家人。
                        尽管寒潮来袭，年会现场却是热情洋溢，每个人都被温暖包围。
                        年会伊始，凌总上台发表热情洋溢的讲话，让各位家人明确了积分宝的定位和方向，
                        回首2015年，积分宝发展迅猛，意义重大；展望2016希望家人们秉承“大爱天下，强国富民”的企业宗旨，
                        打好系统与App的升级，企业整体包装与营销，团队全方位强化培训的三大战役，为进入资本市场打下扎实的基础，开创全新的篇章。</p>
                </div>
            </div>


            <div class="pagesDiv">
                <div class="pages" style="margin-bottom: 90px;margin-top: 40px;">
                    <%--<a>1</a>--%>
                    <%--<a>2</a>--%>
                    <%--<a>3</a>--%>
                    <%--<div>下一页</div>--%>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="index footerCon">
    <%@include file="./inc/footer.jsp" %>
</div>
<script src="./js/news.js?_v=${_v}" type="text/javascript"></script>
</body>
</html>