package com.github.flycatzly.sqlparsing.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.github.flycatzly.sqlparsing.context.Context;
import com.github.flycatzly.sqlparsing.handler.XmlParser;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.token.TokenHandler;
import com.github.flycatzly.sqlparsing.token.TokenParser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author Alvin
 */
public class DynamicSqlEngine {

    public Cache cache = new Cache();

    public String setText(String text){
        return String.format("<root>%s</root>", text);
    }

    public SqlMeta parse(String text, Map<String, Object> params) {
        SqlNode sqlNode = parseXml2SqlNode(setText(text));
        Context context = new Context(params);
        parseSqlText(sqlNode, context);
        parseParameter(context);
        SqlMeta sqlMeta = new SqlMeta(text,context.getSql(), context.getJdbcParameters(),params);
        return sqlMeta;
    }

    public SqlMeta parse(String text, Map<String, Object> params,String dBType) {
        SqlNode sqlNode = parseXml2SqlNode(setText(text));
        Context context = new Context(params);
        parseSqlText(sqlNode, context);
        parseParameter(context);
        String result = SQLUtils.format(context.getSql(), dBType);
        SqlMeta sqlMeta = new SqlMeta(text,result, context.getJdbcParameters(),params);
        return sqlMeta;
    }

    public Set<String> parseParameter(String text) {
        SqlNode sqlNode = parseXml2SqlNode(setText(text));
        HashSet<String> set = new HashSet<>();
        sqlNode.applyParameter(set);
        return set;
    }

    private SqlNode parseXml2SqlNode(String text) {
        SqlNode node = cache.getNodeCache().get(text);
        if (node == null) {
            node = XmlParser.parseXml2SqlNode(text);
            cache.getNodeCache().put(text, node);
        }
        return node;
    }

    /**
     * 解析标签，去除标签，替换 ${}为常量值, #{}保留不变
     *
     * @param sqlNode
     * @param context
     */
    private void parseSqlText(SqlNode sqlNode, Context context) {
        sqlNode.apply(context);
    }

    /**
     * #{}替换成?，并且将?对应的参数值按顺序保存起来
     *
     * @param context
     */
    private void parseParameter(Context context) {
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                Object value = context.getOgnlValue(content);
                if (value == null) {
                    throw new RuntimeException("could not found value : " + content);
                }
                context.addParameter(value);
                return "?";
            }
        });
        String sql = tokenParser.parse(context.getSql());
        context.setSql(sql);
    }

    public void destroy(){
        cache.destroy();
    }

    public void destroy(String text){
        cache.destroy(setText(text));
    }

    public void put(String text){
        parseXml2SqlNode(setText(text));
    }

}
