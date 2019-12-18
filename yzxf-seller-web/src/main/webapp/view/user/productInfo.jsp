<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%;">
    <div class="section1">
        <div class="sectionTitle">
            <div class="fl rowTitle1">商品管理
                <span class="colorGray888" ng-bind=" '(共'+totalNumber+'条记录)'"></span>
            </div>
        </div>
        <div class="sectionQuery flex2">
            <div>
                <span>商户名称:</span>
                <input type="text" class="inputCon" ng-model="sellerName"></div>
            <div>
                <span>商品名称:</span>
                <input type="text" class="inputCon" ng-model="productName"></div>
            <div>
                <span>商品状态:</span>
                <input type="text" class="inputCon" ng-model="productStatus"></div>
            <div>
                <span>库存:</span>
                <input type="text" class="inputCon" ng-model="productStock">
            </div>
            <div>
                <span>规格:</span>
                <select class="inputCon" ng-model="productAudit">
                    <option>---请选择---</option>
                    <option value="0">待审核</option>
                    <option value="1">审核不通过</option>
                    <option value="2">待上架</option>
                    <option value="3">已上架</option>
                    <option value="4">已下架</option>
                </select>
            </div>
            <div>
                <span>是否参与活动:</span>
                <select class="inputCon" ng-model="eventProduct">
                    <option>---请选择---</option>
                    <option value="0">是</option>
                    <option value="1">否</option>
                </select>
            </div>
            <div>
                <div class="btn1" ng-click="clearSellectData()">清除全部</div>
                <div class="btn1" ng-click="queryProduct()">查询</div>
            </div>
        </div>

        <table class="sectionTable">
            <tr>
                <td style="width:5% "></td>
                <td style="width:10%">商户名称</td>
                <td style="width:15%">商户ID</td>
                <td style="width:20%">商品名称</td>
                <td style="width:7% ">状态</td>
                <td style="width:10%">规格</td>
                <td style="width:10%">是否参与活动</td>
                <td style="width:8% ">库存</td>
                <td style="width:15%">创建日期</td>
            </tr>
            <tr ng-repeat="product in productList" class="trBk">
                <td style="width:5% "><input type="checkbox" class="font25px" ng-checked="selectAll"
                                             data-id="{{product._id}}"/></td>
                <td style="width:10%" ng-bind="product.sellerName"></td>
                <td style="width:15%" ng-bind="product.sellerId"></td>
                <td style="width:20%" ng-bind="product.productName"></td>
                <td style="width:7% ">有效</td>
                <td style="width:10%">{{product.isDeploy==true?'已上架':'未上架'}}</td>
                <td style="width:10%">{{product.isActivity==true?'是':'否'}}</td>
                <td style="width:8% " ng-bind="product.stockCount"></td>
                <td style="width:15%" ng-bind="showYFullTime(product.createTime)"></td>
            </tr>
        </table>
        <div class="selectTableCon">
            <div class="fl lineH50px marginLeft25">
                <input type="checkbox" class="font25px" ng-model="selectAll"> 全选
            </div>
            <div class="fr lineH50px">
                <button class="queryBtn1">修改</button>
            </div>
        </div>
        <div class="clearDiv"></div>
        <div class="sectionPage" ng-hide="totalPage<1 || totalPage==null">
            <%--<div class="lineH30px" ng-click="pageNumber(1)" ng-show="pageIndex!=1">首页</div>--%>
            <div class="btn2" ng-click="pageNext(-1)" ng-show="pageIndex!=1">上一页</div>
            <div ng-show="isFirstPage">
                <div class="btn3 fl marginLR5" ng-bind="1" ng-click="pageNumber(1)"></div>
                <div class="fl lineH30px">......</div>
            </div>
            <div class="btn3" ng-repeat="page in pageList" ng-bind="page.num"
                 ng-click="pageNumber(page.num);pageCur(page.num)"
                 ng-class="pageIndex==page.num?'bgBlue tWhite':''"></div>
            <div ng-show="isLastPage">
                <div class="fl lineH30px">......</div>
                <%--<div class="btn3 fl marginLR5" ng-bind="totalPage-1" ng-click="pageNumber(totalPage-1)"></div>--%>
                <div class="btn3 fl marginLR5" ng-bind="totalPage" ng-click="pageNumber(totalPage)"></div>
            </div>
            <div class="btn2" ng-click="pageNext(1)" ng-show="pageIndex!=totalPage">下一页</div>
            <%--<div class="lineH30px" ng-click="pageNumber(totalPage)" ng-show="pageIndex!=totalPage">尾页</div>--%>
        </div>
    </div>
</div>
</div>