package com.github.flycatzly.sqlparsing.engine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Alvin
 */
public class SqlMeta {

    private String sql;
    private String oldSql;
    private List<Object> jdbcParamValues;
    private Object[] paramValueObj;
    private Map<String, Object> paramValues;

    public SqlMeta(String sql, List<Object> jdbcParamValues) {
        this.sql = sql;
        this.jdbcParamValues = jdbcParamValues;
    }

    public SqlMeta(String oldSql,String sql, List<Object> jdbcParamValues) {
        this.sql = sql;
        this.oldSql = oldSql;
        this.jdbcParamValues = jdbcParamValues;
    }

    public SqlMeta(String oldSql,String sql, List<Object> jdbcParamValues,Map<String, Object> paramValues) {
        this.sql = sql;
        this.oldSql = oldSql;
        this.jdbcParamValues = jdbcParamValues;
        this.paramValues = paramValues;
        this.paramValueObj =jdbcParamValues.toArray();
    }

    public String getOldSql() {
        return oldSql;
    }

    public void setOldSql(String oldSql) {
        this.oldSql = oldSql;
    }

    public Map<String, Object> getParamValues() {
        return paramValues;
    }

    public void setParamValues(Map<String, Object> paramValues) {
        this.paramValues = paramValues;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getJdbcParamValues() {
        return jdbcParamValues;
    }

    public void setParamValueObj(Object[] paramValueObj) {
        this.paramValueObj = paramValueObj;
    }

    public Object[] getParamValueObj() {
        return jdbcParamValues.toArray();
    }

    public void setJdbcParamValues(List<Object> jdbcParamValues) {
        this.jdbcParamValues = jdbcParamValues;
    }

    @Override
    public String toString() {
        return "SqlMeta{" +
                "sql='" + sql + '\'' +
                ", oldSql='" + oldSql + '\'' +
                ", jdbcParamValues=" + jdbcParamValues +
                ", paramValueObj=" + Arrays.toString(paramValueObj) +
                ", paramValues=" + paramValues +
                '}';
    }
}
