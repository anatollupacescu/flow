package org.flow.core;

import java.util.Set;

public interface Flow {
    void addChild(Action child);

    void addChildWithCondition(Action child, String reason);

    void addChild(Set<SedaType> fieldList);

    void addChild(String operation, Data data, Set<SedaType> fieldList);

    void addChild(String operation, Set<SedaType> fieldList);
}
