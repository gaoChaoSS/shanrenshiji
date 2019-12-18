(function() {
    window.jsVersion = 0.003;
    //window.basePath = '/services/api';
    (window.queryTimes != null) || (window.queryTimes = 0);// 用于实现http查询队列
    window.zIndex = 10;
    var apiVersion = '1';
    setCookie('apiVersion', apiVersion);
    window.goPage = function(url) {
        window.location.href = url;
    }

    window.actionPathMap = {
        // 基本用户操作
        'auth' : apiVersion + ':put@/account/User/auth',
        'login' : apiVersion + ':post@/account/User/login',
        'reg' : apiVersion + ':post@/account/User/reg',
        // 直接操作设备的命令，

        // 好友
        'myContacts' : apiVersion + ':get@/im/Contact/myContacts',
        'addContact' : apiVersion + ':put@/im/Contact/addContact',
        'respAddContact' : apiVersion + ':put@/im/Contact/respAddContact',// 回应添加好友请求
        'notifi_reqStatus' : apiVersion + ':put@/im/Contact/notifi_reqStatus',// 回应添加好友请求

        // 群组
        'myGroups' : apiVersion + ':get@/im/ChatGroup/myGroups',
        'getGroupMembers' : apiVersion + ':get@/im/ChatGroup/getGroupMembers',

        // 消息
        'sendMsg' : apiVersion + ':post@/im/ImMsg/sendMsg',
        'sendMsgToDevice' : apiVersion + ':post@/im/ImMsg/sendMsgToDevice',// 发送到设备的消息
        'sendMsgToGroup' : apiVersion + ':post@/im/ImMsg/sendMsgToGroup',// 发送到群组的消息
        'notifi_msg' : apiVersion + ':post@/im/ImMsg/notifi_msg',
        'notifi_whoInput' : apiVersion + ':put@/im/ImMsg/notifi_whoInput',// 收到对方正在输入的讯息
        'sendMyInput' : apiVersion + ':put@/im/ImMsg/sendMyInput',// 发送我正在输入的讯息
        'setMsgRead' : apiVersion + ':put@/im/ImMsg/setMsgRead',
        'getChatLastMsg' : apiVersion + ':get@/im/ImMsg/getChatLastMsg',
        'getMyNewMsgCount' : apiVersion + ':get@/im/ImMsg/getMyNewMsgCount',
        'onlineStatusNotifi' : apiVersion + ':get@/im/ImMsg/onlineStatusNotifi',// 收到通知状态改变的通知
        'changeOnlineStatus' : apiVersion + ':put@/im/ImUserConf/changeOnlineStatus',// 请求改变自己的状态
        // 用户定义
        'saveMyImConf' : apiVersion + ':post@/im/ImUserConf/saveMyImConf',
        'getMyImConf' : apiVersion + ':get@/im/ImUserConf/getMyImConf',
        'updateChat' : apiVersion + ':post@/im/ChatRecord/updateChat',// 更新聊天顺序
        'getMyChats' : apiVersion + ':get@/im/ChatRecord/getMyChats'
    };

    // 保存到数据库时删除多余的显示字段
    window._deleteField = function(value) {
        if (value == null) {
            return value;
        }
        for ( var k in value) {
            if (k.startsWith && k.startsWith("__")) {
                delete value[k];
            } else {
                var v = value[k]
                if ($.isArray(v) || $.isPlainObject(v)) {
                    value[k] = _deleteField(v);
                }
            }
        }
        return value;
    }
    window._fixSubmitData = function(data) {
        var value = {};
        $.extend(true, value, data);
        value = _deleteField(value);
        // alert(JSON.stringify(value));
        return value;
    }

})();