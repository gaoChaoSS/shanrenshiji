package com.zq.kyb.core.ctrl;

import com.zq.kyb.core.dao.Dao;

/**
 * <p>
 * 作用: 封装节本操作方法的action接口
 * </p>
 * Timestamp: 2007-4-13 Time: 15:08:32
 */
public interface BaseAction {

    public void setEntityName(String entityName);

    public void setModelName(String modelName);

    public void setDao(Dao dao);
}
