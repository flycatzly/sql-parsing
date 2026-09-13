package com.github.flycatzly.sqlparsing.context;


import com.github.flycatzly.sqlparsing.utils.OgnlUtil;

import java.util.*;


public class Context {

    StringBuilder sqlBuilder = new StringBuilder();
    List<Object> jdbcParameters = new ArrayList<>();
    Set<String> paramNames = new HashSet<>();
    Map<String, Object> data;

    public Context(Map<String, Object> data) {
        this.data = data;
    }

    public void appendSql(String text) {
        if (text != null) {
            sqlBuilder.append(text);
        }
    }

    public void addParameter(Object o) {
        jdbcParameters.add(o);
    }

    public void addParameterName(String o) {
        paramNames.add(o);
    }

    /**
     * 通过ognl表达式获取值
     *
     * @param expression
     * @return
     */
    public Object getOgnlValue(String expression) {
        return OgnlUtil.getValue(expression, data);
    }

    public Boolean getOgnlBooleanValue(String expression) {
        return OgnlUtil.getBooleanValue(expression, data);
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public void setSql(String text) {
        sqlBuilder = new StringBuilder(text);
    }

    public List<Object> getJdbcParameters() {
        return jdbcParameters;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public void setSqlBuilder(StringBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    public void setJdbcParameters(List<Object> jdbcParameters) {
        this.jdbcParameters = jdbcParameters;
    }

    public Set<String> getParamNames() {
        return paramNames;
    }

    public void setParamNames(Set<String> paramNames) {
        this.paramNames = paramNames;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
