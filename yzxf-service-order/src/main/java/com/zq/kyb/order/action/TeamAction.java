package com.zq.kyb.order.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.Page;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.*;

/**
 * 团队层级
 */
public class TeamAction extends BaseActionImpl{
    /***
     * @description: 检查是否是管理员，如无代理商ID，则无权限
     * @author: Ali.Cao
     * @param
     * @return void
     **/
    public static void checkAdmin() throws Exception{
        String otherDataJson = ControllerContext.getContext().getOtherDataJson();
        JSONObject other = JSONObject.fromObject(otherDataJson);
        if (StringUtils.mapValueIsEmpty(other, "agentId")) {
            throw new UserOperateException(400, "无查看权限");
        }
    }

    /***
     * @description: 根据会员ID查询会员团队归属关系
     * @author: Ali.Cao
     * @param memberId
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String,Object> checkTeam(String memberId) throws Exception{
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("memberId");
        returnFields.add("isBindCard");
        returnFields.add("canUse");
        returnFields.add("teamId");
        returnFields.add("teamFactor");
        returnFields.add("sellerId");
        returnFields.add("path");
        returnFields.add("belongFactor");
        String sql = "select" +
                " t1._id as memberId" +
                ",t1.isBindCard" +
                ",t1.canUse" +
                ",t2._id as teamId" +
                ",t2.factorId as teamFactor" +
                ",t2.sellerId" +
                ",t2.path" +
                ",t3._id as belongFactor" +
                " from Member t1" +
                " left join Team t2 on t1._id = t2.memberId" +
                " left join Factor t3 on t1.belongAreaValue = t3.areaValue" +
                " where t1._id = ?";
        List<Map<String,Object>> list = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(list==null || list.size()==0 || list.get(0)==null || list.get(0).size()==0){
            return null;
        }
        return list.get(0);
    }

    @POST
    @Path("/checkActive")
    public void checkActive() throws Exception{
        Map<String,Object> req = ControllerContext.getContext().getReq().getContent();
        checkActive(req);
    }

    /**
     * 注册、激活会员，生成团队层级关系
     *   只要注册了，就会生成团队层级关系path,sellerId；若激活了，则必定会有factorId，没有激活必定没有；
     *   商家没有分享功能，只有会员有分享功能。
     *   所有会员必归属于服务站，会员若归属于商家，则分享的会员也会归属于商家。
     *   若会员与商家账号相关联，则为商家会员,商家会员必定没有path，商家会员相当于一级会员
     *   若会员上级没有商家，则也同时没有商家会员。
     *   _id,memberId,factorId,sellerId,path
     * @throws Exception
     */
    public void checkActive(Map<String,Object> filter) throws Exception{
        if(StringUtils.mapValueIsEmpty(filter,"regMember")){
            throw new UserOperateException(500,"获取用户失败");
        }
        String regMember = filter.get("regMember").toString();
        // 商家分享，shareSeller;会员分享，shareMember
        String shareSeller = "";
        String shareMember = "";
        String path = "";
        String factorId = "";
        String teamId = "";

        Map<String,Object> member = checkTeam(regMember);
        if(!StringUtils.mapValueIsEmpty(member,"teamId")){
            teamId = member.get("teamId").toString();
        }else{
            teamId = ZQUidUtils.genUUID();
        }
        Map<String,Object> team = new HashMap<>();
        // 商家分享 ,则无path
        if(!StringUtils.mapValueIsEmpty(filter,"shareSeller")){
            shareSeller = filter.get("shareSeller").toString();
            team.put("sellerId", shareSeller);
        }else if(!StringUtils.mapValueIsEmpty(filter,"shareMember")){
            shareMember = filter.get("shareMember").toString();
            //查询分享会员的path
            Map<String,Object> shareMemberItem = checkTeam(shareMember);

            if(shareMemberItem==null || shareMemberItem.size()==0 || StringUtils.mapValueIsEmpty(shareMemberItem,"memberId")){
                throw new UserOperateException(500,"未找到邀请人");
            }
            if(StringUtils.mapValueIsEmpty(shareMemberItem,"canUse")
                    || !Boolean.parseBoolean(shareMemberItem.get("canUse").toString())){
                throw new UserOperateException(500,"邀请人已被禁用");
            }
            if(StringUtils.mapValueIsEmpty(shareMemberItem,"isBindCard")
                    || !Boolean.parseBoolean(shareMemberItem.get("isBindCard").toString())){
                throw new UserOperateException(500,"邀请人未激活");
            }
            if(!StringUtils.mapValueIsEmpty(shareMemberItem,"sellerId")){
                shareSeller = shareMemberItem.get("sellerId").toString();
                team.put("sellerId", shareSeller);
            }

            if(StringUtils.mapValueIsEmpty(shareMemberItem,"teamId")){
                team.put("factorId",shareMemberItem.get("belongFactor"));
            }
            // 如果是分享会员有创建服务站，则注册会员归属到创建服务站，没有则为分享会员的团队服务站
            List<String> r = new ArrayList<>();
            r.add("factorId");
            List<Object> p = new ArrayList<>();
            p.add(shareMember);
            String sql = "select factorId from User where memberId = ?";
            List<Map<String,Object>> userList = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
            if(userList!=null && userList.size()!=0 && userList.get(0)!=null){
                team.put("factorId", userList.get(0).get("factorId"));
            } else if(!StringUtils.mapValueIsEmpty(shareMemberItem,"teamFactor")){
                team.put("factorId", shareMemberItem.get("teamFactor"));
            }
            if(StringUtils.mapValueIsEmpty(shareMemberItem,"path")){
                path = "_"+shareMember+"_";
            }else{
                path = "_" + shareMember+splitPath(shareMemberItem.get("path").toString());
            }
            team.put("path", path);
        }else if(!StringUtils.mapValueIsEmpty(filter,"factorId")){
            factorId = filter.get("factorId").toString();// 已经激活，但未分配团队
            team.put("factorId", factorId);
        }else{
//            throw new UserOperateException(500,"获取邀请人失败");
        }

        team.put("_id", teamId);
        team.put("memberId", regMember);
        team.put("createTime",System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("Team",team);
    }

    /***
     * @description: 修改下属（推荐会员）的团队关系，如果自己申请商家或发卡点后，
     *                  自己推荐的会员的推荐人path路径用自己的推荐人替换自己
     * @author: Ali.Cao
     * @param
     * @return void
     **/
    @POST
    @Path("/changeUnderPath")
    public void changeUnderPath() throws Exception{
        Map<String,Object> req = ControllerContext.getContext().getReq().getContent();
        String regMember = req.get("regMember").toString();
        List<Object> params = new ArrayList<>();
        params.add(regMember);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("memberId");
        returnFields.add("path");
        String sql = "select memberId,path from Team where memberId = ?";
        //查询自己的path
        List<Map<String,Object>> myPaths = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        String myPath ="";
        //如果自己有推荐人，则取自己的推荐人作为自己推荐的会员的推荐人
        if(null != myPaths && myPaths.size()>0){
            if(null !=myPaths.get(0) && null !=myPaths.get(0).get("path")
                    && !"".equals(myPaths.get(0).get("path"))){
                int length = myPaths.get(0).get("path").toString().split("_").length;
                myPath = myPaths.get(0).get("path").toString().split("_")[length-1];
            }
        }
        //查询自己推荐的会员的path
        regMember = "%"+regMember+"%";
        params.clear();
        params.add(regMember);
        String sqlOther = "select memberId,path from Team where path like ?";
        List<Map<String,Object>> otherPaths = MysqlDaoImpl.getInstance().queryBySql(sqlOther,returnFields,params);
        //如果自己有推荐的会员，则修改自己推荐的会员的推荐人替换为自己的推荐人
        regMember = regMember.replaceAll("%","");
        if(null !=otherPaths && otherPaths.size()>0){
            for(Map<String,Object> map:otherPaths){
                String path = map.get("path").toString();
                if(path.contains(myPath)){
                    path = path.replace("_"+regMember,"");
                }else{
                    path = path.replace(regMember,myPath);
                }
                params.clear();
                params.add(path);
                params.add(map.get("memberId").toString());
                String updateSql = "update Team set path = ? where memberId =?";
                MysqlDaoImpl.getInstance().exeSql(updateSql,params,"Team");
            }
        }
    }


    /**
     * 修改关联账号的商家和会员时，同步更新会员的团队关系
     * @throws Exception
     */
    @GET
    @Path("/updateUserMemberTeam")
    public void updateUserMemberTeam() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        String sellerId = ControllerContext.getPString("sellerId");

        Map<String,Object> params = new HashMap<>();
        params.put("memberId",memberId);
        Map<String,Object> team = MysqlDaoImpl.getInstance().findOne2Map("Team",params,null,null);
        if(team==null || team.size()==0){
            team = new HashMap<>();
            team.put("_id",ZQUidUtils.genUUID());
            team.put("createTime",System.currentTimeMillis());
        }
        team.put("sellerId",sellerId);
        MysqlDaoImpl.getInstance().saveOrUpdate("Team",team);
    }

