# Requirements Engineering in the DevOps Era

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

## Act I: Technical environment

### Step I.1: Creating a Maven project

Create a file named `pom.xml`, to store the _Project Object Model_. This fille will describe in a declarative way what dependencies and biuld tools we are using in this tutorial. If you prefern an imperative approach, you can use _Gradle_ instead of _Maven_.

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
  http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>io.github.ace-lectures</groupId>
    <artifactId>re-21</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>RE 2021 tutorial</name>
    <packaging>jar</packaging>
    
    <properties>
        <maven.compiler.source>16</maven.compiler.source>
        <maven.compiler.target>16</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
</project>
```

The file basically states (using `properties`) that we're developping a Java 16 project with UTF-8 files.

We can also create two directories: `src/main/java` and `src/test/java`.

### Step I.2: Loading into your favorite IDE

Our project being a Maven project, a _smart_ IDE will recognize the structure and silently import it.

![Opening file into IntelliJ](./images/step_1_2_intellij.png)

### Step I.3: Create a dumb application

We are going to create an Hello World program just to check that everything is correct.

Create a file named `Main.java` in `src/main/java`, with the following contents:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to this tutorial!");
    }
}
```

To compile the program:

```
mosser@loki re21-devops % mvn clean package
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.613 s
[INFO] Finished at: 2021-09-19T12:02:00-04:00
[INFO] ------------------------------------------------------------------------
```

To execute it:

```
mosser@loki re21-devops % mvn -q exec:java -Dexec.mainClass=Main
Welcome to this tutorial!
```

We are now ready to talk about requirements engineering!

## Act II: Creating Acceptance Scenarios

We focus here on the three following user stories:

  - _As_ Romeo, _I want to_ create an empty order for Juliet _so that_ I can add drinks later.
  - _As_ Romeo, _I want to_ add a drink into an order  _so that_ I can drink it.
  - _As_ Romeo, _I want to_ pay for an order _so that_ I'm not going to jail.

### Step II.1: Load _Cucumber_

Cucumber is the acceptance testing framework used in this tutorial. To load it, we will add into our _Project Object Model_ the dependencies referring to Cucumber, as well as Junit, the reference framework for unit testing in Java (as cucumber is built on top of Junit)

```xml
<dependencies>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>6.10.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
         <groupId>io.cucumber</groupId>
         <artifactId>cucumber-junit</artifactId>
         <version>6.10.4</version>
         <scope>test</scope>
    </dependency>
    <dependency>
         <groupId>junit</groupId>
         <artifactId>junit</artifactId>
         <version>4.13.2</version>
         <scope>test</scope>
    </dependency>
```
### Step II.2: Write a simple Unit test

The test checks that, when an order is created, it is sempty by default. Create a class named `OrderUnitTest` in the `src/test/java` directory:

```java
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderUnitTest {

    @Test
    @Test public void empty_order_by_default(){
        Order o = new Order();
        o.setOwner("Romeo");
        o.setRecipient("Juliet");
        List<Order.Drink> drinks = o.getDrinks();
        assertEquals(0, drinks.size());
    }
}
```

We need two supporting classes to support this test: `Order` (with three methods: `setOwner`, `setRecipient` and `getDrinks`), and `Drink`. This should not be a surprise, as our objective is to define a piece of software to order drinks.

We are not interested (for now) by the implementation of the system, as we eant the architecture to emerge from the tests. **We made the choice here to declare `Drink` as an internal class, to minimize the number of files we're interacting with**.

Create a class named `Order` in `src/main/java`:

```java
public class Order {

    public void setOwner(String who) { /* ...*/ }
    public void setRecipient(String who) { /* ...*/ }
    public List<Order.Drink> getDrinks() { return new LinkedList<>(); }

    static class Drink { 
        public Drink(String name){ }  
    }
}
```

To run this  test:

```
mosser@loki re21-devops % mvn -q test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running OrderUnitTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.063 sec
Results :
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

### Step II.3: Activate cucumber for the project

To date, only JUnit tests are executed. To integrate acceptance scenarios insode the system, we need to activate the _cucumber_ framework in the project. We'll catch the Junit train and create a bridge (named `RunCucumberTest`) to lure JUnit into executing our acceptance scenarios in addtion to classical unit testing.

To create the bridge, create a class named `RunCucumberTest` in the `src/test/java` directory:

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"})
public class RunCucumberTest {}
```

