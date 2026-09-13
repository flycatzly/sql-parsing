package com.github.flycatzly.sqlparsing.node;

import java.util.Collections;
import java.util.List;


public class SetSqlNode extends TrimSqlNode {

    private static final List<String> COMMA = Collections.singletonList(",");

    public SetSqlNode(SqlNode contents) {
        //super(contents, "SET ", null, null, Arrays.asList(","));
        //super(contents, "SET ", null, COMMA,COMMA);
        super(contents, "SET ", null, null, COMMA);
    }
}
