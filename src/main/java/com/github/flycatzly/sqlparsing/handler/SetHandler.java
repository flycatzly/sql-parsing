package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import com.github.flycatzly.sqlparsing.node.SetSqlNode;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @author Alvin
 */

public class SetHandler implements NodeHandler {

    public SetHandler() {
        // Prevent Synthetic Access
    }

    //todo 多个下自己在条件里写连接符 ”,“
    @Override
    public void handle(Element element, List<SqlNode> contents) {
/*        List<SqlNode> contentList = XmlParser.parseElement(element);
        contentList.forEach(item->{
            contents.add(new SetSqlNode(item));
        });*/
        List<SqlNode> contentsList = XmlParser.parseElement(element);
        SetSqlNode node = new SetSqlNode(new MixedSqlNode(contentsList));
        contents.add(node);
    }
}
