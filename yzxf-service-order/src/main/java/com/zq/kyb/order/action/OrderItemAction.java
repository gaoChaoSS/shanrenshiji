package com.zq.kyb.order.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用于订单产品项的修改删除和添加,一切以orderItemId为条件
 * Created by hujoey on 16/5/18.
 */
public class OrderItemAction extends BaseActionImpl {

    /**
     * 下单后需要保存一个item
     */
    public static Double saveItem(Map orderItem) throws Exception {
        //Map j = (Map) ControllerContext.getContext().getReq().getContent();
        String orderId = (String) orderItem.get("orderId");
        String productId = (String) orderItem.get("productId");
        Double count = Double.valueOf(orderItem.get("count").toString());

        Object discountStr = orderItem.get("discount");
        Double discount = 1.0;
        if (discountStr != null) {
            discount = Double.valueOf(discountStr.toString());
        }
        Object orderStatusStr = orderItem.get("orderStatus");

        if (orderStatusStr == null) {
            throw new UserOperateException(400, "订单状态必须设置");
        }
        Integer orderStatus = Integer.valueOf(orderStatusStr.toString());

        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(400, "订单号必须设置");
        }
        if (StringUtils.isEmpty(productId)) {
            throw new UserOperateException(400, "产品编号必须设置");
        }
        if (count == null || count <= 0) {
            throw new UserOperateException(400, "商品数量必须大于1， productId:" + productId);
        }
        if (discount == null || discount <= 0) {
            throw new UserOperateException(400, "折扣必须设置");
        }


        //Map<String, Object> product = MysqlDaoImpl.getInstance().findById2Map("ProductInfo", productId, null, null);

        Message msg = Message.newReqMessage("1:GET@/product/ProductInfo/show");
        msg.getContent().put("_id", productId);
        JSONObject product = ServiceAccess.callService(msg).getContent();

        if (product == null) {
            throw new UserOperateException(400, "商品不存在! productId:" + productId);
        }

        Object salePrice = product.get("salePrice");
        if (salePrice == null) {
            throw new UserOperateException(400, "商品未设定售价! productId:" + productId);
        }
        Double sellPrice = Double.valueOf(salePrice.toString());//售价

        Double price = BigDecimalUtil.multiply(sellPrice, discount);
        Double ji = BigDecimalUtil.multiply(price, count);//乘以数量

        Double oldPrice;
        if (StringUtils.mapValueIsEmpty(product, "oldPrice")) {
            oldPrice = sellPrice;
        } else {
            oldPrice = Double.valueOf(product.get("oldPrice").toString());//平均成本价
        }

        Double lirun = BigDecimalUtil.fixDoubleNum2(BigDecimalUtil.add(price, -oldPrice));
        Double lirunPercent = BigDecimalUtil.multiply(BigDecimalUtil.divide(lirun, price), 100);
        HashMap<String, Object> j = new HashMap<String, Object>();

        //每次改变类型都跟新产品的相关属性
        j.put("name", product.get("name"));
        j.put("icon", product.get("icon"));
        j.put("productNo", product.get("productNo"));
        j.put("price", price);//下单价
        j.put("sellPrice", sellPrice);//销售价
        j.put("oldPrice", oldPrice);//平均成本价
        j.put("lirun", lirun);
        j.put("lirunPercent", BigDecimalUtil.fixDoubleNum2(lirunPercent));
        j.put("count", count);


        if (!orderItem.containsKey("_id")) {//新加
            j.put("createTime", System.currentTimeMillis());
            j.put("creator", ControllerContext.getContext().getCurrentUserId());
            j.put("_id", UUID.randomUUID().toString());
            j.put("returnCount", 0);
        } else {
            j.put("_id", orderItem.get("_id"));
        }

        if (!StringUtils.mapValueIsEmpty(orderItem, "returnCount")) {
            Double returnCount = Double.valueOf(orderItem.get("returnCount").toString());
            j.put("returnCount", returnCount);
        }

        j.put("productId", product.get("_id"));
        j.put("orderId", orderId);//关联订单号
        MysqlDaoImpl.getInstance().saveOrUpdate("OrderItem", j);

        //检查产品的制作记录
        Boolean isNeedMake = !StringUtils.mapValueIsEmpty(product, "isNeedMake") && (Boolean) product.get("isNeedMake");
        //isNeedMake = isNeedMake == null ? false : isNeedMake;
        if (orderStatus == OrderInfoAction.ORDER_TYPE_BOOKING && isNeedMake) {
            // ProductMakeAction.saveProductMakeItem(j);
        }

        return ji;
    }


}
