# Act II: Creating Acceptance Scenarios

## Navigation

  - [Go to summary](../README.md)
  - [Go to previous act](./Act_1.md)

## Objective

We focus here on the three following user stories:

- _As_ Romeo, _I want to_ create an empty order for Juliet _so that_ I can add drinks later.
- _As_ Romeo, _I want to_ add a drink into an order  _so that_ I can drink it.
- _As_ Romeo, _I want to_ pay for an order _so that_ I'm not going to jail.


## Step II.1: Write a small unit tests

First, we need to load the Junit dependency. Junit is the reference framework for unit testing in Java. We load it by adding a new dependency in the `pom.xml` file:

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

We now consider the first story, and create a test to support it. 

> _As_ Romeo, _I want to_ create an empty order for Juliet _so that_ I can add drinks later.

The test will check that, when an order is created by Romeo for Juliet, it is empty by default. Create a class named `OrderUnitTest` in the `src/test/java` directory:

```java
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class OrderUnitTest {
    
    @Test public void empty_order_by_default(){
        Order o = new Order();
        o.setOwner("Romeo"); 
        o.setRecipient("Juliet");
        List<Order.Drink> drinks = o.getDrinks();
        assertEquals(0, drinks.size());
    }
    
}
```

## Step II.2: Use the test to infer the architecture

We need two supporting classes to support this test: `Order` (with three methods: `setOwner`, `setRecipient` and `getDrinks`), and `Drink`. This should not be a surprise, as our objective is to define a piece of software to order drinks.

We are not interested (for now) by the implementation of the system, as we eant the architecture to emerge from the tests. **We made the choice here to declare `Drink` as an internal class, to minimize the number of files we're interacting with**.

Create a class named `Order` in `src/main/java`:

```java
import java.util.List;
import java.util.LinkedList;

public class Order {

    public void setOwner(String who) { /* ...*/ }
    public void setRecipient(String who) { /* ...*/ }
    public List<Order.Drink> getDrinks() { return new LinkedList<>(); }

    static class Drink { 
        public Drink(String name){ }  
    }
    
}
```

To run the test on top of this code:

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

## Step II.3: Load _Cucumber_

Cucumber is the acceptance testing framework used in this tutorial. To load it, we will add into our _Project Object Model_ the dependencies referring to Cucumber in Java, as well as the bridge used to plug cucumber into the JUnit logic.

```xml
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>6.11.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
         <groupId>io.cucumber</groupId>
         <artifactId>cucumber-junit</artifactId>
         <version>6.11.0</version>
         <scope>test</scope>
    </dependency>
```

## Step II.4: Activate cucumber for the project

To date, only JUnit tests are executed. To integrate acceptance scenarios inside the system, we need to activate the _cucumber_ framework in the project. We'll catch the Junit train and create a bridge (named `RunCucumberTest`) to lure JUnit into executing our acceptance scenarios in addtion to classical unit testing.

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

To remove a very annoying (advertisement) message from Cucumber, create a file named `cucumber.properties` in the `src/test/resources` (create it if it does not already exist) with the following contents:

```properties
cucumber.publish.quiet=true
```

### Step II.5: Migrate the Unit test to an Acceptance Scenario (Story #1)

Create a `src/test/resources` folder, and a file named `ordering.feature`. This file describes our acceptance scenario for the story under consideration here, containing three steps expressed using the Gherkin language:

```gherkin
Feature: Ordering drinks

  Scenario: Creating an empty order
    Given Romeo who wants to create an Order
    When Juliet is declared as recipient
    Then the order does not contain any drinks
```

We now need to create the code that support each step. Create a class named `StepDefinitions` in `src/test/Java`, with the following contents:

```java
import io.cucumber.java.en.*;
import java.util.List;
import static org.junit.Assert.*;

public class StepDefinitions {

    private Order o;

    @Given("Romeo who wants to create an Order")
    public void creating_an_order(){ throw new PendingException(); }

    @When("Juliet is declared as recipient")
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

Without surprise, as there is no code to make a link between our high-level acceptance scenario and the real code, we do not have any meaningful results. Let us map the formerly defined unit test into the different steps:

```java
@Given("Romeo who wants to create an Order")
public void creating_an_order() {
    o = new Order(); 
    o.setOwner("Romeo");
}

@When("Juliet is declared as recipient")
public void declaring_recipient(){ 
  o.setRecipient("Juliet");  
}

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

### Step II.6: Use parameters to make scenarios more expressive (Story #2)

> _As_ Romeo, _I want to_ add a drink into an order  _so that_ I can drink it.


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

@Then("the order contains {int} drink")
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
private List<Drink> contents = new LinkedList<>();
public List<Order.Drink> getDrinks() { return contents; }    
```

**Warning: this design is terrible in terms of object-orientation (public accessors, information leak, external modification of private contents, ...). We're only focusing here on how to express acceptance scenarios!**

### Step II.7: Make the scenario more precise (Story #2 updated)

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

First, we need to improve the `Drink` class to make the drink's name more useful:

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

Finally, we can create the last assertion checking, counting how many drinks of a given kind are available:

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
### Step II.8: Use mocks to support missing parts (Story #3)

> _As_ Romeo, _I want to_ pay for an order _so that_ I'm not going to jail.

We now consider a database used to store the price of each drink. For testing purpose, we do not need to use a _real_ database, we only need a _dummy_ one, answering what we told it to answer. 

This is the precise rationale of _mock_ objects.

### A) Scenario to support

Then, we add the following scenario to check the price of a given order:

```gherkin
Scenario: Paying the price
  Given Céline who wants to create an Order
    And the price of a "PepsaCoke Zero" being 2.75 dollars
    And the price of a "DietCola Max" being 2.55 dollars
    And taxes in Quebec being 15%
  When René is declared as recipient
    And a "PepsaCoke Zero" is added to the order
    And a "DietCola Max" is added to the order
  Then the price without taxes is 5.30 dollars
    And the price including taxes is 6.10 dollars
