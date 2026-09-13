package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.ChooseNode;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class ChooseHandler implements NodeHandler {

    public ChooseHandler() {
        // Prevent Synthetic Access
    }

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> whenSqlNodes = new ArrayList<SqlNode>();
        List<SqlNode> otherwiseSqlNodes = new ArrayList<SqlNode>();

        XmlParser.handleWhenOtherwiseNodes(element,whenSqlNodes, otherwiseSqlNodes);
        SqlNode defaultSqlFragment = getDefaultSqlNode(otherwiseSqlNodes);
        ChooseNode chooseSqlFragment = new ChooseNode(whenSqlNodes, defaultSqlFragment);
        targetContents.add(chooseSqlFragment);
    }

    private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlFragments) {
        SqlNode defaultSqlFragment = null;
        if (defaultSqlFragments.size() == 1) {
            defaultSqlFragment = defaultSqlFragments.get(0);
        } else if (defaultSqlFragments.size() > 1) {
            throw new RuntimeException("Too many default (otherwise) elements in choose statement.");
        }
        return defaultSqlFragment;
    }
}