**This class will stay as is. This is just a way to catch Junit, and asks the framework to start the cucumber process (with the `@RunWith(Cucumber.class)` annotation).**

### Step II.4: Migrate from a Unit Test to an Acceptance Scenario (Story #1)


Create a `src/test/resources` directory, and a file named `ordering.feature`. This file describe our acceptance scenario for the story under consideration here, containing three steps:

```gherkin
Feature: Ordering drinks
  Scenario: Creating an empty order
    Given Romeo who wants to create an Order
    When Juliette is declared as recipient
    Then the order does not contain any drinks
```

We now need to create the code that support each steps. Create a class named `StepDefinitions` in `src/test/Java`, with the following contents:

```java
import io.cucumber.java.en.*;
import java.util.List;
import static org.junit.Assert.*;

public class StepDefinitions {

    private Order o;

    @Given("Romeo who wants to create an Order")
    public void creating_an_order(){ throw new PendingException(); }

    @When("Juliette is declared as recipient")
    public void declaring_recipient(){ throw new PendingException(); }

    @Then("the order does not contain any drinks")
    public void check_emptyness() { throw new PendingException(); }
    }
}
```

We can now try to execute the acceptance scenario:

```
mosser@loki re21-devops % mvn -q test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running OrderUnitTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.097 sec
Running RunCucumberTest

Scenario: Creating an empty order            # odering.feature:3
  Given Romeo who wants to create an Order   # StepDefinitions.creating_an_order()
      io.cucumber.java.PendingException: TODO: implement me
	at StepDefinitions.creating_an_order(StepDefinitions.java:12)
	at ✽.Romeo who wants to create an Order(classpath:odering.feature:4)

  When Juliette is declared as recipient     # StepDefinitions.declaring_recipient()
  Then the order does not contain any drinks # StepDefinitions.check_emptyness()
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.432 sec <<< FAILURE!
Creating an empty order(Ordering drinks)  Time elapsed: 0.117 sec  <<< ERROR!
io.cucumber.java.PendingException: TODO: implement me
	at StepDefinitions.creating_an_order(StepDefinitions.java:12)
	at ✽.Romeo who wants to create an Order(classpath:odering.feature:4)

Results :
Tests in error: 
  Creating an empty order(Ordering drinks): TODO: implement me
Tests run: 2, Failures: 0, Errors: 1, Skipped: 0
```

Withour surprise, as there is no code to make a link between our high-level acceptance scenario and the real code, we do not have any meaningful results. Let us map the formerly defined unit test into the different steps:

```java
@Given("Romeo who wants to create an Order")
public void creating_an_order() {
    o = new Order(); o.setOwner("Romeo");
}

@When("Juliette is declared as recipient")
public void declaring_recipient(){ o.setRecipient("Juliet");  }

@Then("the order does not contain any drinks")
public void check_emptiness() {
    List<Order.Drink> drinks = o.getDrinks();
    assertEquals(0, drinks.size());
}
```

And execute it:

```
mosser@loki re21-devops % mvn -q test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running OrderUnitTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.056 sec
Running RunCucumberTest

Scenario: Creating an empty order            # odering.feature:3
  Given Romeo who wants to create an Order   # StepDefinitions.creating_an_order()
  When Juliette is declared as recipient     # StepDefinitions.declaring_recipient()
  Then the order does not contain any drinks # StepDefinitions.check_emptyness()
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.322 sec

Results :
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

### Step II.5: Use parameters to make scenarios more expressive (Story #2)

Add the following scenario to the `ordering.feature` file:

```gherkin
Scenario: Adding a drink to an order
  Given Tom who wants to create an Order
  When Jerry is declared as recipient
  And a "PepsaCola Zero" is added to the order
  Then the order contains 1 drink
```

First, we refactor the two initial definitions so they can accept variables names. We use the `{word}` placeholder, and cucumber will match wahtever we wrote here and transfer it to the function parameter, as a String:

```java
@Given("{word} who wants to create an Order")
public void creating_an_order(String who) {
    o = new Order();
    o.setOwner(who);
}

@When("{word} is declared as recipient")
public void declaring_recipient(String who){
    o.setRecipient(who);
}
```

Now we can enrich our steps by adding the two missing ones:

```java
@When("a {string} is added to the order")
public void add_drink_to_the_order(String drinkName){
    o.getDrinks().add(new Drink(drinkName));
}

