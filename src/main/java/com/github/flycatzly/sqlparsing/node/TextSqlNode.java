package com.github.flycatzly.sqlparsing.node;

import com.github.flycatzly.sqlparsing.token.TokenParser;
import com.github.flycatzly.sqlparsing.context.Context;
import com.github.flycatzly.sqlparsing.token.TokenHandler;

import java.util.Set;


public class TextSqlNode implements SqlNode {

    public String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public void apply(Context context) {
        //解析常量值 ${xxx}
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                Object value = context.getOgnlValue(paramName);
                return value == null ? "" : value.toString();
            }
        });
        String str = tokenParser.parse(text);
        context.appendSql(str);
    }

    @Override
    public void applyParameter(Set<String> set) {
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        String s = tokenParser.parse(text);

        TokenParser tokenParser2 = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        tokenParser2.parse(s);
    }
}
