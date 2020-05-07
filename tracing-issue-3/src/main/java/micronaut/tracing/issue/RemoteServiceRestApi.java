package micronaut.tracing.issue;

import io.micronaut.http.annotation.Get;
import io.reactivex.Single;


public interface RemoteServiceRestApi {
    String BASE_URL = "/v1/serviceB";

    @Get("/reactive")
    Single<String> reactive();

    @Get("/non-reactive")
    String nonReactive();
}
