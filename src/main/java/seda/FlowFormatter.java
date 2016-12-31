package seda;

import com.google.common.base.Strings;

import java.util.Optional;

class FlowFormatter {

    private final String separator;
    private final String initial;

    FlowFormatter(String separator, String initial) {
        this.separator = orEmpty(separator);
        this.initial = orEmpty(initial);
    }

    private String orEmpty(String input) {
        return Optional.ofNullable(input).orElse("");
    }

    String getInitial() {
        return initial;
    }

    String getRow(String condition, String from, String to) {
        if (!Strings.isNullOrEmpty(condition)) {
            return String.format("%s%s(%s)%s", from, separator, condition, to);
        }
        return String.format("%s%s%s", from, separator, to);
    }
}
