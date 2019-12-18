<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>

<div class="popSection flex2">
    <div class="popTitle" style="margin-top: 0;">关联信息</div>
    <div><button class="btn1" ng-click="showDesc=!showDesc">注意事项，操作前必看(点击查看)</button></div>
    <div style="width:100%;white-space: normal;" class="gray888" ng-show="showDesc">
        <strong class="gray333">1.关联账号下的四个角色的登录名不共享。</strong>
        即使'关联账号'发生变化，也不影响登录名，且关联账号不能用来直接登录；<br/>
        <strong class="gray333">2.关联的商家、服务站、代理商共享同一个密码，会员使用独立密码。</strong>
        这三个角色的密码存在于'关联账号'上，所以'关联账号'改变后的密码将会使用改变后的'关联账号'，但若这个改变后的'关联账号'是新建立的，则密码使用原密码；<br/>
        <strong class="gray333">3.被解绑的角色将会新建一个'关联账号'。</strong>
        密码为原角色账号的密码；<br/>
        <strong class="gray333">4.无任何角色的'关联账号'将会被删除。</strong>
        若A'关联账号'仅关联了一个角色账号，角色为商家，将A'关联账号'的商家角色关联给B'关联账号'之后，A'关联账号'无任何其他角色关联，则A'关联账号'将被删除；<br/>
        <strong class="gray333">5.若准备将商家角色账号S001关联在'A关联账号'，但S001已经被关联在'B关联账号'上。则在此情景下，'B关联账号'的S001商家将被移到'A关联账号'上，S001商家角色的登录密码将使用'A关联账号'的登录密码。</strong>
        若A'关联账号'已经关联了一个商家角色，则这个商家角色会被移到一个新建立的C'关联账号'，密码为原账号密码<br/>
        <strong class="gray333">6.'关联账号'已经绑定了会员或商家时，重新绑定会员或商家，会同步更新会员和商家的归属关系。</strong>
        解除绑定时不会移除会员和商家的归属关系；<br/>
        <strong class="gray333">7.会员申请的三个角色将会自动绑定到'关联账号'。</strong><br/>
        <strong class="gray333">8.注册会员不会生成'关联账号'，关联账号将在申请其他角色时创建。</strong>
    </div>

    <div class="sectionTable" style="width:100%">
        <div>
            <div style="width:10%">角色</div>
            <div style="width:40%">角色账号</div>
            <div style="width:50%">操作</div>
        </div>
        <div ng-repeat="user in userList" class="trBk">
            <div style="width:10%" ng-bind="user.title"></div>
            <div style="width:40%" ng-bind="user.name"></div>
            <div style="width:50%;text-align: left;">
                <button class="btn1" ng-click="goBindUser(user)">绑定其他账号</button>
                <button class="btn1" ng-show="user.hasOwnProperty('_id') && !jQuery.isEmptyObject(user._id)"
                        ng-click="relieveUser(user)" style="background:#FD4231">解绑当前账号</button>
                <button class="btn1" ng-show="user.hasOwnProperty('_id') && !jQuery.isEmptyObject(user._id)"
                        ng-click="resetPwd(user)" style="background:#ffb00c">重置密码</button>
            </div>
        </div>
    </div>
</div>


<div class="popSection flex2" ng-show="isBindUser">
    <div class="popTitle">搜索需要绑定的账号</div>
    <div style="width:100%">
        <span>当前选择角色:</span>
        <span ng-bind="selectUser.title"></span>
    </div>
    <div style="width:80%;margin-left:10%">
        <div style="width:100%">
            <span>检索:</span>
            <span>
            <form ng-submit="reSearchUser()">
                <input type="text" ng-model="selectUser.$$name" placeholder="名称/手机号码"/>
                <button class="btn1" type="submit">搜索</button>
            </form>
        </span>
        </div>
        <div class="searchCon" style="width:100%" ng-show="searchPage.items.length>0">
            <div class="item" ng-repeat="search in searchPage.items">
                <div style="width:30%" ng-bind="isEmpty(search.name)?search.realName:search.name"></div>
                <div style="width:40%" ng-bind="isEmpty(search.mobile)?search.phone:search.mobile"></div>
                <div style="width:30%"><button class="btn1" ng-click="setBindUser(search)">绑定</button></div>
            </div>
        </div>
        <div class="moreBtn" ng-show="searchPage.pageNo<searchPage.totalPage"
             ng-click="nextSearchUser()">
            <div class="gray888" ng-bind="'还有'+(searchPage.totalNum - searchPage.pageNo * searchPage.pageSize)+'条数据'"></div>
            <div class="gray888">点击查看更多</div>
        </div>
    </div>
</div>
