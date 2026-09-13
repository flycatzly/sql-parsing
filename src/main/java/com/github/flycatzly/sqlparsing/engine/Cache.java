package com.github.flycatzly.sqlparsing.engine;

import com.github.flycatzly.sqlparsing.node.SqlNode;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Alvin
 */
public class Cache {

    ConcurrentHashMap<String, SqlNode> nodeCache = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, SqlNode> getNodeCache() {
        return nodeCache;
    }

    public void destroy(){
        nodeCache.clear();
    }

    public void destroy(String text){
        nodeCache.remove(text);
    }

}
