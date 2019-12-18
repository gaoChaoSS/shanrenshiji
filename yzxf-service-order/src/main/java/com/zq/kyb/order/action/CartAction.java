package com.zq.kyb.order.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.order.util.XmlUtils;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.MessageDigestUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CartAction extends BaseActionImpl {
    /**
     * 保存我的购物车
     * @throws Exception
     */
    @POST
    @Member
    @Path("/saveMyCart")
    public void saveMyCart() throws Exception {
        String memberId = ControllerContext.getContext().getCurrentUserId();
        String cartId = ControllerContext.getPString("cartId");
        String productId = ControllerContext.getPString("productId");
        int count = ControllerContext.getPInteger("count");
        String isReset = ControllerContext.getPString("isReset");
        JSONArray spec = (net.sf.json.JSONArray) ControllerContext.getContext().getReq().getContent().get("spec");

//        if(true){
//            throw new UserOperateException(500,"暂未开放线上支付");
//        }

        Map<String,Object> cart = new HashMap<>();
        double unitPrice=0.0;
        double price=0.0;
        //如果没有cartId,则是新增购物车;若有,则是修改购物车数量
        if(StringUtils.isEmpty(cartId)){
            cartId=UUID.randomUUID().toString();

            Map<String,Object> params = new HashMap<>();
            params.put("memberId",memberId);
            long totalNum = MysqlDaoImpl.getInstance().findCount("Cart",params);

            if(StringUtils.isEmpty(productId)){
                throw new UserOperateException(500, "获取商品数据失败");
            }
            if(count<1){
                throw new UserOperateException(500, "请选择正确的商品数量");
            }

            //获取商品数据
            Map<String, Object> product = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", productId, null, null);
            if (product == null || product.size() == 0 || product.get("sellerId") == null) {
                throw new UserOperateException(500, "获取商品数据失败");
            }
            //检查该会员是否收藏过相同的商品的购物车,且规格相同;若存在,则更改以前的购物车
            List<Object> p = new ArrayList<>();
            p.add(productId);
            p.add(memberId);
            List<String> r = new ArrayList<>();
            r.add("_id");
            r.add("spec");
            r.add("count");
            String sql = "select _id,spec,count from Cart where productId=? and memberId=?";
            List<Map<String, Object>> oldProduct = MysqlDaoImpl.getInstance().queryBySql(sql,r,p);
            if (oldProduct != null && oldProduct.size() != 0) {
                for(int i=0,len=oldProduct.size();i<len;i++) {
                    JSONArray oldSpec = JSONArray.fromObject(oldProduct.get(i).get("spec"));
                    boolean flag = true;
                    if (oldSpec.size() != spec.size()) {
                        flag = false;
                        if(totalNum>=20){
                            throw new UserOperateException(500, "购物车最多存放20件不同的商品");
                        }
                        continue;
                    }
                    for (int s = 0; s < oldSpec.size(); s++) {
                        if (!oldSpec.get(s).equals(spec.get(s))) {
                            flag = false;
                            if(totalNum>=20){
                                throw new UserOperateException(500, "购物车最多存放20件不同的商品");
                            }
                            break;
                        }
                    }
                    if (flag) {
                        cartId = oldProduct.get(i).get("_id").toString();
                        //如果需要重置之前的数量,则不累计
                        if(!StringUtils.isEmpty(ControllerContext.getPString("isReset")) && ControllerContext.getPBoolean("isReset")){
                            count = (int) oldProduct.get(i).get("count");
                        }else{
                            count += (int) oldProduct.get(i).get("count");
                        }
                        break;
                    }
                }
            }else{
                if(totalNum>=20){
                    throw new UserOperateException(500, "购物车最多存放20件不同的商品");
                }
            }
            cart.put("sellerId",product.get("sellerId"));
            cart.put("productId",product.get("_id"));
            cart.put("creator",memberId);
            cart.put("memberId",memberId);
            cart.put("createTime",System.currentTimeMillis());
            cart.put("spec",spec.toString());

            unitPrice = new OrderInfoAction().getPriceBySpec(product,spec);
        }else{
            Map<String, Object> oldCart = MysqlDaoImpl.getInstance().findById2Map("Cart",cartId,null,null);
            if(oldCart==null || oldCart.size()==0 || StringUtils.mapValueIsEmpty(oldCart,"unitPrice")){
                throw new UserOperateException(500, "获取购物车商品单价失败");
            }
            unitPrice=(double)oldCart.get("unitPrice");
        }
        //计算金额
        price = BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.multiply(unitPrice, Double.valueOf(count)));
        if(price<=0){
            throw new UserOperateException(500, "商品金额异常");
        }
        cart.put("_id",cartId);
        cart.put("price",price);
        cart.put("unitPrice",unitPrice);
        cart.put("count",count);
        MysqlDaoImpl.getInstance().saveOrUpdate("Cart",cart);
        toResult(200,cart);
    }

    /**
     * 为传入的购物车ID字符串添加引号
     * @throws Exception
     */
    protected String getCartArr(String cartId) throws Exception{
        String[] cartArr = cartId.split(",");
        cartId="";
        for(int i=0,len=cartArr.length;i<len;i++){
            cartId+="'"+cartArr[i]+"',";
        }
        return cartId.substring(0,cartId.length()-1);
    }

    /**
     * 删除我的购物车
     * @throws Exception
     */
    public void deleteMyCart(String cartId) throws Exception{
        String memberId = ControllerContext.getContext().getCurrentUserId();
        if(StringUtils.isEmpty(cartId)){
            throw new UserOperateException(500,"请选择需要删除的商品");
        }
        cartId=getCartArr(cartId);
        List<Object> params = new ArrayList<>();
        params.add(memberId);
        String sql = "delete from Cart where memberId = ? and _id in ("+cartId+")";
        MysqlDaoImpl.getInstance().exeSql(sql, params, "Cart", false);
    }

    /**
     * 删除我的购物车
     * @throws Exception
     */
    @POST
    @Member
    @Path("/deleteMyCart")
    public void deleteMyCart() throws Exception {
        String cartId = ControllerContext.getPString("cartId");
        deleteMyCart(cartId);
    }

    /**
     * 获取我的购物车
     * @throws Exception
     */
    public List<Map<String,Object>> getMyCart(String cartId) throws Exception {
        String currentId = ControllerContext.getContext().getCurrentUserId();
        List<Object> params = new ArrayList<>();
        params.add(currentId);
        List<String> returnFields = new ArrayList<>();

        String where = " where t1.memberId=? ";
        if(!StringUtils.isEmpty(cartId)){
            cartId=getCartArr(cartId);
            where+=" and t1._id in ("+cartId+")";
        }

        returnFields.clear();
        returnFields.add("sellerId");
        returnFields.add("_id");
        returnFields.add("productId");
        returnFields.add("createTime");
        returnFields.add("spec");
        returnFields.add("count");
        returnFields.add("unitPrice");
        returnFields.add("price");
        returnFields.add("sellerName");
        returnFields.add("integralRate");
        returnFields.add("productIcon");
        returnFields.add("productName");
        returnFields.add("salePrice");

        String sql = "select" +
                " t1.sellerId" +
                ",t1._id" +
                ",t1.productId" +
                ",t1.createTime" +
                ",t1.spec" +
                ",t1.count" +
                ",t1.unitPrice" +
                ",t1.price" +
                ",t2.name as sellerName" +
                ",t2.integralRate" +
                ",t3.icon as productIcon" +
                ",t3.name as productName" +
                ",t3.salePrice" +
                " from Cart t1" +
                " left join Seller t2 on t1.sellerId = t2._id" +
                " left join ProductInfo t3 on t1.productId = t3._id" +
                where+
                " order by t1.sellerId desc, t1.createTime desc";
        List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

        //按商家分组,re是按照商家排序的,所以这里只需要判断某个商品与前一个商品的商家是否一致
        List<Map<String,Object>> sellerList = new ArrayList<>();
        if(re!=null && re.size()>0){
            //分组的起始数
            int groupStartIndex=0;
            for(int i=0,len=re.size();i<len;i++){
                if(i==len-1 || !re.get(i).get("sellerId").toString().equals(re.get(i+1).get("sellerId").toString())){
                    Map<String,Object> sellerItem = new HashMap<>();
                    sellerItem.put("sellerName",re.get(i).get("sellerName"));
                    sellerItem.put("sellerId",re.get(i).get("sellerId"));
                    sellerItem.put("integralRate",re.get(i).get("integralRate"));

                    List<Object> productList = new ArrayList<>();
                    for(int j=groupStartIndex;j<i+1;j++){
                        productList.add(re.get(j));
                    }
                    sellerItem.put("product",productList);
                    sellerList.add(sellerItem);
                    groupStartIndex=i+1;
                }
            }
        }
        return sellerList;
    }

    /**
     * 获取我的购物车
     * @throws Exception
     */
    @GET
    @Member
    @Path("/getMyCart")
    public void getMyCart() throws Exception {
        String cartId = ControllerContext.getPString("cartId");
        toResult(200, getMyCart(cartId));
    }

