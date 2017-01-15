package org.flow2;

import com.google.common.base.Strings;

import java.util.Optional;

class FlowFormatter {

    private final String separator;

    private FlowFormatter(String separator) {
        this.separator = orEmpty(separator);
    }

    private String orEmpty(String input) {
        return Optional.ofNullable(input).orElse("");
    }

    static FlowFormatter withSeparator(String separator) {
        return new FlowFormatter(separator);
    }

    String getRow(String condition, String from, String to) {
        if (!Strings.isNullOrEmpty(condition)) {
            return String.format("%s%s(%s)%s", from, separator, condition, to);
        }
        return String.format("%s%s%s", from, separator, to);
    }
}
