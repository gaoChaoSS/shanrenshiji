(function () {
    try {
        window.showAlert = function (msg) {
            console.log(msg);
        }
        // Set URL of your WebSocketMain.swf here:
        WEB_SOCKET_SWF_LOCATION = "/js/websocket/WebSocketMain.swf";
        // Set this to dump debug message from Flash to console.log:
        WEB_SOCKET_DEBUG = true;
        if (WebSocket != null && WebSocket.loadFlashPolicyFile != null) {
            WebSocket.loadFlashPolicyFile("xmlsocket://" + window.top.location.host + ":10843");
        }

        window.reConnTimes = 0;
        window.initWebsocket = function () {
            var isConn = false;
            if (window.location.href.startsWith("http://")) {
                window.socket1 = new WebSocket("ws://" + window.top.location.host + "/events");
            } else {
                window.socket1 = new WebSocket("wss://" + window.top.location.host + "/events");
            }

            // 心跳包
            setInterval(function () {
                if (isConn) {
                    window.socket1.send('');
                }
            }, 5 * 1000);
            // Set event handlers.
            window.socket1.onopen = function () {
                // alert('open');
                isConn = true;
                window.reConnTimes = 0;
                showAlert("与服务器连接成功!");
                window.websocketConnTimes = 0;
                //发送auth
                var msg = {
                    _id: genUUID(),
                    actionPath: "1:PUT@/account/AdminUser/auth",
                    content: {token: getCookie("___ADMIN_TOKEN")}
                }
                window.socket1.send(JSON.stringify(msg));

                if (window.webSocket_open) {
                    window.webSocket_open();
                }
            };
            window.socket1.onmessage = function (e) {
                // e.data contains received string.
                var msg = e.data;
                // alert(msg);
                // var obj = JSON.parse(msg);
                // showMessage(msg);
                if (window.webSocket_receive) {
                    window.webSocket_receive(msg);
                }
            };
            window.socket1.isConn = function () {
                return isConn;
            }
            window.socket1.onclose = function () {
                isConn = false;
                if (window.webSocket_close) {
                    window.webSocket_close();
                }
                showAlert("连接已经关闭!,立刻自动重连");
                window.reConnTimes++;
                window.reConnTimes = window.reConnTimes > 1024 ? 1024 : window.reConnTimes;
                var round = Math.pow(2, window.reConnTimes);
                // alert(round);
                // 重新连接设置
                // window.websocketConnTimes || (window.websocketConnTimes = 0);
                // var timeOut = (++window.websocketConnTimes) * 10;// TODO 改为线性增长函数
                showAlert(round + "秒后自动重连!");
                initWebsocket.delay(round);
            };
            window.socket1.onerror = function () {
                showAlert("服务器通信已中断！");
                console.log("websocket 发生了错误！");
            };

            if (window.socket1.connType == 'comet') {
                var cometConnId = getCookie('cometConnId');
                if (cometConnId != null) {
                    window.socket1.onmessage({
                        data: JSON.stringify({
                            path: 'authMember',
                            status: 200
                        })
                    });
                }
            }
        }
    } catch (e) {
        // alert('init_webscoket.js:' + e);
    }
})();