//    public static void tempCreate() throws Exception{
//        String filePath="/Users/zq2014/Downloads/付款文档/city.xlsx";
//        System.out.println("开始");
//        try{
//            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
//            // 构造 XSSFWorkbook 对象，strPath 传入文件路径
//            XSSFWorkbook xwb = new XSSFWorkbook(is);
//            // 读取第一章表格内容
//            XSSFSheet sheet = xwb.getSheetAt(0);
//            // 定义 row、cell
//            XSSFRow row;
//            String cell;
//            // 循环输出表格中的内容
//            for (int i = sheet.getFirstRowNum()+1; i < sheet.getPhysicalNumberOfRows(); i++) {
//                row = sheet.getRow(i);
//                String sql = "insert into BankCity (provinceValue,province,cityValue,city,createTime,_id)" +
//                        " values (";
//                int count = 0;
//                for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {
//                    // 通过 row.getCell(j).toString() 获取单元格内容，
////                    System.out.print(cell + "\t");
//                    count++;
//                    if(count>4){
//                        break;
//                    }
//                    if(row.getCell(j)==null || StringUtils.isEmpty(row.getCell(j).toString())){
//                        sql+="'',";
//                    }else{
//                        sql+="'"+row.getCell(j).toString()+"',";
//                    }
//                }
//                sql+="'"+System.currentTimeMillis()+"'," +
//                        "'"+ZQUidUtils.genUUID()+"')";
////                sql = sql.substring(0,sql.length()-1)+")";
//                MysqlDaoImpl.getInstance().exeSql(sql,null,"BankCity",false);
//                System.out.println(sql);
//            }
//        }catch(Exception e) {
//            System.out.println("已运行xlRead() : " + e );
//        }
//    }
//
//    public static void main(String args[]) throws Exception{
//        try {
//            tempCreate();
//            MysqlDaoImpl.commit();
//        } catch (Exception e) {
//            MysqlDaoImpl.rollback();
//        }finally {
//            MysqlDaoImpl.clearContext();
//            Thread.sleep(1000);
//        }
//    }

