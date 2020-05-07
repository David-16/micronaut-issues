package micronaut.tracing.issue;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpHeaders;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.reactivex.Single;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Controller("/v1/serviceA")
public class ServiceA {
    @Inject
    @Client("http://localhost:8080")
    private RxHttpClient client;

    @Inject
    private Tracer tracer;

    @Get(produces = MediaType.TEXT_PLAIN)
    public Single<Integer> foo() {
        Assertions.assertTrue(tracer.activeSpan().toString().endsWith("GET /v1/serviceA"), tracer.activeSpan().toString());

        return Single.fromCallable(() -> Integer.MAX_VALUE)
            .doOnSuccess(x -> System.out.println(tracer.activeSpan()))
            .flatMap(x -> client.retrieve(HttpRequest.GET("/v1/serviceB/"), Integer.class).singleOrError())
            .doOnSuccess(x -> System.out.println(tracer.activeSpan()))
            .flatMap(x -> client.retrieve(HttpRequest.GET("/v1/serviceC/"), Integer.class).singleOrError())
            .doOnSuccess(x -> System.out.println(tracer.activeSpan()));
    }
}
