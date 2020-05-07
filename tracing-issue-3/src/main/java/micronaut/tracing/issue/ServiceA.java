package micronaut.tracing.issue;

import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.opentracing.Tracer;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


@Controller(ServiceA.BASE_URL)
public class ServiceA {
    public static final String BASE_URL = "/v1/serviceA";

    private static final Logger logger = LoggerFactory.getLogger(ServiceA.class);

    @Inject
    @Client("http://${micronaut.server.host}:${micronaut.server.port}")
    private RemoteServiceRestApi serviceB;

    @Inject
    private Tracer tracer;

    @Get(uri = "/reactive", produces = MediaType.TEXT_PLAIN)
    public Single<String> reactive() {
        return callExpensiveOperationOnServiceBReactively().map(this::length);
    }

    @Get(uri = "/non-reactive", produces = MediaType.TEXT_PLAIN)
    public String nonReactive() {
        return length(callExpensiveOperationOnServiceBNonReactively());
    }

    @Cacheable(cacheNames = "reactive-cache")
    public Single<String> callExpensiveOperationOnServiceBReactively() {
        return serviceB.reactive();
    }

    @Cacheable(cacheNames = "non-reactive-cache")
    public String callExpensiveOperationOnServiceBNonReactively() {
        return serviceB.nonReactive();
    }

    private String length(final String s) {
        final String baggageItem = tracer.activeSpan().getBaggageItem("foo");
        logger.info("foo = '{}'", baggageItem);
        return baggageItem;
    }
}