```

### B) Computing the price with and without taxes

First, we create the interface of this price database, as a `Catalogue` interface in `src/main/java`:

```java
public interface Catalogue {
    Double getPrice(String drinkName);
}
```

We add three missing methods in the `Order` class: remembering the tax rate, and computing prices with and without taxes.

```java
public void setTaxes(double rate) { this.taxes = rate; }

public double computePrice(Catalogue catalogue){
    return this.getDrinks().stream()
               .map(d -> catalogue.getPrice(d.getName()))
               .reduce(0.0, Double::sum);
}

public double computePriceWithTaxes(Catalogue catalogue){
    return new BigDecimal(this.computePrice(catalogue) * taxes)
            .setScale(2, RoundingMode.HALF_EVEN)
            .doubleValue();
}
```

And we add the following steps to match our new scenario lines:

```java
@Given("taxes in {word} being {double}%")
public void taxes_being(String place, double rate) {
   o.setTaxes(1 + rate/100);
}

@Then("the price without taxes is {double} dollars")
public void the_price_without_taxes_is_$(Double expected) {
    assertEquals(expected, o.computePrice(catalogue), 0.01);
}

@Then("the price including taxes is {double} dollars")
public void the_price_including_taxes_is_$(Double expected) {
    assertEquals(expected, o.computePriceWithTaxes(catalogue), 0.01);
}
```

**Remarks**:

  - The first argument for the tax declaration step (the location) is not used yet. This is a simple syntactic sugar provided to the acceptance scenario writer to make the scenario more readable.
  - We need to use `dollars` instead of the `$` sign because `$` is a reserved keyword in Gherkin (as it is a reserved keyword in regular expressions, matching the end of the line)

### C) Controlling the price database inside the test

Now we load _Mockito_, a reference mock framework for Java, by declaring a new dependency in the `pom.xml` file

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>3.12.4</version>
    <scope>test</scope>
</dependency>
```

And we improve the step definitions to support our new steps related to price and taxes:

```java
private Catalogue catalogue = mock(Catalogue.class);

@Given("the price of a {string} being {double} dollars")
public void the_price_of_a_being_$(String drink, Double price) {
    when(catalogue.getPrice(drink)).thenReturn(price);
}
```

The mockito fluent API style helps to understand what is happening:

  - the `catalogue` object is created as a _mock_ of the `Catalogue` interface
  - when the method `getPrice` is called on the mock with `drink` as a parameter, then the mock returns `price` as output.

### Step III.9: Refactoring the scenarios

The scenarios are considering couples like Céline and René, Tom and Jerry, ... But actually who is ordering for who does not matter for these scenarios. Thus, we can _factorize_ these steps into a common _Background_:

```gherkin
Background:
    Given Seb who wants to create an Order
    When Jean-Michel is declared as recipient
```

And then remove the unnecessary context initialisation in all the other steps.

### Step III.10: Tagging the scenarios

We now have scenarios related to two different part of the _Ordering feature: _(i)_ ordering by itself, and _(ii)_ paying for the order.

Cucumber supports _tags_, as arbitrary string labels associated to `Scenario`s or `Feature`s. This we can tag our acceptance scenario with the two following tags: `@payment` and `@ordering`

```gherkin
Feature: Ordering drinks
  # ...

  @ordering
  Scenario: Creating an empty order
    # ...

  @ordering
  Scenario: Adding a drink to an order
    # ...

  @ordering
  Scenario: Checking the contents of an order
    # ...

  @payment
  Scenario: Paying the price
    # ...
```

To execute only the subset of scenarios related to one given tag:

```
mosser@loki re21-devops % mvn -q test -Dcucumber.filter.tags="@payment"

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running OrderUnitTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.058 sec
Running RunCucumberTest

@payment
Scenario: Paying the price                                 # odering.feature:26
  Given Seb who wants to create an Order                   # StepDefinitions.creating_an_order(java.lang.String)
  When Jean-Michel is declared as recipient                # StepDefinitions.declaring_recipient(java.lang.String)
  Given the price of a "PepsaCoke Zero" being 2.75 dollars # StepDefinitions.the_price_of_a_being_$(java.lang.String,java.lang.Double)
  And the price of a "DietCola Max" being 2.55 dollars     # StepDefinitions.the_price_of_a_being_$(java.lang.String,java.lang.Double)
  And taxes in Quebec being 15%                            # StepDefinitions.taxes_in_quebec_being(java.lang.String,double)
  When a "PepsaCoke Zero" is added to the order            # StepDefinitions.add_drink_to_the_order(java.lang.String)
  And a "DietCola Max" is added to the order               # StepDefinitions.add_drink_to_the_order(java.lang.String)
  Then the price with taxes is 5.30 dollars                # StepDefinitions.the_price_with_taxes_is_$(java.lang.Double)
  And the price including taxes is 6.10 dollars            # StepDefinitions.the_price_including_taxes_is_$(java.lang.Double)
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.795 sec

Results :

Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

  - [Go to next act](./Act_3.md)
