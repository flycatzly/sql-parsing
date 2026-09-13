package com.github.flycatzly.sqlparsing.node;

import com.github.flycatzly.sqlparsing.context.Context;

import java.util.List;
import java.util.Set;


public class MixedSqlNode implements SqlNode {

    private final List<SqlNode> contents;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public void apply(Context context) {
        contents.forEach(node -> node.apply(context));
    }

    @Override
    public void applyParameter(Set<String> set) {
        contents.forEach(node -> node.applyParameter(set));
    }
}
