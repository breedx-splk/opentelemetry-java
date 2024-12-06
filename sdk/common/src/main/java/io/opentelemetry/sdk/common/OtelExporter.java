/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.common;

import java.util.Collection;

public interface OtelExporter<T> {

  CompletableResultCode export(Collection<T> data);

  CompletableResultCode flush();

  CompletableResultCode shutdown();
}