    /**
     * 切割团队层级，获取前两级;不满3级的，全部返回
     * @param path
     * @return
     * @throws Exception
     */
    public String splitPath(String path) throws Exception{
        String[] arr = path.substring(1,path.length()-1).split("_");
        String re = "_";

        if(arr.length<=2){
           re = path;
        }else{
            for(int i=1;i<3;i++){
                re+=arr[i]+"_";
            }
        }
        return re;
    }

    @GET
    @Path("/getTeamUp")
    public void getTeamUp() throws Exception{
        toResult(200,getTeamUp(ControllerContext.getPString("memberId")));
    }

    /**
     * 获取用户的上面3级分享团队 和 商家
     * @throws Exception
     */
    public static Map<String,Object> getTeamUp(String memberId) throws Exception{
        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        returnFields.add("path");
        returnFields.add("sellerId");
        returnFields.add("sellerCanUse");
        params.add(memberId);
        String sql = "select" +
                " t1.path" +
                ",t1.sellerId" +
                ",t2.canUse as sellerCanUse" +
                " from Team t1" +
                " left join Seller t2 on t1.sellerId = t2._id" +
                " where t1.memberId = ?";
        List<Map<String,Object>> teamList = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(teamList==null || teamList.size()==0 || teamList.get(0)==null || teamList.get(0).size()==0){
            return null;
        }
        Map<String,Object> team = teamList.get(0);

        if(StringUtils.mapValueIsEmpty(team,"path")){
            return team;
        }

        params.clear();
        returnFields.clear();
        returnFields.add("memberId");
        returnFields.add("canUse");
        returnFields.add("isBindCard");
        returnFields.add("name");
        sql = "select _id as memberId,canUse,isBindCard,realName as name from Member";

        String where = "";
        String path = team.get("path").toString();
        String[] arr = path.substring(1,path.length()-1).split("_");
        for(String str : arr){
            where+=",?";
            params.add(str);
        }
        if(StringUtils.isNotEmpty(where)){
            where = where.substring(1,where.length());
            sql += " where _id in ("+where+")";
        }

        List<Map<String,Object>> member = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        List<Map<String,Object>> re = new ArrayList<>();
        // 根据层级排序，方便分润时循环操作
        for(String str : arr){
            for(Map<String,Object> m : member){
                if(str.equals(m.get("memberId")) ){
                    re.add(m);
                }
            }
        }
        team.put("member",re);
        return team;
    }

