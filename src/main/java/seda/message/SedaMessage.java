package seda.message;

import java.util.HashMap;
import java.util.Map;

public class SedaMessage extends HashMap<SedaType, String> {

    public SedaMessage() {
        super();
    }

    public SedaMessage(Map<SedaType, String> data) {
        super(data);
    }
}
