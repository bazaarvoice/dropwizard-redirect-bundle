Getting Started
===============

Just add this maven dependency:
```xml
<dependency>
  <groupId>com.bazaarvoice.dropwizard</groupId>
  <artifactId>dropwizard-redirect-bundle</artifactId>
  <version>0.3.0</version>
</dependency>
```

- For Dropwizard 0.6.2: use version < 0.3.0
- For Dropwizard 0.7.0: use version >= 0.3.0

To redirect one URI to another URI:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new UriRedirect("/old", "/new")
    ));
  }

  // ...
}
```

To redirect many URIs at once:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new UriRedirect(ImmutableMap.<String, String>builder()
        .put("/old1", "/new1")
        .put("/old2", "/new2")
        .build())
    ));
  }

  // ...
}
```

To redirect non-HTTPS traffic to the HTTPS port:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new HttpsRedirect()
    ));
  }

  // ...
}
```

