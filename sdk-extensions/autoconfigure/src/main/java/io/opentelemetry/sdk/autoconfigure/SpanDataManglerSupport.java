package io.opentelemetry.sdk.autoconfigure;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.traces.SpanDataMangler;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

//@AutoService(AutoConfigurationCustomizerProvider.class) ?????
public class SpanDataManglerSupport implements AutoConfigurationCustomizerProvider {

  @Override
  public void customize(AutoConfigurationCustomizer autoConfiguration) {
    BiFunction<? super SpanExporter, ConfigProperties, ? extends SpanExporter> customizer = buildCustomizer();
    if(customizer != null){
      autoConfiguration.addSpanExporterCustomizer(customizer);
    }
  }

  @Nullable
  private BiFunction<? super SpanExporter, ConfigProperties, ? extends SpanExporter> buildCustomizer() {
    ClassLoader classLoader = AutoConfiguredOpenTelemetrySdkBuilder.class.getClassLoader();
    List<SpanDataMangler> manglers = SpiUtil.loadOrdered(SpanDataMangler.class, classLoader);
    if(manglers.isEmpty()){
      return null;
    }
    Function<? super SpanData, SpanData> allManglers = buildAllMangler(manglers);

    return (delegate, configProperties) -> new SpanManglingExporter(delegate, allManglers);
  }

  private Function<? super SpanData, SpanData> buildAllMangler(List<SpanDataMangler> manglers) {
    return spanData -> {
      SpanData result = spanData;
      for (SpanDataMangler mangler : manglers) {
        result = mangler.apply(result);
      }
      return result;
    };
  }

  private static class SpanManglingExporter implements SpanExporter {
    private final SpanExporter delegate;
    private final Function<? super SpanData, SpanData> allManglers;

    public SpanManglingExporter(SpanExporter delegate, Function<? super SpanData, SpanData> allManglers) {
      this.delegate = delegate;
      this.allManglers = allManglers;
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
      return delegate.export(spans.stream().map(allManglers).collect(Collectors.toList()));
    }

    @Override
    public CompletableResultCode flush() {
      return delegate.flush();
    }

    @Override
    public CompletableResultCode shutdown() {
      return delegate.shutdown();
    }
  }
}
