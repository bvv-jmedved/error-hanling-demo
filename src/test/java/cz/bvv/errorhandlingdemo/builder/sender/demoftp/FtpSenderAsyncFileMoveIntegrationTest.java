package cz.bvv.errorhandlingdemo.builder.sender.demoftp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * FtpSenderAsyncFileMoveIntegrationTest integration test.
 *
 * <p>Verifies end-to-end route behavior across policy, retry, and failure-contract boundaries and
 * documents expected architecture behavior.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("poc")
class FtpSenderAsyncFileMoveIntegrationTest {

  private static final Path INPUT_DIR = Path.of("ftp-home", "test");
  private static final Path ARCHIVE_DIR = INPUT_DIR.resolve(".archive");
  private static final Path ERROR_DIR = INPUT_DIR.resolve(".error");

  @BeforeEach
  void cleanFolders() throws IOException {
    Files.createDirectories(INPUT_DIR);
    deleteChildren();
    Files.createDirectories(INPUT_DIR);
    Files.createDirectories(ARCHIVE_DIR);
    Files.createDirectories(ERROR_DIR);
  }

  @Test
  void shouldMoveHappyPathFileToArchive() throws Exception {
    String fileName = "ftp-happy-" + UUID.randomUUID() + ".txt";
    Path inputFile = INPUT_DIR.resolve(fileName);

    Files.writeString(inputFile, "payload=demo", StandardCharsets.UTF_8);

    Path archivedFile = waitForFileBySuffix(ARCHIVE_DIR, fileName, Duration.ofSeconds(12));
    assertThat(archivedFile).isNotNull();
    assertThat(Files.exists(inputFile)).isFalse();
  }

  @Test
  void shouldMoveFailedFileToErrorFolder() throws Exception {
    String fileName = "ftp-failure-" + UUID.randomUUID() + ".txt";
    Path inputFile = INPUT_DIR.resolve(fileName);

    Files.writeString(
        inputFile,
        String.join(
            "\n", "payload=demo", "X_THROW_IN=sender-validate", "X_THROW_TYPE=business-validation"),
        StandardCharsets.UTF_8);

    Path failedFile = waitForFileBySuffix(ERROR_DIR, fileName, Duration.ofSeconds(12));
    assertThat(failedFile).isNotNull();
    assertThat(findFileBySuffix(ARCHIVE_DIR, fileName)).isNull();
    assertThat(Files.exists(inputFile)).isFalse();
  }

  private static Path waitForFileBySuffix(
      Path dir, String expectedFileNameSuffix, Duration timeout) {
    AtomicReference<Path> fileRef = new AtomicReference<>();
    await()
        .atMost(timeout)
        .pollInterval(Duration.ofMillis(150))
        .until(
            () -> {
              Path file = findFileBySuffix(dir, expectedFileNameSuffix);
              fileRef.set(file);
              return file != null;
            });
    return fileRef.get();
  }

  private static Path findFileBySuffix(Path dir, String expectedSuffix) throws IOException {
    try (var stream = Files.list(dir)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith("-" + expectedSuffix))
          .findFirst()
          .orElse(null);
    }
  }

  private static void deleteChildren() throws IOException {
    try (var stream = Files.walk(FtpSenderAsyncFileMoveIntegrationTest.INPUT_DIR)) {
      stream
          .filter(path -> !path.equals(FtpSenderAsyncFileMoveIntegrationTest.INPUT_DIR))
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.deleteIfExists(path);
                } catch (IOException ex) {
                  throw new IllegalStateException("Failed to clean test FTP path: " + path, ex);
                }
              });
    }
  }
}
