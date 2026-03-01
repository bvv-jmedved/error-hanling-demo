package cz.bvv.errorhandlingdemo.policy.route.logging;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates
 * how route metadata is translated into stable operational logs.
 */
public enum RecoveryAction {
    NONE,
    TOKEN_REFRESH_START,
    TOKEN_REFRESH_OK,
    TOKEN_REFRESH_FAILED,
    RETRY_AFTER_REFRESH
}
