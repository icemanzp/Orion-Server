/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletHeaderValueHolder.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午6:59:07
 * TODO
 */
package com.jack.netty.server.servlet;

import java.util.*;

import com.jack.netty.util.Assert;
import com.jack.netty.util.CollectionUtils;


/**
 * @Class Name HeaderValueHolder
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class HeaderValueHolder {
    private final List<Object> values = new LinkedList<Object>();


    public void setValue(Object value) {
        this.values.clear();
        this.values.add(value);
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public void addValues(Collection<?> values) {
        this.values.addAll(values);
    }

    public void addValueArray(Object values) {
        CollectionUtils.mergeArrayIntoCollection(values, this.values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(this.values);
    }

    public List<String> getStringValues() {
        List<String> stringList = new ArrayList<String>(this.values.size());
        for (Object value : this.values) {
            stringList.add(value.toString());
        }
        return Collections.unmodifiableList(stringList);
    }

    public Object getValue() {
        return (!this.values.isEmpty() ? this.values.get(0) : null);
    }

    public String getStringValue() {
        return (!this.values.isEmpty() ? this.values.get(0).toString() : null);
    }


    /**
     * Find a HeaderValueHolder by name, ignoring casing.
     *
     * @param headers the Map of header names to HeaderValueHolders
     * @param name    the name of the desired header
     * @return the corresponding HeaderValueHolder,
     * or {@code null} if none found
     */
    public static HeaderValueHolder getByName(Map<String, HeaderValueHolder> headers, String name) {
        Assert.notNull(name, "Header name must not be null");
        for (String headerName : headers.keySet()) {
            if (headerName.equalsIgnoreCase(name)) {
                return headers.get(headerName);
            }
        }
        return null;
    }
}
