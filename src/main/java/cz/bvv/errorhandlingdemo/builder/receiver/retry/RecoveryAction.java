package cz.bvv.errorhandlingdemo.builder.receiver.retry;

/**
 * Receiver retry detail actions emitted into monitoring logs.
 *
 * <p>Values describe intermediate recovery steps so operators can distinguish in-flight retry
 * activity from final route outcomes.
 */
public enum RecoveryAction {
  NONE,
  TOKEN_REFRESH_START,
  TOKEN_REFRESH_OK,
  TOKEN_REFRESH_FAILED,
  RETRY_AFTER_REFRESH
}
