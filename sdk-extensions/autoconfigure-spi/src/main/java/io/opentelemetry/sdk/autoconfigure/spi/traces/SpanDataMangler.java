package io.opentelemetry.sdk.autoconfigure.spi.traces;

import io.opentelemetry.sdk.autoconfigure.spi.Ordered;
import io.opentelemetry.sdk.trace.data.SpanData;

// spi
public interface SpanDataMangler extends Ordered {

  SpanData apply(SpanData spanData);
}
