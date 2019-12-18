(function() {
    // alert('getBrowserInfo.js');
    try {
        function getBrowserInfo() {
            var agent = navigator.userAgent.toLowerCase();
            var regStr_ie = /msie [\d.]+;/gi;
            var regStr_ff = /firefox\/[\d.]+/gi
            var regStr_chrome = /chrome\/[\d.]+/gi;
            var regStr_saf = /safari\/[\d.]+/gi;
            // IE
            if (agent.indexOf("msie") > 0) {
                return agent.match(regStr_ie);
            }
            // firefox
            if (agent.indexOf("firefox") > 0) {
                return agent.match(regStr_ff);
            }
            // Chrome
            if (agent.indexOf("chrome") > 0) {
                return agent.match(regStr_chrome);
            }
            // Safari
            if (agent.indexOf("safari") > 0 && agent.indexOf("chrome") < 0) {
                return agent.match(regStr_saf);
            }
        }

        // 然后获取版本号
        function getBrowserVersion() {
            var browser = getBrowserInfo();
            // alert(browser);
            return (browser + "").replace(/[^0-9.]/ig, "");
        }

        var _BrowserDetect = {
            init : function() {
                this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
                this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "an unknown version";
                this.OS = this.searchString(this.dataOS) || "an unknown OS";
            },
            searchString : function(data) {
                for (var i = 0; i < data.length; i++) {
                    var dataString = data[i].string;
                    var dataProp = data[i].prop;
                    this.versionSearchString = data[i].versionSearch || data[i].identity;
                    if (dataString) {
                        if (dataString.indexOf(data[i].subString) != -1)
                            return data[i].identity;
                    } else if (dataProp)
                        return data[i].identity;
                }
            },
            searchVersion : function(dataString) {
                var index = dataString.indexOf(this.versionSearchString);
                if (index == -1)
                    return;
                return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
            },
            dataBrowser : [ {
                string : navigator.userAgent,
                subString : "Chrome",
                identity : "Chrome"
            }, {
                string : navigator.userAgent,
                subString : "OmniWeb",
                versionSearch : "OmniWeb/",
                identity : "OmniWeb"
            }, {
                string : navigator.vendor,
                subString : "Apple",
                identity : "Safari",
                versionSearch : "Version"
            }, {
                prop : window.opera,
                identity : "Opera",
                versionSearch : "Version"
            }, {
                string : navigator.vendor,
                subString : "iCab",
                identity : "iCab"
            }, {
                string : navigator.vendor,
                subString : "KDE",
                identity : "Konqueror"
            }, {
                string : navigator.userAgent,
                subString : "Firefox",
                identity : "Firefox"
            }, {
                string : navigator.vendor,
                subString : "Camino",
                identity : "Camino"
            }, { // for newer Netscapes (6+)
                string : navigator.userAgent,
                subString : "Netscape",
                identity : "Netscape"
            }, {
                string : navigator.userAgent,
                subString : "MSIE",
                identity : "Explorer",
                versionSearch : "MSIE"
            }, {
                string : navigator.userAgent,
                subString : "Gecko",
                identity : "Mozilla",
                versionSearch : "rv"
            }, { // for older Netscapes (4-)
                string : navigator.userAgent,
                subString : "Mozilla",
                identity : "Netscape",
                versionSearch : "Mozilla"
            } ],
            dataOS : [ {
                string : navigator.platform,
                subString : "Win",
                identity : "Windows"
            }, {
                string : navigator.platform,
                subString : "Mac",
                identity : "Mac"
            }, {
                string : navigator.userAgent,
                subString : "iPhone",
                identity : "iPhone/iPod"
            }, {
                string : navigator.platform,
                subString : "Linux",
                identity : "Linux"
            } ]

        };
        var _genDeviceData = function() {
            var deviceInfo = {
                browser : _BrowserDetect.browser,
                version : _BrowserDetect.version,//
                userAgent : window.navigator.userAgent,
                language : window.navigator.language || navigator.userLanguage,
                colorDepth : window.screen.colorDepth,
                deviceXDPI : window.screen.deviceXDPI
            }
            var data = {
                type : 'WEB',
                os : window.browserDetect.OS,
                osVersion : '',
                deviceInfo : JSON.stringify(deviceInfo)
            };
            data.width = window.screen.width;
            data.height = window.screen.height;
            data.brand = _BrowserDetect.browser;
            data.model = _BrowserDetect.version + '';
            if (window._config) {
                data.clientVersion = window._config.client_version;
            }
            return data;
        }
        _BrowserDetect.init();
        window.getBrowserInfo = getBrowserInfo;
        window.getBrowserVersion = getBrowserVersion;
        window.browserDetect = _BrowserDetect;
        window.genDeviceData = _genDeviceData;
    } catch (e) {
        console.log('error:' + e);
    }
})();