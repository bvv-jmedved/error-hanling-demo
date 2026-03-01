package cz.bvv.errorhandlingdemo.policy.route.logging;

import cz.bvv.errorhandlingdemo.foundation.annotation.FlowDirection;
import org.springframework.util.StringUtils;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates how route
 * metadata is translated into stable operational logs.
 */
public record FlowDirectionConfig(String source, String target, String process) {
  public static final FlowDirectionConfig EMPTY = new FlowDirectionConfig(null, null, null);

  /**
   * Creates normalized configuration from {@link FlowDirection} annotation.
   *
   * @param flowDirection annotation instance or {@code null}
   * @return normalized flow direction config
   */
  public static FlowDirectionConfig fromAnnotation(FlowDirection flowDirection) {
    if (flowDirection == null) {
      return EMPTY;
    }
    return new FlowDirectionConfig(
        trimToNull(flowDirection.source()),
        trimToNull(flowDirection.target()),
        trimToNull(flowDirection.process()));
  }

  private static String trimToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
