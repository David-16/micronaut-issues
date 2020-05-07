# micronaut-tracing-issue

https://github.com/micronaut-projects/micronaut-core/issues/2832

## Running

`mvn clean install exec:exec`

## Testing

### Baseline with 1.2.9

`curl -sSv -w '\n' http://localhost:8080/v1/serviceA/`

This should return HTTP/200 with a payload of `1`

The micronaut stdout will display 3 lines which represent the OpenTracing Span at various points

Note that all 3 lines will be the same

### Baseline with 1.3.0

Modify `pom.xml` to change the `micronaut.version` to `1.3.0`

Restart the service and run `curl` again

The micronaut stdout will display 3 lines which represent the OpenTracing Span at various points

Note that all 3 lines will be different