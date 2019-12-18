;
(function() {
    var _checkDataIsError = function(checkType, value, fieldName) {
        value = value == null ? '' : value;
        var errMsgs = [];
        $.each(checkType.split(','), function(k, v) {
            // 注意先后顺序
            if (v == 'notNull' && (value === '' || value.trim() === '')) {
                var msgStr = '';
                if (!isEmpty(fieldName)) {
                    msgStr = Locale.getMsg('i18n_field_is_null', fieldName);
                }
                errMsgs.push(msgStr);
                return;
            }
            if (v == 'email') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /^([a-zA-Z0-9]+[_|\_|\.|\-]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.|\-]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_email_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'phone') {
                if (isEmpty(value)) {
                    return;
                }
                // var reg = /[\-|\*|\#\d]+$/;
                var reg = /^0{0,1}(1[3-9][0-9])[0-9]{8}$/;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_phone_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'telphone') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /[\-|\*|\#\d]+$/;
                // var reg = /^0{0,1}(1[0][0-9]|15[0-9]|18[0-9])[0-9]{8}$/;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_telphone_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'double') {
                if (isEmpty(value)) {
                    return;
                }
                if (!$.isNumeric(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_double_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'price') {
                if (isEmpty(value)) {
                    return;
                }
                var index = value.indexOf('.');
                if (index > -1 && value.length > (index + 1)) {
                    var sub = last = value.substring(index + 1);
                    if (sub.length > 2) {
                        var msgStr = '';
                        if (!isEmpty(fieldName)) {
                            msgStr = (Locale.getMsg('i18n_field_price_error', fieldName));
                        }
                        errMsgs.push(msgStr);
                        return;
                    }
                }
            }

            if (v.startsWith('min=')) {
                if (isEmpty(value)) {
                    return;
                }
                var vs = parseFloat(v.split('=')[1]);
                var vf = null;
                try {
                    vf = parseFloat(value);
                } catch (e) {
                }
                if (vf != null && vf < vs) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_doubleMin_error', fieldName, vs));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v.startsWith('max=')) {
                if (isEmpty(value)) {
                    return;
                }
                var vs = parseFloat(v.split('=')[1]);
                var vf = null;
                try {
                    vf = parseFloat(value);
                } catch (e) {
                }
                if (vf != null && vf > vs) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_doubleMax_error', fieldName, vs));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v.startsWith('length=')) {
                if (isEmpty(value)) {
                    return;
                }
                var vs = parseInt(v.split('=')[1]);
                if (value.length != vs) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_length_error', fieldName, vs));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v.startsWith('lengthMin=')) {
                if (isEmpty(value)) {
                    return;
                }
                var vs = parseInt(v.split('=')[1]);
                if (value.length < vs) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_lengthMin_error', fieldName, vs));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v.startsWith('lengthMax=')) {
                if (isEmpty(value)) {
                    return;
                }
                var vs = parseInt(v.split('=')[1]);
                if (value.length > vs) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_lengthMax_error', fieldName, vs));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'int') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /^-?\d+$/;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_int_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'password') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /^[A-Za-z0-9_]{6,20}$/;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_password_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
            if (v == 'idcard') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i;
                if (!reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_idcard_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }

            if (v == 'nots') {
                if (isEmpty(value)) {
                    return;
                }
                var reg = /[`~!@#$%^&*()_+<>?:"{},.\/\\;'[\]]/; // /[^%&',;=?$\x22]+/;
                if (reg.test(value)) {
                    var msgStr = '';
                    if (!isEmpty(fieldName)) {
                        msgStr = (Locale.getMsg('i18n_field_nots_error', fieldName));
                    }
                    errMsgs.push(msgStr);
                    return;
                }
            }
//            if (v == 'time' ) {
//                if (isEmpty(value)) {
//                    return;
//                }
//                var vs = value.split(':');
//                if (vs != null && vs.length != 2) {
//                    isError = true;
//                    errMsgs.push(Locale.getMsg('i18n_field_send_time'));
//                    
//                }
//            }

        });
        return errMsgs;
    }
    // 数据有效性校验
    $.fn.submitCheckFormData = function() {
        var hasError = false;
        var errMsgs = null;
        $.each($(this).find('*[data-check="true"]:visible'), function(k, v) {
            $(v).checkInputItemData();
            if ($(v).data('dataError')) {
                hasError = true;
                $(v).focus();
            }
        });
        $.each($(this).find('*[data-checkAll="true"]'), function(k, v) {
            $(v).checkInputItemData();
            if ($(v).data('dataError')) {
                hasError = true;
                $(v).focus();
            }
        });
        this.data('dataError', hasError);
        return this;
    }
    // 数据有效性校验
    $.fn.resetCheckFormData = function() {
        $.each($(this).find('*[data-check="true"]'), function(k, v) {
            $(v).removeClass('error').attr('data-error', false);
            $(v).next('.checkInputDataMsg').hide();
        });
        this.data('dataError', false);
        return this;
    }
    $.fn.checkInputItemData = function() {
        var _self = $(this);
        if (_self.attr('disabled')) {
            return;
        }
        var checkType = _self.attr('data-checkType');
        var fieldName = _self.attr('data-fieldName');
        var value = _self.val();
        checkType || (checkType = 'notNull');
        errMsgs = _checkDataIsError(checkType, value, fieldName);
        var msgCon = _self.next('.checkInputDataMsg');
        if (msgCon.size() == 0) {
            msgCon = $('<span></span>').addClass('checkInputDataMsg');
            _self.after(msgCon);
        }

        var hasError = false;
        if (errMsgs.length > 0) {
            hasError = true;
            msgCon.html(errMsgs.join('<br/>')).fadeIn();
            // msgCon.html('*').fadeIn();
        } else {
            msgCon.hide();
        }
        _self.attr('data-error', hasError).data('dataError', hasError);
        return _self;
    }

    $.fn.initCheckInputDataBlur = function() {
        $(this).find('input[data-check="true"],textarea[data-check="true"]').blur(function() {
            $(this).checkInputItemData();
        });
        $(this).find('select[data-check="true"]').change(function() {
            $(this).checkInputItemData();
        });
        $(this).find('*[data-check="true"]').each(function(k, v) {
            var type = $(v).attr('data-checkType');
            if (type != null && type.indexOf('notNull') == -1) {
                return;
            }
            $(v).after('<span class="checkInputStar">*</span>')
        });
        return this;
    }
})();