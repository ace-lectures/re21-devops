# Requirements Engineering in the DevOps Era

[![CI/CD Pipeline](https://github.com/ace-lectures/re21-devops/actions/workflows/pipeline.yml/badge.svg)](https://github.com/ace-lectures/re21-devops/actions/workflows/pipeline.yml)

  - Authors: 
      - Sébastien Mosser (UQAM), [mosser.sebastien@uqam.ca](mosser.sebastien@uqam.ca)
    - Jean-Michel Bruel (Université de Toulouse)
  - Version: 2021.09 (RE 2021)
  - Kata length: 120 to 180 minutes

## Objectives

  - Create a toy application in java to order drinks in a bar;
  - Express acceptance scenarios to support requirements engineering;
  - Implement a _Continous Integration_/_Continuous Deployment_ (CI/CD) pipeline

## Documents

- [IEEE 2021 International Requirements Engineering Conference proceedings]()
- [Tutorial handout](./docs/handout.pdf)

## Prerequisites

### On your local computer

  - Java 16: programming language used for this tutorial
  - Maven 3: classical build and dependencies tools for Java
  - Git: version control system for collaborative coding
  - Docker: container framework used to create turn-key applications

### On external services

  - GitHub account: [https://github.com/join](https://github.com/join)
    - to host our code and execute the CI/CD pipeline 
  - Docker Hub account: [https://hub.docker.com/signup](https://hub.docker.com/signup)
    - to store our Docker image publicly (release)
  - Heroku account: [https://signup.heroku.com/](https://signup.heroku.com/)
    - to host our running aplication (deployment) 

## Tutorial Outline

  - [Act I](./docs/Act_1.md): Building the Technical Environment
  - [Act II](./docs/Act_2.md): Creating Acceptance Scenarios
  - [Act III](./docs/Act_3.md): Containerizing the application as a REST service
  - [Act IV](./docs/Act_4.md): Continuous Integration & Deployment
  - [Act V](./docs/Act_5.md): Pipeline in Action

## Disclaimer

  - The objective of this tutorial is to focus onn acceptance scenarios & technical pipeline. Thus, we took some liberty with a "proper" object orientation of REST good practices.
  - DevOps is way much more than _acceptance testing_ and _CI/CD_. We only had today a quick overview focused on what is possible at the technical level. The other side of DevOps, more human-centered, cannot fit in a half-day workshop.
