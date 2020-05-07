# micronaut-tracing-issue

## Description

This issue demonstrates an issue between Cacheable and OpenTracing thread local

A service calls a remote service but caches the result using Cacheable

Subsequent calls then have the open tracing context left over from the previous call

## Running

`JAEGER_PROPAGATION=b3 mvn clean install exec:exec`

## Testing

Run the following 4 times:

```
curl -sSv -w '\n' --noproxy '*' -H 'baggage-foo: zzzzzzzz' -H 'X-B3-TraceId: c2c5411ff5475bce' -H 'X-B3-ParentSpanId: c2c5411ff5475bce' -H 'X-B3-SpanId: d6b36e1e74d1d7f2' http://localhost:8080/v1/serviceA/reactive
```

It will return `zzzzzzzz`

Run the following 4 times:

```
curl -sSv -w '\n' --noproxy '*' -H 'baggage-foo: xxxxxxxx' -H 'X-B3-TraceId: c2c5411ff5475bce' -H 'X-B3-ParentSpanId: c2c5411ff5475bce' -H 'X-B3-SpanId: d6b36e1e74d1d7f2' http://localhost:8080/v1/serviceA/reactive
```

It will return `xxxxxxxx`

Now run the following 4 times:

```
curl -sSv -w '\n' --noproxy '*' -H 'baggage-foo: aaaaaaaa' -H 'X-B3-TraceId: c2c5411ff5475bce' -H 'X-B3-ParentSpanId: c2c5411ff5475bce' -H 'X-B3-SpanId: d6b36e1e74d1d7f2' http://localhost:8080/v1/serviceA/reactive
```

It should return `aaaaaaaa`, but it does not.  It will return `zzzzzzzz` or `xxxxxxxx` which are left overs from previous calls

## Workarounds

1. Remove `@Cacheable`
2. Remove reactive types (there are `/non-reactive` endpoints that are exactly the same except do not use `io.reactivex` types and these do not have issues)

## Hints

I debugged the issue best I could, but I am no expert with reactive types and the subscriber / publisher hell is too much for me at this time

I did, however, notice the following line in `io.opentracing.util.ThreadLocalScope` was hitting in the breakpoint:

```
if (scopeManager.tlsScope.get() != this) {
    // This shouldn't happen if users call methods in the expected order. Bail out.
    return;
}
```

Note the comment.  The fact that the breakpoint stops at this line suggests there is an unequal number of push and pops of the OpenTracing context