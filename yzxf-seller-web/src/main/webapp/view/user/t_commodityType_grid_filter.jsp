<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<div class="width100B" ng-include="'/temp_new/agent_select.jsp'"></div>
<table style="background-color: #E2EBEF;">
    <%--<tr>--%>
        <%--<td class="headerTd">创建日期:</td>--%>
        <%--<td colspan="3">--%>
            <%--<div ng-include="timeFilter"></div>--%>
        <%--</td>--%>
    <%--</tr>--%>
    <tr>
        <td class="headerTd">商品名:</td>
        <td>
            <input type="text" ng-model="filter._commodityName"/>
        </td>
        <td class="headerTd">所属商户:</td>
        <td>
            <input type="text" ng-model="filter._sellerName"/>
        </td>
    </tr>
    <tr>
        <td class="headerTd">商品类别:</td>
        <td>
            <input type="text" ng-model="filter._operateType"/>
        </td>
        <td class="headerTd">商品展示类型:</td>
        <td>
            <select ng-model="filter._commodityType" style="border: none;">
                <option value="">------请选择------</option>
                <option value="gongyi">商城:公益商品</option>
                <option value="tejia">商城:特价商品</option>
                <option value="remen">热门商品</option>
                <option value="isIndexCommodity">会员:置顶热门商品</option>
            </select>
        </td>
    </tr>
</table>

