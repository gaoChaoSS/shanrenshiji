package com.zq.kyb.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UrlUtils {

    public static boolean filterEnd(String lowerCase, String fileEnds) {
        int endIndex = lowerCase.lastIndexOf(".");
        if (endIndex != -1)
            lowerCase = lowerCase.substring(endIndex, lowerCase.length());
        if (StringUtils.isEmpty(lowerCase) || fileEnds.indexOf(lowerCase + ".") == -1) {
            // Logger.getLogger(UrlUtils.class.getName()).info("ok:" + lowerCase);
            return true;
        }
        return false;
    }

    /**
     * 取得同站的完全路径
     *
     * @param aurl
     * @param string
     * @param encoder
     * @return
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static URL getRelativeURL(URL aurl, String string, String encoder) throws UnsupportedEncodingException, MalformedURLException {
        string = string.replaceAll("\\\\", "/");
        URL url2 = null;
        try {
            url2 = new URL(string);
        } catch (MalformedURLException e) {
            try {
                String url = aurl.toString();
                if (string.startsWith("/")) {
                    url = aurl.getProtocol() + "://" + aurl.getAuthority() + string;
                } else {
                    String path = aurl.getPath();
                    int lastIndexOf = aurl.getPath().lastIndexOf("/");
                    if (lastIndexOf != -1)
                        path = path.substring(0, lastIndexOf);
                    url = aurl.getProtocol() + "://" + aurl.getAuthority() + path + "/" + string;
                }
                url2 = new URL(url);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        // 处理../
        String p = url2.getPath();
        p = p.replaceAll("/./", "/");
        if (p.indexOf("/../") != -1) {
            int i = 0;
            while (p.indexOf("/../") != -1 && ++i < 30) {
                p = p.replaceAll("/([^/\\.]+)/\\.\\./", "/");
            }
            // url2 = new URL(aurl.getProtocol() + "://" + aurl.getAuthority() +
            // p);
        }
        if (p.indexOf("..") != -1) {
            Logger.getLogger(UrlUtils.class.getName()).info("==========================================");
            Logger.getLogger(UrlUtils.class.getName()).info(aurl.toString() + ":" + string);
            int i = 0;
            while (p.indexOf("/../") != -1 && ++i < 30) {
                p = p.replaceAll("/../", "/");
            }
        }
        // 处理参数编码
        StringBuffer queryStr = new StringBuffer();
        String query = url2.getQuery();
        if (StringUtils.isNotEmpty(query)) {
            String[] split = query.split("&");
            int i = 0;
            for (String sp : split) {
                if (StringUtils.isNotEmpty(sp)) {
                    String[] split2 = sp.split("=");
                    if (split2 != null && split2.length == 2) {
                        queryStr.append(i++ == 0 ? "?" : "&");
                        String value = split2[1];
                        queryStr.append(split2[0]);
                        queryStr.append("=");
                        queryStr.append(value.indexOf("%") == -1 ? URLEncoder.encode(value, encoder) : value);
                    }
                }
            }
        }
        url2 = new URL(url2.getProtocol() + "://" + url2.getAuthority() + p + queryStr.toString());
        return url2;
    }

    public static String getUrlParams(String url, String params) {
        String paramStr = "";

        String[] ps = url.split("\\?");
        url = ps[0];
        Map<String, String> m = new HashMap<String, String>();

        if (ps.length == 2) {
            paramStr += ps[1];
        }
        if (params != null) {
            paramStr += "&" + params;
        }
        String[] oldParams = paramStr.split("&");
        for (String string : oldParams) {
            String[] param = string.split("=");
            if (string.endsWith("=")) {
                param = new String[]{string.substring(0, string.length() - 1), ""};
            }
            if (param.length == 2)
                m.put(param[0], param[1]);
        }
        String newParamStr = "";
        try {
            for (String key : m.keySet()) {
                newParamStr += ("".equals(newParamStr) ? "" : "&") + key + "=" + URLEncoder.encode(m.get(key), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String string = url + (StringUtils.isEmpty(newParamStr) ? "" : "?") + newParamStr;
        return string;
    }

    /**
     * 对url反向操作
     *
     * @param string
     */
    private String urlRe(String string) {
        // RewriteRule /city/([0-9]+)_p([0-9]+)$
        // /a/f_index_infoList.html?place=$1&pageNo=$2 [L,PT]
        // RewriteRule /type/([0-9]+)_p([0-9]+)$
        // /a/f_index_infoList.html?id=$1&pageNo=$2 [L,PT]
        // RewriteRule /info/([0-9]+).html$ /a/f_index_infoShow.html?id=$1
        // [L,PT]
        String str = "/a/f_index_infoShow.html?id=";
        int indexOf = string.indexOf(str);
        if (indexOf == 0) {
            return "/info/" + string.substring(str.length()) + ".html";
        }

        String[] paths = string.split("\\?");
        if (paths.length == 2) {
            if (paths[0].equals("/a/f_index_infoList.html")) {
                String[] params = paths[1].split("&");
                if (params.length == 1) {
                    if (paths[1].indexOf("place=") != -1) {
                        return "/city/" + params[0].split("=")[1] + "_p1";
                    } else if (paths[1].indexOf("id=") != -1) {
                        return "/type/" + params[0].split("=")[1] + "_p1";
                    }
                }
                if (params.length == 2) {
                    String placeId = null, type = null, pageNo = null;
                    if ("place".equals(params[0].split("=")[0])) {
                        placeId = params[0].split("=")[1];
                    } else if ("pageNo".equals(params[0].split("=")[0])) {
                        pageNo = params[0].split("=")[1];
                    } else if ("id".equals(params[0].split("=")[0])) {
                        type = params[0].split("=")[1];
                    }
                    if ("place".equals(params[1].split("=")[0])) {
                        placeId = params[1].split("=")[1];
                    } else if ("pageNo".equals(params[1].split("=")[0])) {
                        pageNo = params[1].split("=")[1];
                    } else if ("id".equals(params[1].split("=")[0])) {
                        type = params[1].split("=")[1];
                    }
                    return "/" + (placeId == null ? "type" : "city") + "/" + (placeId == null ? type : placeId) + "_p" + (pageNo == null ? 1 : pageNo);

                    // if (("&" + paths[1]).indexOf("&place=") != -1 && ("&" +
                    // paths[1]).indexOf("&pageNo=") != -1)
                    // {
                    //
                    //
                    // return "/city/" + paths[1].split("=")[1] + "_p";
                    // } else if (("&" + paths[1]).indexOf("&place=") != -1 &&
                    // ("&" + paths[1]).indexOf("&pageNo=")
                    // !=
                    // -1) {
                    // return "/type/" + paths[1].split("=")[1] + "_p";
                    // }
                }

            }
        }

        return string;
    }

    public static void isParam(String pstr, String placeId, String type, String pageNo) {

        if ("place=".equals(pstr.split("=")[0])) {
            placeId = pstr.split("=")[1];
        } else if ("pageNo=".equals(pstr.split("=")[0])) {
            pageNo = pstr.split("=")[1];
        } else if ("id=".equals(pstr.split("=")[0])) {
            type = pstr.split("=")[1];
        }
    }

    public static String toQueryStr(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (String key : params.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf-8"));
        }
        return sb.toString();
    }
}
