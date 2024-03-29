**NOTE: THIS PROJECT IS DEPRECATED.** This projects is no longer maintained.  It is deprecated for
[dropwizard-bundles maintained fork](https://github.com/dropwizard-bundles/dropwizard-redirect-bundle).
Users of this project should update their project dependencies appropriately.

Getting Started
===============

Just add this maven dependency:
```xml
<dependency>
  <groupId>com.bazaarvoice.dropwizard</groupId>
  <artifactId>dropwizard-redirect-bundle</artifactId>
  <version>0.4.0</version>
</dependency>
```

- For Dropwizard 0.6.2: use version < 0.3.0
- For Dropwizard 0.7.0: use version >= 0.3.0

To redirect one path to another path:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new PathRedirect("/old", "/new")
    ));
  }

  // ...
}
```

To redirect many paths at once:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new PathRedirect(ImmutableMap.<String, String>builder()
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

For more advanced users, there is also a regular expression based redirector that has access to the full URI.  This
operates in a similar fashion to the mod-rewrite module for Apache:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
      new UriRedirect("(.*)/welcome.html$", "$1/index.html")
    ));
  }

  // ...
}
```

If you have to combine http to https redirect and path redirect at the same time, then you want to do the following:
```java
public class MyApplication extends Application<...> {
  // ...

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new RedirectBundle(
        bootstrap.addBundle(new RedirectBundle(new PathRedirect("/", "docs"), new HttpsRedirect()));
    ));
  }

  // ...
}
```
