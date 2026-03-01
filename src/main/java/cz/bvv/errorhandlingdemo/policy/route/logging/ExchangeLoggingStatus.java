package cz.bvv.errorhandlingdemo.policy.route.logging;

/**
 * Logging policy component used by the architecture demo.
 *
 * <p>This type participates in canonical process-status monitoring and demonstrates how route
 * metadata is translated into stable operational logs.
 */
public enum ExchangeLoggingStatus {
  STARTED,
  RUNNING,
  COMPLETED,
  FAILED,
  FAILURE,
  ENQUEUED,
  DEQUEUED
}
