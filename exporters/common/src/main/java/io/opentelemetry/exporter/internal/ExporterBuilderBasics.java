package io.opentelemetry.exporter.internal;

import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.common.export.RetryPolicy;
import javax.annotation.Nullable;
import java.time.Duration;

/**
 * <p>This class is internal and is hence not for public use. Its APIs are unstable and can change
 *  * at any time.
 * @param <T> The specific concrete builder type
 */
public interface ExporterBuilderBasics<T> {
  T setEndpoint(String endpoint);

  T addHeader(String key, String value);

  T setCompression(String compressionMethod);

  T setTimeout(Duration timeout);

  T setTrustedCertificates(byte[] trustedCertificatesPem);

  T setClientTls(byte[] privateKeyPem, byte[] certificatePem);

  T setRetryPolicy(@Nullable RetryPolicy retryPolicy);

  T setMemoryMode(MemoryMode memoryMode);
}
