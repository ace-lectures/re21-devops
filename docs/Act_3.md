## Act III: Containerizing the application as a REST service

## Navigation

  - [Go to summary](../README.md)
  - [Go to previous act](./Act_2.md)


## Step III.1: Load Jooby

Jooby is a super-light REST environment for Java. 

To load it into our project, we add the following dependencies into the `pom.xml` file:

```xml
    <dependency>
      <groupId>io.jooby</groupId>
      <artifactId>jooby-netty</artifactId>
      <version>2.11.0</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.6</version>
    </dependency>
```

If you want to have less verbose logs, consider copy-pasting the [`logback.xml`](./../src/main/resources/logback.xml) file into the `src/main/java/resources` directory.

## Step III.2: Make the system "presentable"

We need to improve our `Order` class to make it more presentable. 

We basically add two instance variable for `owner` and `recipient`, as well as `toString` methods for drinks and orders.

```java
private String owner;
public void setOwner(String who) { this.owner = who; }

private String recipient;
public void setRecipient(String who) { this.recipient = who; }

@Override
public String toString() {
    return "Order: " + owner + " / " + recipient + " / { " + contents + "}";
}

static class Drink {
    // ...
    @Override public String toString() { return name; }
}
```

## Step III.3: Wrap the system into a  service

We create a pretty simple and naive REST service, that will declare three routes:

  - `GET /`: say hello (e.g., homepage of the tool)
  - `GET /orders`: list ongoing orders
  - `GET /orders/{owner}/{recipient}/{drink}`: add an order to the list

> **Warning: the last route must not be a `GET` in an ideal world, but a `POST`. However, for the sake of demonstration simplicity, we decided to use a `GET` and only rely on plain browser features.**

We create a class named `Service`, with the following contents to declare the three routes and map the routes to method executions.

```java
import io.jooby.Jooby;
import java.util.LinkedList;
import java.util.List;

public class Service extends Jooby {

    public static void main(String[] args) { runApp(args, Service::new); }

    {
        get("/", ctx -> "Welcome to our drink ordering system");
        get("/orders", ctx -> getAllOrders() );
        get("/orders/{owner}/{recipient}/{drink}", ctx -> {
            Order o = addOrder(ctx.path("owner").value(),
                               ctx.path("recipient").value(),
                               ctx.path("drink").value());
            return "added " + o;
        });
    }

    private final List<Order> orders = new LinkedList<>();

    public String getAllOrders() { 
        return ""; 
    }

    public Order addOrder(String owner, String recipient, String drinkName) {
        return null;
    }    
}
```

This code :

  - Declare the entry point of the system, by running the `Service` class as a Jooby service (`main` method);
  - Using a _static initialisation block_, declare the three `get` routes;
    - The routes point to empty methods (`getAllOrders` and `addOrder`).
  - Declare the list that will store the orders;
  
We can now focus on _wrapping_ our pre-existing business logic with the service methods:

```java
public String getAllOrders() {
    if(orders.isEmpty())
        return "Nothing to show";
    return orders.stream()
                 .map(Order::toString)
                 .reduce("",(s1,s2) -> s1 +"\n" + s2);
}

public Order addOrder(String owner, String recipient, String drinkName) {
    Order o = new Order();
    o.setOwner(owner);
    o.setRecipient(recipient);
    o.getDrinks().add(new Order.Drink(drinkName));
    orders.add(o);
    return o;
}
```

## Step III.4: Run the service locally

To run the service, we have to first compile it. We already know that our tests are OK, there is no need to spend time on these tests, so to reduce the compilation time we use the `-DskipTests` flag.

```
mosser@loki re21-devops % mvn clean package -DskipTests
```

To execute the system as a service, we simply ask maven to execute the `Service` class

```
mosser@loki re21-devops % mvn -q exec:java -Dexec.mainClass=Service
[2021-09-19 17:58:24,767]-[Service.main()] INFO  Service - Service started with:
[2021-09-19 17:58:24,769]-[Service.main()] INFO  Service -     PID: 33383
[2021-09-19 17:58:24,769]-[Service.main()] INFO  Service -     netty {port: 8080, ioThreads: 16, workerThreads: 64, bufferSize: 16384, maxRequestSize: 10485760}
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service -     env: [dev]
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service -     execution mode: default
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service -     user: mosser
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service -     app dir: /Users/mosser/work/re21-devops
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service -     tmp dir: /Users/mosser/work/re21-devops/tmp
[2021-09-19 17:58:24,770]-[Service.main()] INFO  Service - routes: 

  GET /
  GET /orders
  GET /orders/{owner}/{recipient}/{drink}

listening on:
  http://localhost:8080/
```

