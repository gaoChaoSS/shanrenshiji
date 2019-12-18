<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<table style="background-color: #E2EBEF;">
    <tr>
        <td class="headerTd">关键字:</td>
        <td>
            <input type="text" placeholder="名称,地址,电话..." ng-model="filter._search"/>
        </td>
        <td class="headerTd">类型:</td>
        <td>
            <select ng-model="filter._pid">
                <option value="">------请选择------</option>
                <option value="-1">城市</option>
                <option value="notCity">分支机构</option>
            </select>
        </td>
    </tr>
</table>