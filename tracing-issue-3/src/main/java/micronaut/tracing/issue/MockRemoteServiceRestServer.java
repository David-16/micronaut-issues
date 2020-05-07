package micronaut.tracing.issue;

import io.micronaut.http.annotation.Controller;
import io.opentracing.Tracer;
import io.reactivex.Single;

import javax.inject.Inject;


@Controller(RemoteServiceRestApi.BASE_URL)
public class MockRemoteServiceRestServer implements RemoteServiceRestApi {
    @Inject
    private Tracer tracer;

    public Single<String> reactive() {
        return Single.just(nonReactive());
    }

    public String nonReactive() {
        return "" + Math.random();
    }
}
