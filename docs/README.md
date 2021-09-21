# Requirements Engineering in the DevOps Era

[![CI/CD Pipeline](https://github.com/ace-lectures/re21-devops/actions/workflows/pipeline.yml/badge.svg)](https://github.com/ace-lectures/re21-devops/actions/workflows/pipeline.yml)

  - Authors: Sébastien Mosser (UQAM) & Jean-Michel Bruel (Université de Toulouse)
  - Version: 2021.09 (RE 2021)

## Objectives

  - Create a toy application in java to order drinks in a bar;
  - Express acceptance scenarios to support requirements engineering;
  - Implement a _Continous Integration/Continuous Deployment_ (CI/CD) pipeline

## Preliminaries

  - Java
  - Git
  - Maven
  - Docker
  - Read the slides


## Act V: Seeing the pipeline in action

Heroku will select a port for us, and tell us which one using the `PORT` environment variable.

We fist need to fix our Dockerfile to consider the port as a variation point. By default, we'll use `8080`, but one should be able to override this setting.

```dockerfile
FROM openjdk:16-alpine
ENV PORT=8080
WORKDIR /app
COPY target/re-21-SHADED.jar .
CMD ["java", "-jar", "re-21-SHADED.jar"]
```

Then, we have to fix the `Service` class to rely on the environment variable. We add the following instruction in the static initialization block

```java
setServerOptions(new ServerOptions()
                .setPort(Integer.parseInt(System.getenv("PORT"))));
```

We can now add, commit and push to trigger the pipeline.

```
mosser@loki tmp % git add -A; git commit -m "variable port"; git push 
```

the pipeline eventually deploys the app, which will be avaialbe at the following URL:

  - http://HEROKU_APP_NAME.heroku.com

## Conclusions

DevOps is way much more than _acceptance testing_ and _CI/CD_. We only had today a quick overview focusued on what is possible at the technical level. The other side of DevOps, more human-centered, cannot fit in a half-day workshop.
