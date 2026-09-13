package com.github.flycatzly.sqlparsing.node;


import com.github.flycatzly.sqlparsing.context.Context;

import java.util.Set;


/**
 * @author Alvin
 */
public interface SqlNode {

    void apply(Context context);

    void applyParameter(Set<String> set);

}