    /**
     * 获取用户的上一级 分享团队
     * @throws Exception
     */
    public Map<String,Object> getTeamUpOne(String memberId) throws Exception{
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",memberId);
        Map<String,Object> team = MysqlDaoImpl.getInstance().findOne2Map("Team",params,new String[]{"path"},Dao.FieldStrategy.Include);
        if(team==null || team.size()==0 || StringUtils.mapValueIsEmpty(team,"path")){
            return null;
        }
        String path = team.get("path").toString();
        String[] arr = path.substring(1,path.length()-1).split("_");
        Message msg = Message.newReqMessage("1:GET@/crm/Member/getMyInfoById2");
        msg.getContent().put("_id",arr[0]);
        Map<String,Object> re = ServiceAccess.callService(msg).getContent();
        return re;
    }

    /**
     * 获取用户的下面3级分享团队
     * @throws Exception
     */
    public Page getTeamUnder(Map<String,Object> filter) throws Exception{
        long pageNo = Long.parseLong(filter.get("pageNo").toString());
        int pageSize = Integer.parseInt(filter.get("pageSize").toString());
        String memberId="",sellerId="";

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String where = " where 1=1";

        if(StringUtils.mapValueIsEmpty(filter,"sellerId")){
            memberId = filter.get("memberId").toString();
        } else{
            sellerId = filter.get("sellerId").toString();
            where += " and t1.sellerId = ?";
            params.add(sellerId);
        }

        if(StringUtils.isEmpty(ControllerContext.getPString("pageNo"))){
            pageNo = 1;
        }
        if(StringUtils.isEmpty(ControllerContext.getPString("pageSize"))){
            pageSize = 10;
        }
        if(!StringUtils.mapValueIsEmpty(filter,"memberId")){
            where += " and t1.path like ?";
            params.add("%"+memberId+"%");
        }

        String from = " from Team t1" +
                " left join Member t2 on t1.memberId = t2._id";
        String sql = "select count(t1._id) as totalCount" +
                from+where;
        returnFields.add("totalCount");
        List<Map<String, Object>> cardCount = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        Long totalNum = 0L;
        if (cardCount.size() != 0) {
            totalNum = (Long) cardCount.get(0).get("totalCount");
        }
        Page page = new Page(pageNo, pageSize, totalNum);

        returnFields.clear();
        returnFields.add("teamId");
        returnFields.add("realName");
        returnFields.add("idCard");
        returnFields.add("icon");
        returnFields.add("mobile");
        returnFields.add("sex");
        returnFields.add("isRealName");
        returnFields.add("isBindCard");
        returnFields.add("cardNo");
        returnFields.add("createTime");
        returnFields.add("canUse");
        returnFields.add("realArea");
        returnFields.add("realAddress");

        sql = "select" +
                " t1._id as teamId" +
                ",t2.realName" +
                ",t2.idCard" +
                ",t2.icon" +
                ",t2.mobile" +
                ",(case when t2.sex=1 then '男' when t2.sex=2 then '女' else '' end) as sex" +
                ",t2.isRealName" +
                ",t2.isBindCard" +
                ",t2.cardNo" +
                ",t2.createTime" +
                ",t2.canUse" +
                ",t2.realArea" +
                ",t2.realAddress" +
                from + where+
                " order by t2.createTime desc" +
                " limit " + page.getStartIndex() + "," + pageSize;

        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        page.setItems(re);
        return page;
    }

