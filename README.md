Getting Started
===============

Just add this maven dependency:
```xml
<dependency>
    <groupId>com.bazaarvoice.dropwizard</groupId>
    <artifactId>dropwizard-redirect-bundle</artifactId>
    <version>0.1.1</version>
</dependency>
```

And add your redirect bundle:
```java
public class MyService extends Service<...> {
    public static void main(String[] args) throws Exception {
        new MyService().run(args);
    }

    @Override
    public void initialize(... bootstrap) {
        bootstrap.addBundle(new RedirectBundle(ImmutableMap.<String, String>builder()
                .put("/", "/dashboard/index.html")
                .put("/index.html", "/dashboard/index.html")
                .build()));
    }

    @Override
    public void run(... configuration, Environment environment) {
        ...
    }
}
```