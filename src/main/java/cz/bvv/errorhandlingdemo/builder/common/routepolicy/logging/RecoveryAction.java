package cz.bvv.errorhandlingdemo.builder.common.routepolicy.logging;

public enum RecoveryAction {
    NONE,
    TOKEN_REFRESH_START,
    TOKEN_REFRESH_OK,
    TOKEN_REFRESH_FAILED,
    RETRY_AFTER_REFRESH
}