@Then("the order contains {int} drink(s?)")
public void check_order_size(int size) {
    assertEquals(size, o.getDrinks().size());
}
```

If we try to execute the test suite now, it ends up with an error: the list is still empty!

```
mosser@loki re21-devops % mvn -q test
...
Tests run: 2, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.367 sec <<< FAILURE!
Creating an empty order #2(Ordering drinks)  Time elapsed: 0.01 sec  <<< FAILURE!
java.lang.AssertionError: expected:<1> but was:<0>
	at org.junit.Assert.fail(Assert.java:89)
	at org.junit.Assert.failNotEquals(Assert.java:835)
	at org.junit.Assert.assertEquals(Assert.java:647)
	at org.junit.Assert.assertEquals(Assert.java:633)
	at StepDefinitions.check_order_size(StepDefinitions.java:28)
	at ✽.the order contains 1 drink(classpath:odering.feature:12)
```

We can fix this by moving the list initialization in the `Order` class:

```java
public class Order {

    /* ... */ 
    private List<Drink> contents = new LinkedList<>();
    public List<Order.Drink> getDrinks() { return contents; }
    
}
```

**Warning: this design is terrible in terms of object-orientation (public accessors, information leak, external modification of private contents, ...). We're only focusing here on how to express acceptance scenarios!**

### Step III.7: Make the scenario more precise (Story #2 updated)

We now want to support the following acceptance scenario:

```gherkin
Scenario: Checking the contents of an order
  Given Seb who wants to create an Order
  When Jean-Michel is declared as recipient
    And a "PepsaCoke Zero" is added to the order
    And a "DietCola Max" is added to the order
    And another "PepsaCoke Zero" is added to the order
  Then the order contains 3 drinks
    And the order contains 2 "PepsaCoke Zero"
    And the order contains 1 "DietCola Max"
```

First, we need to improve the `Drink` class to make the drink's name more usefull:

```java
static class Drink {
    public Drink(String name){ this.name = name; }
    private String name;
    public String getName() { return name; }
}
```

Then we need to adapt the regular expressions of the existing steps to support plurals (e.g., _drink**s**_, _a**nother**_ drink)

```java
@When("a(nother?) {string} is added to the order")
public void add_drink_to_the_order(String drinkName){ /* ... */ }

@Then("the order contains {int} drink(s?)")
public void check_order_size(int size) { /* ... */ }
```

Finally we can create the last assertion checking, counting how many drinks of a given kind are available:

```java
@Then("the order contains {int} {string}")
public void check_order_contents(int size, String drink) {
    long count = o.getDrinks().stream()
                  .filter(d -> d.getName().equals(drink))
                  .count();
    assertEquals(size,count);
}
```

And run the test scenario

```
mosser@loki re21-devops % mvn -q test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------

...

Scenario: Checking the contents of an order          # odering.feature:14
  Given Seb who wants to create an Order             # StepDefinitions.creating_an_order(java.lang.String)
  When Jean-Michel is declared as recipient          # StepDefinitions.declaring_recipient(java.lang.String)
  And a "PepsaCoke Zero" is added to the order       # StepDefinitions.add_drink_to_the_order(java.lang.String)
  And a "DietCola Max" is added to the order         # StepDefinitions.add_drink_to_the_order(java.lang.String)
  And another "PepsaCoke Zero" is added to the order # StepDefinitions.add_drink_to_the_order(java.lang.String)
  Then the order contains 3 drinks                   # StepDefinitions.check_order_size(int)
  And the order contains 2 "PepsaCoke Zero"          # StepDefinitions.check_order_contents(int,java.lang.String)
  And the order contains 1 "DietCola Max"            # StepDefinitions.check_order_contents(int,java.lang.String)
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.345 sec

Results :
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```
### Step III.6: Use mocks to support missing parts (Story #3)

We now consider a database used to store the price of each drinks. For testing purpose, we do not need to use a _real_ database, we only need a _dummy_ onnnnne answering what we told it it to answer. This is the precise rationale of _mock_ objects.

First, we create 

## Act III: Containerization

### Step III.1: Wrap the system into a web service

### Step III.2: Run the service locally

### Step III.3: Wrap the webservice into a container


## Act IV: Continuous Integration & Deployment

### Step IV.1: Create a git repository

### Step IV.2: 

## Conclusions

DevOps is way much more than _acceptance testing_ and _CI/CD_. We only had today a quick overview focusued on what is possible at the technical level. The other side of DevOps, more human-centered, cannot fit in a half-day workshop.