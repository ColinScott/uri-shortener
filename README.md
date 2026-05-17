# URI Shortener

## Running

From the project root directory run:

```shell
./gradlew bootRun
```

## Profiles

The project supports two profiles. These control the data store used by the application. The default `jpa` uses a JPA repository which is configured to use an in-memory H2 database. There is also an `inmemory` profile which uses a `HashMap`.

This configuration can be changed in [application.properties](./src/main/resources/application.properties).

## Invoking

### Shortening a URI

URIs can be shortened via a POST, such as with the `curl` command below:

```shell
curl -X POST --location "http://localhost:8080/shortened" \
    -H "Content-Type: application/json" \
    -d '{ "uri": "https://www.originenergy.com.au/electricity-gas/plans.html" }' \
    -i
```

Replace the server location and URI to be shortened as appropriate.

The shortened URL will be returned in the `Location` header, for example:

```text
HTTP/1.1 201
Location: http://localhost:8080/iuy1JHLv
Content-Type: text/plain;charset=UTF-8
Content-Length: 10
Date: Sun, 17 May 2026 12:30:40 GMT

No Content%
```

### Redirecting

Opening the shortened URI from the above in a browser will result in a redirect. Alternately the URI can be obtained with `curl` as follows:

```shell
curl -i http://localhost:8080/DU7Igz46
```

Substitute the shortened URI recieved from a shortening request.

The full URL will be returned in the `Location` header, for example:

```text
HTTP/1.1 307
Location: https://www.originenergy.com.au/electricity-gas/plans.html
Content-Type: text/plain;charset=UTF-8
Content-Length: 8
Date: Sun, 17 May 2026 12:33:43 GMT

Redirect%
```

### Getting details

The shortened URI details can be obtained as JSON from the GET `/shortened/<key>` endpoint. Using `curl` for the key above this would be:

```shell
curl -i http://localhost:8080/shortened/DU7Igz46
```

Example response:

```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 17 May 2026 22:11:03 GMT

{"uri":"https://www.originenergy.com.au/electricity-gas/plans.html"}%
```

## Running Tests

```shell
./gradlew test
```