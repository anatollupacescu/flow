package seda;

import com.google.common.base.Strings;

import java.util.Optional;

public class FlowFormatter {

    private final String separator;

    public FlowFormatter(String separator) {
        this.separator = orEmpty(separator);
    }

    private String orEmpty(String input) {
        return Optional.ofNullable(input).orElse("");
    }

    public String getRow(String condition, String from, String to) {
        if (!Strings.isNullOrEmpty(condition)) {
            return String.format("%s%s(%s)%s", from, separator, condition, to);
        }
        return String.format("%s%s%s", from, separator, to);
    }
}
