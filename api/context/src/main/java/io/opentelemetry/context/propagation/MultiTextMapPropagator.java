/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.context.propagation;

import io.opentelemetry.context.Context;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;

final class MultiTextMapPropagator implements TextMapPropagator {
  private final TextMapPropagator[] textPropagators;
  private final Collection<String> allFields;
  private final boolean stopExtractAfterFirst;
  private final boolean iterateBackwards;

  /** Returns a {@link MultiTextMapPropagator} for the given {@code propagators}. */
  public static TextMapPropagator create(TextMapPropagator... propagators) {
    return builder(propagators).build();
  }

  /** Returns a {@link MultiTextMapPropagator} for the given {@code propagators}. */
  public static TextMapPropagator create(Iterable<TextMapPropagator> propagators) {
    return builder(propagators).build();
  }

  /**
   * Creates a {@link MultiTextMapPropagator} instance that will iterate in reverse order and will
   * stop extracts when the first delegate updates the context.
   */
  public static TextMapPropagator createBackwards(TextMapPropagator... propagators) {
    return builder(propagators).stopExtractAfterFirst().iterateBackwards().build();
  }

  /**
   * Creates a new Builder instance to help building a new {@link MultiTextMapPropagator}.
   *
   * @param propagators delegates
   */
  public static TextMapPropagatorBuilder builder(TextMapPropagator... propagators) {
    return builder(Arrays.asList(propagators));
  }

  /**
   * Creates a new Builder instance to help building a new {@link MultiTextMapPropagator}.
   *
   * @param propagators delegates
   */
  public static TextMapPropagatorBuilder builder(Iterable<TextMapPropagator> propagators) {
    return new TextMapPropagatorBuilder(propagators);
  }

  MultiTextMapPropagator(TextMapPropagatorBuilder builder) {
    this.textPropagators = new TextMapPropagator[builder.delegates.size()];
    builder.delegates.toArray(this.textPropagators);
    this.allFields = Collections.unmodifiableCollection(builder.allFields);
    this.iterateBackwards = builder.iterateBackwards;
    this.stopExtractAfterFirst = builder.stopExtractAfterFirst;
  }

  @Override
  public Collection<String> fields() {
    return allFields;
  }

  /** Delegates injection to all textPropagators, in order. */
  @Override
  public <C> void inject(Context context, @Nullable C carrier, Setter<C> setter) {
    for (TextMapPropagator textPropagator : textPropagators) {
      textPropagator.inject(context, carrier, setter);
    }
  }

  /**
   * Extracts the value from upstream invoking all the registered propagators. It will iterate
   * forwards or backwards, depending on the iterateBackwards flag. If stopExtractAfterFirst is
   * true, then extraction will be halted after the first delegate updates the context.
   *
   * @param context the {@code Context} used to store the extracted value.
   * @param carrier holds propagation fields. For example, an outgoing message or http request.
   * @param getter invoked for each propagation key to get.
   * @param <C> carrier of propagation fields, such as an http request.
   * @return the {@code Context} containing the extracted value.
   */
  @Override
  public <C> Context extract(Context context, @Nullable C carrier, Getter<C> getter) {
    int start = iterateBackwards ? textPropagators.length - 1 : 0;
    int inc = iterateBackwards ? -1 : 1;
    int term = iterateBackwards ? -1 : textPropagators.length;
    for (int i = start; i != term; i += inc) {
      Context updatedContext = textPropagators[i].extract(context, carrier, getter);
      if (stopExtractAfterFirst && (updatedContext != context)) {
        return updatedContext;
      }
      context = updatedContext;
    }
    return context;
  }
}
