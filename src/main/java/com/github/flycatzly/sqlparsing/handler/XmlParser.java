package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.node.TextSqlNode;
import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import org.dom4j.*;
import org.dom4j.tree.DefaultCDATA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Alvin
 */
public class XmlParser {

    static Map<String, NodeHandler> nodeHandlers = new HashMap<String, NodeHandler>() {
        {
            put("foreach", new ForeachHandler());
            put("if", new IfHandler());
            put("trim", new TrimHandler());
            put("where", new WhereHandler());
            put("set", new SetHandler());
            put("choose", new ChooseHandler());
            put("when", new WhenHandler());
            put("otherwise", new OtherwiseHandler());
            put("bind", new BindHandler());
        }
    };

    /**
     * 将xml内容解析成sqlNode类型
     * @param text
     * @return
     */
    public static SqlNode parseXml2SqlNode(String text) {
        Document document = null;
        try {
            document = DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            throw new RuntimeException("400", e);
        }
        Element rootElement = document.getRootElement();
        List<SqlNode> contents = parseElement(rootElement);
        SqlNode sqlNode = new MixedSqlNode(contents);
        return sqlNode;
    }

    /**
     * 解析单个标签的子内容，转化成sqlNode list
     * @param element
     * @return
     */
    public static List<SqlNode> parseElement(Element element) {
        List<SqlNode> nodes = new ArrayList<>();
        List<Object> children = element.content();
        for (Object node : children) {
            if (node instanceof Text) {
                TextSqlNode textSqlNode = new TextSqlNode(((Text) node).getText());
                nodes.add(textSqlNode);
            } else if (node instanceof Element) {
                String nodeName = ((Element) node).getName();
                NodeHandler handler = nodeHandlers.get(nodeName.toLowerCase());
                if (handler == null) {
                    throw new RuntimeException("tag not supported");
                }else{
                    //内部递归调用此方法
                    handler.handle((Element) node, nodes);
                }
            }else if(node instanceof DefaultCDATA){
                //<![CDATA[ ]]>
                String text =((DefaultCDATA) node).getText();
                TextSqlNode textSqlNode = new TextSqlNode(text);
                nodes.add(textSqlNode);
            }
        }
        return nodes;
    }

    public static void handleWhenOtherwiseNodes(Element element, List<SqlNode> whenSqlNodes, List<SqlNode> otherwiseSqlNodes) {
        List<SqlNode> nodes = new ArrayList<>();
        List<Object> children = element.content();
        for (Object child : children) {
           if (child instanceof Element) {
                String nodeName = ((Element) child).getName();
                NodeHandler handler = nodeHandlers.get(nodeName.toLowerCase());
                if (handler == null) {
                    throw new RuntimeException("tag not supported");
                }else if (handler instanceof WhenHandler) {
                    handler.handle((Element) child, whenSqlNodes);
                }else if (handler instanceof OtherwiseHandler) {
                   handler.handle((Element) child, otherwiseSqlNodes);
                }else if(handler instanceof DefaultCDATA){
                    //<![CDATA[ ]]>
                    String text =((DefaultCDATA) handler).getText();
                    TextSqlNode textSqlNode = new TextSqlNode(text);
                    nodes.add(textSqlNode);
                }
           }
        }
    }
}
