package micronaut.tracing.issue;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;
import java.util.Random;


@Controller("/v1/serviceB")
public class ServiceB {
    @Inject
    private Tracer tracer;

    private final Random random = new Random();

    @Get(produces = MediaType.TEXT_PLAIN)
    int foo(final HttpRequest<?> httpRequest) {
        Assertions.assertTrue(tracer.activeSpan().toString().endsWith("GET /v1/serviceB"), tracer.activeSpan().toString());
        return random.nextInt();
    }
}
