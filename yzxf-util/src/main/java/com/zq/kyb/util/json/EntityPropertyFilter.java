package com.zq.kyb.util.json;

import net.sf.json.util.PropertyFilter;

public class EntityPropertyFilter implements PropertyFilter {

    @Override
    public boolean apply(Object source, String name, Object value) {
        return false;
    }
}
