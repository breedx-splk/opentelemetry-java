/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.autoconfigure.spi;

import io.opentelemetry.sdk.resources.Resource;

/**
 * A service provider interface (SPI) for providing a {@link Resource} that is merged into the
 * {@linkplain Resource#getDefault() default resource}.
 */
public interface ResourceProvider extends Ordered {

  default Resource createResource(ConfigProperties config) {
    return Resource.empty();
  }

  /**
   * Implementations that need to inspect the current state of the existing {@link Resource} <em>as
   * it is being built</em> may implement this method. Implementations are discouraged from calling
   * merge() on the existing resource.
   *
   * @param config - The auto configuration properties
   * @param existing - The current state of the resource being created
   * @return a new Resource
   */
  default Resource createResource(ConfigProperties config, Resource existing) {
    return createResource(config);
  }
}