    @GET
    @Path("/getTeamUnder")
    public void getTeamUnder() throws Exception{
        Map<String,Object> req = ControllerContext.getContext().getReq().getContent();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if(StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"请登录");
        }
        req.put("memberId",memberId);
        toResult(200,getTeamUnder(req));
    }

    /**
     * 获取用户的下面3级分享团队 以及 该用户的分享人
     * @throws Exception
     */
    @GET
    @Path("/getTeamUpUnder")
    public void getTeamUpUnder() throws Exception{
        Map<String,Object> filter = ControllerContext.getContext().getReq().getContent();
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        Map<String,Object> re = new HashMap<>();
        if(!StringUtils.isEmpty(memberId) && memberId.substring(0,1).equals("M")){
            filter.put("memberId",memberId);
        }else if(!StringUtils.isEmpty(sellerId) && sellerId.substring(0,1).equals("S")){
            filter.put("sellerId",sellerId);
        }else{
            checkAdmin();
            if(StringUtils.isEmpty(memberId) || !memberId.substring(0,1).equals("M")){
                memberId = ControllerContext.getPString("memberId");
                filter.put("memberId",memberId);
            }else if(!StringUtils.isEmpty(sellerId) || !sellerId.substring(0,1).equals("S")){
                filter.put("sellerId",ControllerContext.getPString("sellerId"));
            }
        }

        if(!StringUtils.mapValueIsEmpty(filter,"memberId")){
            re.put("up",getTeamUpOne(memberId));
        }
        re.put("under",getTeamUnder(filter));

        toResult(200,re);
    }

    /**
     * 查询单笔交易记录 团队收益
     * @throws Exception
     */
    @GET
    @Path("/getTeamEarnings")
    public void getTeamEarnings() throws Exception{
        String orderNo = ControllerContext.getPString("orderNo");

        Map<String,Object> params = new HashMap<>();
        params.put("orderNo",orderNo);
        Map<String,Object> order = MysqlDaoImpl.getInstance().findOne2Map("OrderInfo",params,new String[]{"memberId"},Dao.FieldStrategy.Include);

        if(order==null || order.size()==0){
            return;
        }

        String memberId=order.get("memberId").toString();

        String type = ControllerContext.getPString("type");
        Map<String, Object> re = new HashMap<>();
        //默认查询利润分配
        if(StringUtils.isEmpty(type)){
            type="4";
        }
        List<Object> p = new ArrayList<>();
        List<String> r = new ArrayList<>();
        String[] user = new String[]{"Agent","Factor","Seller","Member"};

        String fields = "";
        String orderBy = "";
        String where = "";

        for(String str : user){
            String lowStr = str.toLowerCase();
            p.add(orderNo);
            if(str.equals("Agent")){
                fields = ",t2.level";
                orderBy = " order by t2.level asc";
                r.add("level");
            }
            if(str.equals("Agent") || str.equals("Factor")){
                p.add(type);
                where = " and t1.type=?";
            }else if(str.equals("Seller")){
                p.add("6");
                where = " and t1.tradeType=?";
            }else if(str.equals("Member")){
                fields=",t2.mobile";
                r.add("mobile");
                p.add("7");
                where = " and t1.tradeType=?";
            }
            String sql = "select" +
                    " distinct t1.orderId" +
                    ",t2.name" +
                    ",t1.orderCash" +
                    ",t2._id" +
                    fields +
                    " from "+str+"MoneyLog t1" +
                    " left join "+str+" t2 on t1."+lowStr+"Id = t2._id" +
                    " where t1.orderId=? " + where +
                    orderBy;
            r.add("orderId");
            r.add("name");
            r.add("orderCash");
            r.add("_id");
            List<Map<String, Object>> item = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);
            re.put(lowStr, item);
            p.clear();
            r.clear();
            orderBy="";
            fields="";
        }

        // 对三级会员排序
        if(!StringUtils.mapValueIsEmpty(re,"member")){
            params.clear();
            params.put("memberId",memberId);
            Map<String,Object> team = getTeamUp(memberId);
            if(!StringUtils.mapValueIsEmpty(team,"member")){
                List<Map<String,Object>> memberInfo = (List<Map<String,Object>>)team.get("member");
                List<Map<String,Object>> memberOrder = (List<Map<String,Object>>)re.get("member");

                List<Map<String,Object>> memberRe = new ArrayList<>();

                // 根据层级排序
                for(Map<String,Object> info : memberInfo){
                    for(Map<String,Object> ord : memberOrder){
                        if(info.get("memberId").equals(ord.get("_id")) ){
                            info.putAll(ord);
                            memberRe.add(info);
                        }
                    }
                }
                re.put("member",memberRe);
            }
        }

        toResult(200, re);
    }

    /**
     * 会员版/商家版 查询 团队收益 记录
     * @throws Exception
     */
    @GET
    @Path("/getTeamLog")
    public void getTeamLog() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        String sellerId = ControllerContext.getPString("sellerId");

        if(StringUtils.isEmpty(sellerId) && StringUtils.isEmpty(memberId)){
            memberId = ControllerContext.getContext().getCurrentUserId();
            sellerId = ControllerContext.getContext().getCurrentSellerId();

            if(StringUtils.isEmpty(sellerId) && StringUtils.isEmpty(memberId)){
                throw new UserOperateException(500,"请登录");
            }
        }
        long pageNo = ControllerContext.getPLong("pageNo");
        int pageSize = ControllerContext.getPInteger("pageSize");
        long startTime = ControllerContext.getPLong("startTime");
        long endTime = ControllerContext.getPLong("endTime");

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();
        String where = " where 1=1";

        if (startTime != 0) {
            where += " and t1.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<=?";
            params.add(endTime);
        }

        if(StringUtils.isEmpty(ControllerContext.getPString("pageNo"))){
            pageNo = 1;
        }
        if(StringUtils.isEmpty(ControllerContext.getPString("pageSize"))){
            pageSize = 10;
        }

        String mainTable = "MemberMoneyLog";
        if(StringUtils.isNotEmpty(sellerId)){
            mainTable="SellerMoneyLog";
            where += " and t1.sellerId = ? and t1.tradeType=6";
            params.add(sellerId);
        }else{
            where += " and t1.memberId = ? and t1.tradeType=7";
            params.add(memberId);
        }

        String from = " from "+mainTable+" t1" +
                " left join OrderInfo t2 on t1.orderId = t2.orderNo" +
                " left join Member t3 on t2.memberId = t3._id";

        String sql = "select count(t1._id) as totalCount" +
                from+where;
        returnFields.add("totalCount");
        List<Map<String, Object>> count = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        long totalNum = 0L;
        if (count.size() != 0) {
            totalNum = Long.parseLong(count.get(0).get("totalCount").toString());
        }
        Page page = new Page(pageNo, pageSize, totalNum);
        returnFields.clear();
        returnFields.add("shareId");
        returnFields.add("orderCash");
        returnFields.add("createTime");
        returnFields.add("orderNo");
        returnFields.add("branchMemberId");
        returnFields.add("realName");
        returnFields.add("mobile");
        sql = "select" +
                " t1.memberId as shareId" +
                ",t1.orderCash" +
                ",t1.createTime" +
                ",t2.orderNo" +
                ",t2.memberId as branchMemberId" +
                ",t3.realName" +
                ",t3.mobile" +
                from +
                where +
                " order by t1.createTime desc"+
                " limit " + page.getStartIndex() + "," + pageSize;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        page.setItems(re);
        toResult(200,page);
    }

    /**
     * 会员版/商家版 查询 团队收益 记录
     * @throws Exception
     */
    @GET
    @Path("/getTeamCount")
    public void getTeamCount() throws Exception{
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String sellerId = ControllerContext.getContext().getCurrentSellerId();
        if(StringUtils.isEmpty(sellerId) && StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"请登录");
        }
        toResult(200,getTeamCount(memberId,sellerId));
    }

    /**
     * 查询团队关系记录
     * @throws Exception
     */
    @GET
    @Path("/getMemberTeam")
    public void getMemberTeam() throws Exception{
        String memberId = ControllerContext.getPString("memberId");
        Map<String,Object> params = new HashMap<>();
        params.put("memberId",memberId);
        Map<String,Object> team = MysqlDaoImpl.getInstance().findOne2Map("Team",params,null,null);
        toResult(200,team);
    }

    /**
     * 平台 查询 团队收益 记录
     * @throws Exception
     */
    @GET
    @Path("/getTeamCountByAdmin")
    public void getTeamCountByAdmin() throws Exception{
        checkAdmin();

        String memberId = ControllerContext.getPString("memberId");
        String sellerId = ControllerContext.getPString("sellerId");

        if(StringUtils.isEmpty(sellerId) && StringUtils.isEmpty(memberId)){
            throw new UserOperateException(500,"请选择用户");
        }

        toResult(200,getTeamCount(memberId,sellerId));
    }

    public Map<String,Object> getTeamCount(String memberId,String sellerId) throws Exception{
        String where = " where 1=1";

        List<Object> params = new ArrayList<>();
        List<String> returnFields = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long dayStart = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MILLISECOND, -1);
        long dayEnd = c.getTimeInMillis();
        c.add(Calendar.MILLISECOND, 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        long monthStart = c.getTimeInMillis();
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.MILLISECOND, -1);
        long monthEnd = c.getTimeInMillis();


        params.add(dayStart);
        params.add(dayEnd);
        params.add(dayStart);
        params.add(dayEnd);
        params.add(monthStart);
        params.add(monthEnd);
        params.add(monthStart);
        params.add(monthEnd);

        returnFields.add("total");
        returnFields.add("totalNum");
        returnFields.add("day");
        returnFields.add("dayNum");
        returnFields.add("month");
        returnFields.add("monthNum");

        String entity;
        if(StringUtils.isNotEmpty(sellerId)){
            where += " and t1.sellerId = ? and t1.tradeType = 6";
            params.add(sellerId);
            entity = "SellerMoneyLog";
        }else{
            where += " and t1.memberId = ? and t1.tradeType = 7";
            params.add(memberId);
            entity = "MemberMoneyLog";
        }

        String sql = "select" +
                " sum(t1.orderCash) as total" +
                ",count(t1.orderCash) as totalNum" +
                ",sum(case when t1.createTime>=? and t1.createTime<=? then t1.orderCash else 0 end) as day" +
                ",sum(case when t1.createTime>=? and t1.createTime<=? then 1 else 0 end) as dayNum" +
                ",sum(case when t1.createTime>=? and t1.createTime<=? then t1.orderCash else 0 end) as month" +
                ",sum(case when t1.createTime>=? and t1.createTime<=? then 1 else 0 end) as monthNum" +
                " from "+entity+" t1" +
                where;
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);
        if(re==null || re.size()==0){
            return null;
        }

        return re.get(0);
    }

//    /**
//     * 将错误数据修正
//     * 将_A_B_C_改为_C_B_A_
//     * @throws Exception
//     */
//    @GET
//    @Path("/reviseTeam")
//    public void reviseTeam() throws Exception{
//        checkAdmin();
//        List<Map<String,Object>> list = MysqlDaoImpl.getInstance().findAll2Map("Team",null,null,new String[]{"_id","path"},Dao.FieldStrategy.Include);
//
//        String path;
//        String[] arr;
//        for(Map<String,Object> item : list){
//            if(StringUtils.mapValueIsEmpty(item,"path")){
//                continue;
//            }
//
//            path = item.get("path").toString();
//            path = path.substring(1,path.length()-1);
//            arr = path.split("_");
//
//            if(arr.length<=1){
//                continue;
//            }
//
//            path = "";
//            for(String str : arr){
//                path = str + "_" + path;
//            }
//            path="_" + path;
//
//            item.put("path",path);
//            System.out.println(path);
//            MysqlDaoImpl.getInstance().saveOrUpdate("Team",item);
//        }
//    }

}
