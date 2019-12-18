package com.zq.kyb.core.model;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对大数据对象的分页操作
 */

@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.FIELD)
public class Page implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final long DEFAULT_PAGE_NO = 1;

    private long pageNo = DEFAULT_PAGE_NO;
    private long totalNum = 0;
    private int pageSize = DEFAULT_PAGE_SIZE;
    public String whereStr;
    public String joinStr;
    /**
     * 需要查询的字段,用逗号隔开
     */
    private String fields;
    private String numFields;
    public String countHeadsStr;
    public String orderByStr;
    private Object[] params;
    private String entityType;

    private List<Object[]> items = new ArrayList<Object[]>();

    public Page() {

    }

    public Page(long pageNo, int pageSize, long totalNum) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalNum = totalNum;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        pageNo = pageNo > 0 ? pageNo : DEFAULT_PAGE_NO;
        this.pageNo = pageNo;
    }

    public long getTotalPage() {
        return ((totalNum - 1) / pageSize) + 1;
    }

    public int getPageSize() {

        return pageSize > 1000 ? 1000 : pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List getItems() {
        if (items == null) {
            return new ArrayList();
        }
        return items;
    }

    public void setItems(List pageList) {
        this.items = pageList;
    }

    public long getStartIndex() {
        pageNo = pageNo <= 0 ? 1 : pageNo;
        return (pageNo - 1) * pageSize;
    }

    public void setTotalNum(long totalNum) {
        totalNum = totalNum < 0 ? 0 : totalNum;
        this.totalNum = totalNum;
//        if (this.getTotalPage() < this.pageNo)
//            this.pageNo = 1;
    }

    public long getTotalNum() {
        return totalNum;
    }

    @Override
    public String toString() {
        return "Page{" + "pageNo=" + pageNo + ", pageSize=" + pageSize + ", pageList=" + items + ", whereStr=" + whereStr + ", joinStr=" + joinStr + ", fields=" + getFields() + ", countHeadsStr="
                + countHeadsStr + ", orderByStr=" + orderByStr + ", params=" + params + "}";
    }

    public String getWhereStr() {
        return StringUtils.isNotEmpty(whereStr) ? whereStr : "";
    }

    public void setWhereStr(String whereStr) {
        this.whereStr = whereStr;
    }

    public String getOrderByStr() {
        return this.orderByStr == null ? "" : this.orderByStr;
    }

    public void setOrderByStr(String orderByStr) {
        this.orderByStr = orderByStr;
    }

    public Object[] getParams() {
        return params == null ? new Object[]{} : params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getJoinStr() {
        return joinStr == null ? "" : joinStr;
    }

    public void setJoinStr(String joinStr) {
        this.joinStr = joinStr;
    }

    public String getCountHeadsStr() {
        return countHeadsStr;
    }

    public void setCountHeadsStr(String countHeadsStr) {
        this.countHeadsStr = countHeadsStr;
    }

    public void setStartIndex(long startIndex) {
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getNumFields() {
        return numFields;
    }

    public void setNumFields(String numFields) {
        this.numFields = numFields;
    }

    public static Map<String, Object> objectsToMap(String[] fields2, Object[] objs) {
        Map<String, Object> m = new HashMap<String, Object>();
        int i = 0;
        for (String key : fields2) {
            m.put(key, objs[i++]);
        }
        return m;
    }
}
