package cz.bvv.errorhandlingdemo.ftp;

import java.io.IOException;
import java.net.ServerSocket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Embedded FTP test infrastructure used by the demo runtime.
 *
 * <p>It allows repeatable local and test execution without external FTP dependencies.
 */
@Component
public class FtpRuntimeSettings {
  private final int port;

  public FtpRuntimeSettings(@Value("${demo.ftp.port:2121}") int configuredPort) {
    this.port = configuredPort > 0 ? configuredPort : allocateEphemeralPort();
  }

  public int port() {
    return port;
  }

  private static int allocateEphemeralPort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to allocate dynamic FTP port", ex);
    }
  }
}
