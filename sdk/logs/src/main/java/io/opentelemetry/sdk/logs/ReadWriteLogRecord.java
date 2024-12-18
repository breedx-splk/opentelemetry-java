/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.logs;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.Value;
import io.opentelemetry.api.common.ValueType;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import javax.annotation.Nullable;

/**
 * A log record that can be read from and written to.
 *
 * @since 1.27.0
 */
public interface ReadWriteLogRecord {

  /**
   * Sets an attribute on the log record. If the log record previously contained a mapping for the
   * key, the old value is replaced by the specified value.
   *
   * <p>Note: the behavior of null values is undefined, and hence strongly discouraged.
   */
  <T> ReadWriteLogRecord setAttribute(AttributeKey<T> key, T value);

  // TODO: add additional setters

  /**
   * Sets attributes to the {@link ReadWriteLogRecord}. If the {@link ReadWriteLogRecord} previously
   * contained a mapping for any of the keys, the old values are replaced by the specified values.
   *
   * @param attributes the attributes
   * @return this.
   * @since 1.31.0
   */
  @SuppressWarnings("unchecked")
  default ReadWriteLogRecord setAllAttributes(Attributes attributes) {
    if (attributes == null || attributes.isEmpty()) {
      return this;
    }
    attributes.forEach(
        (attributeKey, value) -> this.setAttribute((AttributeKey<Object>) attributeKey, value));
    return this;
  }

  /** Return an immutable {@link LogRecordData} instance representing this log record. */
  LogRecordData toLogRecordData();

  /**
   * Returns the value of a given attribute if it exists. This is the equivalent of calling
   * getAttributes().get(key)
   */
  @Nullable
  default <T> T getAttribute(AttributeKey<T> key) {
    return toLogRecordData().getAttributes().get(key);
  }

  /** Returns the instrumentation scope that generated this log. */
  default InstrumentationScopeInfo getInstrumentationScopeInfo() {
    return toLogRecordData().getInstrumentationScopeInfo();
  }

  /** Returns the timestamp at which the log record occurred, in epoch nanos. */
  default long getTimestampEpochNanos() {
    return toLogRecordData().getTimestampEpochNanos();
  }

  /** Returns the timestamp at which the log record was observed, in epoch nanos. */
  default long getObservedTimestampEpochNanos() {
    return toLogRecordData().getTimestampEpochNanos();
  }

  /** Return the span context for this log, or {@link SpanContext#getInvalid()} if unset. */
  default SpanContext getSpanContext() {
    return toLogRecordData().getSpanContext();
  }

  /** Returns the severity for this log, or {@link Severity#UNDEFINED_SEVERITY_NUMBER} if unset. */
  default Severity getSeverity() {
    return toLogRecordData().getSeverity();
  }

  /** Returns the severity text for this log, or null if unset. */
  @Nullable
  default String getSeverityText() {
    return toLogRecordData().getSeverityText();
  }

  /**
   * Returns the body for this log, or {@link io.opentelemetry.sdk.logs.data.Body#empty()} if unset.
   *
   * <p>If the body has been set to some {@link ValueType} other than {@link ValueType#STRING}, this
   * will return a {@link io.opentelemetry.sdk.logs.data.Body} with a string representation of the
   * {@link Value}.
   *
   * @deprecated Use {@link #getBodyValue()} instead.
   */
  @Deprecated
  @SuppressWarnings("deprecation") // Implementation of deprecated method
  default io.opentelemetry.sdk.logs.data.Body getBody() {
    return toLogRecordData().getBody();
  }

  /** Returns the {@link Value} representation of the log body, of null if unset. */
  @Nullable
  default Value<?> getBodyValue() {
    return toLogRecordData().getBodyValue();
  }

  /** Returns the attributes for this log, or {@link Attributes#empty()} if unset. */
  default Attributes getAttributes() {
    return toLogRecordData().getAttributes();
  }

  /**
   * Returns the total number of attributes that were recorded on this log.
   *
   * <p>This number may be larger than the number of attributes that are attached to this log, if
   * the total number recorded was greater than the configured maximum value. See {@link
   * LogLimits#getMaxNumberOfAttributes()}.
   */
  default int getTotalAttributeCount() {
    return toLogRecordData().getTotalAttributeCount();
  }
}
