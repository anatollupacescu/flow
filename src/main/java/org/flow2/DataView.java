package org.flow2;

final class DataView extends Node {

    final String method;

    DataView(Data data, String method) {
        super(data.name, data.fields);
        this.method = method;
    }
}
