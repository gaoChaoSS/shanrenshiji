(function () {
    $(document).ready(function () {
        $("#getPhoneCheck").click(function () {
            var phone = $("#phone").val();
            var reg = /^1[34578]\d{9}$/;
            var error = true;
            if (window.isEmpty(phone)) {
                $("#phone").next().next('div').show();
                error = false;
            }
            if (!reg.test(phone)) {
                $("#phone").next().next('div').show();
                error = false;
            }
            if (error == false) {
                alert("请输入正确的手机号");
                return false;
            }
            var url = '/s_member/api/crm/Member/getMobileIsReg?mobile=' + phone;
            $.get(url).success(function (re) {
                if (re.content._id == null) {
                    var url1 = '/s_member/api/common/Sms/getCheckCode';
                    var data1 = {
                        loginName: phone,
                        type: 'reg'
                    }
                    $.ajax({
                        url: url1,
                        type: 'PUT',
                        data: JSON.stringify(data1),
                        success: function () {
                            alert('获取验证码成功!');
                        }
                    });
                } else {
                    alert('抱歉!该账号已经注册');
                }
            })

        });

        window.submit = function () {
            var phone = $("#phone").val();
            var password = $("#password").val();
            var repeatPsw = $("#repeatPsw").val();
            var code = $("#code").val();
            var error = true;

            var reg = /^1[34578]\d{9}$/;
            if (!reg.test(phone)) {
                $("#phone").next().next('div').show();
                error = false;
            }
            if (!(/^.{6,16}$/.test(password))) {
                $("#password").next('div').show();
            }
            if (password != repeatPsw) {
                $("#repeatPsw").next('div').show();
                error = false;
            }
            if (error == false) {
                return false;
            }

            var data = {
                "mobile": phone,
                "password": password,
                "deviceId": genUUID(),
                "verification": code,
                "secondPwd":repeatPsw
            };
            $.post("/s_member/api/crm/Member/register", JSON.stringify(data)).success(function () {
                $("#edit").hide();
                $("#success").show();
                //setTimeout(function(){
                //    window.location.href = "index.jsp";
                //},5000);
                //for (var i=0;i<5;i++){
                //    setTimeout(function(){
                //        $("#time").html(i--);
                //    },1000*i);
                //}
            }).error(function (data) {
                $("#edit").hide();
                $("#error").show();
                alert(data.responseJSON.content.errMsg);
            });
        };

        $("input").focus(function () {
            $(this).parent().find('div').hide();
        });

        $("#phone").blur(function () {
            var phone = $("#phone").val();
            var reg = /^1[34578]\d{9}$/;
            if (!reg.test(phone)) {
                $(this).next().next('div').show();
            }
        });

        $("#password").blur(function () {
            var password = $("#password").val();
            var reg = /^.{6,16}$/;
            if (!reg.test(password)) {
                $(this).next('div').show();
            }
        });

        $("#repeatPsw").blur(function () {
            var password = $("#password").val();
            var repeatPsw = $("#repeatPsw").val();
            if (password != repeatPsw) {
                $(this).next('div').show();
            }
        });

        window.reSubmit = function () {
            $("#error").hide();
            $("#edit").show();
        };
    })
})();