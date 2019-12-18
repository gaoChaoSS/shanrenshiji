<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java"%>





<div ng-controller="${model}_${entity}_Ctrl" style="width: 100%; height: 100%; position: relative; padding-right: 80px;">
    <div class="fl" style="width: 50%; padding: 10px; background-color: #eee; border: 4px solid #fff; border-radius: 4px; min-height: 300px;">
        <div style="text-align: center;">
            于 <span class="notHigh">{{backTime}}</span> 在 <span class="high">{{backUrl}}</span> 备份
        </div>
        <div></div>
    </div>
    <div class="fl" style="width: 50%; padding: 10px; background-color: #eee; border: 4px solid #fff; border-radius: 4px; min-height: 300px;">
        <div style="text-align: center;">当前数据库数据</div>
        <div></div>
    </div>
    <div class="clearDiv"></div>
    <div style="width: 80px; position: absolute; top: 0; right: 0;">
        <div></div>
    </div>
</div>