We can now visit the different routes using our favorite browser to add orders and then list them:

  1. [http://localhost:8080/](http://localhost:8080/):  display the hello message;
  2. [http://localhost:8080/orders](http://localhost:8080/orders): says that there is no order to display;
  3. [http://localhost:8080/orders/seb/jmb/coke](http://localhost:8080/orders/seb/jmb/coke): Create an order
  4. [http://localhost:8080/orders](http://localhost:8080/orders): display the previously created order.


## Step III.5: Create an executable service

The service is still defined as a Maven artefacts, and cannot run outside of maven. We then need to create a turn-key Java application that will include all the dependencies, so that the system cann be executed outside of Maven, with any java environment.

We simply ask Maven to squash all the dependencies into a single jar (an operation called _shading_), and to consider the `Service` class as the main one. 

To do this, we add the following declaration in the `pom.xml`, in the `build` part.

```xml
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.2.4</version>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
          <configuration>
            <finalName>${project.artifactId}-SHADED</finalName>
            <transformers>
              <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>Service</mainClass>
              </transformer>
            </transformers>
          </configuration>
        </execution>
      </executions>
    </plugin>
</build>
```
To create the `jar` containing all the dependencies into a standalone application, we simply `package` our code:

```
mosser@loki re21-devops % mvn clean package
```

It creates a file named `re-21-SHADED.jar` in the `target` directory, in addition to the regular `re-21-1.0-SNAPSHOT.jar` one. By the size difference between the two files, it is clear that the _shaded_ version contains more contents (_i.e._, the microservice framework and the HTTP server)

```
mosser@loki re21-devops % ls -lh target/*.jar
-rw-r--r--  1 mosser  staff   8.0K 21 Sep 11:16 target/re-21-1.0-SNAPSHOT.jar
-rw-r--r--  1 mosser  staff   4.9M 21 Sep 11:16 target/re-21-SHADED.jar
```

To run the application outside of Maven, we execute it like any Java application, and hit CTRL-C to stop it:

```
mosser@loki re21-devops % cd target/ 
mosser@loki target % java -jar re-21-SHADED.jar 
[2021-09-21 11:21:12,083]-[main] INFO  Service - Service started with:
[2021-09-21 11:21:12,085]-[main] INFO  Service -     PID: 74003
[2021-09-21 11:21:12,085]-[main] INFO  Service -     netty {port: 8080, ioThreads: 16, workerThreads: 64, bufferSize: 16384, maxRequestSize: 10485760}
[2021-09-21 11:21:12,085]-[main] INFO  Service -     env: [dev]
[2021-09-21 11:21:12,085]-[main] INFO  Service -     execution mode: default
[2021-09-21 11:21:12,085]-[main] INFO  Service -     user: mosser
[2021-09-21 11:21:12,085]-[main] INFO  Service -     app dir: /Users/mosser/work/re21-devops/target
[2021-09-21 11:21:12,085]-[main] INFO  Service -     tmp dir: /Users/mosser/work/re21-devops/target/tmp
[2021-09-21 11:21:12,086]-[main] INFO  Service - routes: 

  GET /
  GET /orders
  GET /orders/{owner}/{recipient}/{drink}

listening on:
  http://localhost:8080/

^C[2021-09-21 11:21:25,552]-[Thread-0] INFO  Service - Stopped Service
mosser@loki target % 
```

## Step III.6: Wrap the webservice into a container

From a _dev_ point of view, our drink ordering system is now wrapped as a turn-key service. However, it requires the _ops_ to operate a Java-based stack to support its deployment. Our code is fragile with respect to the java version, _e.g._, will not work if a JRE older than the 16 one is deployed.

To take care of this, we need to _freeze_ the environment. Instead of delivering an application, we are going to deliver an _image of the application_, that will contain the app, as well as the associated technological stack. Then, deploying the application will be equivalent to start a container by instantiating the image.

We define this image by creating a file named `Dockerfile`. The file states that:

  - We are building our image on top of the `openjdk:16-alpine` one (the lightest image providing Java 16)
  - We are working in a directory named `/app`
  - We copy inside the image the shaded application we've just created
  - When asked to start, the container will execute the `java -jar re-21-SHADED.jar` command (`CMD`)

```dockerfile
# syntax=docker/dockerfile:1
FROM openjdk:16-alpine
WORKDIR /app
COPY target/re-21-SHADED.jar .
CMD ["java", "-jar", "re-21-SHADED.jar"]
```

To build the image, we ask docker to `build` it and to name it (replace `USERNAME` by your docker hub account):

```
mosser@loki re21-devops % docker build -t USERNAME/re21 .
[+] Building 13.8s (12/12) FINISHED                                                                                        
...
 => [1/3] FROM docker.io/library/openjdk:16-alpine@sha256:49d822f4fa4deb5f9d0201ffeec9f4d113bcb4e7e49bd6bc063d3ba93aacbcae 
...
 => [2/3] WORKDIR /app                                                                                                     
...
 => [3/3] COPY target/re-21-SHADED.jar .                                                                                   
...
Use 'docker scan' to run Snyk tests against images to find vulnerabilities and learn how to fix them
mosser@loki re21-devops % 
```

By default, a container is isolated from the host computer. Thus, we nneed to bind our local ports to the container's one so that we cann access to the web application from the host. This is done using a _port binding_ (`-p`).

To start a container running the image, we use the following command:

```
mosser@loki re21-devops % docker run --rm -d -p 9090:8080 --name re21-app USERNAME/re21
```

The command states that:
  - we want to _remove_ (`--rm`) the container after its execution;
  - we want it to run in _detached_ (`-d`) mode, acting like a server;
  - we want to bind the host port `9090` to the one used inside the container (`-p 9090:8080`);
  - we want to name the application `re21-app`;
  - we use the `USERNAME/re21` image to instantiate the container.


You can now access the app going to [http://localhost:9090](http://localhost:9090). 

To stop the container:

```
mosser@loki re21-devops % docker stop re21-app
```

## Step III.7: Releasing the image

Docker provides a public registry to host images. Thus, people can _push_ images to the registry, and other people can _pull_ then to execute them in their own context.

We first need to log into the Docker Hub:

```
mosser@loki re21-devops % docker login
Login with your Docker ID to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com to create one.
Username: USERNAME
Password: << ENTER YOUR PASSWORD WHEN PROMPTED TO
Login Succeeded
```

We cann now _push_ the image to the hub:

```
mosser@loki re21-devops % docker push USERNAME/re21
Using default tag: latest
The push refers to repository [docker.io/USERNAME/re21]
22b316dfb79b: Pushed 
9d942cd875a9: Pushed 
6205925ca10a: Layer already exists 
627070616b39: Layer already exists 
1119ff37d4a9: Layer already exists 
latest: digest: sha256:46f3fe601214ed9a224e6070ef8efd17165194476d1d2db9b156ab9a818e0607 size: 1369
```

A docker image is made of layers (approximately one per instruction in the Dockerfile). Docker reused existing layers (_e.g._, the one provided by the `openjdk:16-alpine), and pushed the one that are specific to our image.

## Step III.8: Reusing the image

Now, anyone with access to the docker hub can download the application and start it.

First, start by deleting all your Docker artefact on your local computer to wipe out the image we've just build:

```
mosser@loki re21-devops % docker system prune --all -f    
Deleted Images:
untagged: USERNAME/re21:latest
deleted: sha256:870b65bd4cf2cc04266d05169a93f86715b69b22c0650ac9186d8bdf73c2df92

Deleted build cache objects:
fvkcrf1vvw6u66im5bc4qf606
1bphc31el6qguw73d46kmgzrd
gnml98qv8t2izuo3jvmgqjjm8
jcmdxsohgux3836kle95r3cbu
x8ib9dthbdargr39enihs2who
y23xefsijwdossqsqit35mbiy
z1gqvhcikmv5dsd3b78se52ii
pk1i63tklpp6ricophhipqyhy
zpb2w48l31oa5a4jqkur43klo

Total reclaimed space: 10.37MB
```

We also log out from the Docker Hub:

```
mosser@loki re21-devops % docker logout
Removing login credentials for https://index.docker.io/v1/
```

What happens if we try to start the app by instantiating a container?

```
mosser@loki re21-devops % docker run --rm -d -p 9090:8080 --name re21-app USERNAME/re21
Unable to find image 'USERNAME/re21:latest' locally
latest: Pulling from USERNAME/re21
4c0d98bf9879: Pull complete 
6f1834e342ac: Pull complete 
78f4563ac5cf: Pull complete 
71a5c816ab4c: Pull complete 
7b084e5abbbe: Pull complete 
Digest: sha256:46f3fe601214ed9a224e6070ef8efd17165194476d1d2db9b156ab9a818e0607
Status: Downloaded newer image for USERNAME/re21:latest
faad6116c89ae533d53d37a3789273613e3d2c62f12a3897dd3d9e6c84730484
```

As docker cannot find the image locally, it connects to the hub and pulls all the layers. When the layers are pulled, it starts a container using the freshly downloaded image.

  - [Go to next act](./Act_4.md)