//    public static void main(String args[]) throws Exception{
//        try {
//            String[] data1= {
//                    "0102",
//                    "0103",
//                    "0104",
//                    "0105",
//                    "0301",
//                    "0308",
//                    "0309",
//                    "0305",
//                    "0306",
//                    "0307",
//                    "0310",
//                    "0304",
//                    "0313",
//                    "0314",
//                    "0315",
//                    "0403",
//                    "0303",
//                    "0302",
//                    "0316",
//                    "0318",
//                    "0319",
//                    "0322",
//                    "0402"
//            };
//            String[] data2={
//                    "中国工商银行",
//                    "中国农业银行",
//                    "中国银行",
//                    "中国建设银行",
//                    "交通银行",
//                    "招商银行",
//                    "兴业银行",
//                    "中国民生银行",
//                    "广东发展银行",
//                    "平安银行股份有限公司",
//                    "上海浦东发展银行",
//                    "华夏银行",
//                    "其他城市商业银行",
//                    "其他农村商业银行",
//                    "恒丰银行",
//                    "中国邮政储蓄银行股份有限公司",
//                    "中国光大银行",
//                    "中信银行",
//                    "浙商银行股份有限公司",
//                    "渤海银行股份有限公司",
//                    "徽商银行",
//                    "上海农村商业银行",
//                    "其他农村信用合作社"
//            };
//
//            for(int i=0,len=data1.length;i<len;i++){
//                String sql ="insert into BankType (bankId,name,createTime,_id) values ("+
//                        "'"+data1[i]+"','"+data2[i]+"',"+
//                        "'"+System.currentTimeMillis()+"','"+ZQUidUtils.genUUID()+"')";
//                MysqlDaoImpl.getInstance().exeSql(sql,null,"BankType",false);
//            }
//            MysqlDaoImpl.commit();
//        } catch (Exception e) {
//            MysqlDaoImpl.rollback();
//        }finally {
//            MysqlDaoImpl.clearContext();
//            Thread.sleep(1000);
//        }
//    }

//    public static void main(String args[]) throws Exception{
//        Map<String,Object> map = XmlUtils.xmlToMap("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><payforrsp><ret>000000</ret><memo>成功</memo></payforrsp>");
//        System.out.println(map.get("memo"));
//    }
}
