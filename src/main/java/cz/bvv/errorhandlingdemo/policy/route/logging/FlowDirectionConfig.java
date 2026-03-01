package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import org.springframework.util.StringUtils;

public record FlowDirectionConfig(String source, String target, String process) {
    public static final FlowDirectionConfig EMPTY = new FlowDirectionConfig(null, null, null);

    public static FlowDirectionConfig fromAnnotation(FlowDirection flowDirection) {
        if (flowDirection == null) {
            return EMPTY;
        }
        return new FlowDirectionConfig(
          trimToNull(flowDirection.source()),
          trimToNull(flowDirection.target()),
          trimToNull(flowDirection.process())
        );
    }

    private static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
