Feature: Ordering drinks

  Scenario: Creating an empty order
    Given Romeo who wants to create an Order
    When Juliette is declared as recipient
    Then the order does not contain any drinks

  Scenario: Adding a drink to an order
    Given Tom who wants to create an Order
    When Jerry is declared as recipient
    And a "PepsaCoke Zero" is added to the order
    Then the order contains 1 drink

  Scenario: Checking the contents of an order
    Given Seb who wants to create an Order
    When Jean-Michel is declared as recipient
      And a "PepsaCoke Zero" is added to the order
      And a "DietCola Max" is added to the order
      And another "PepsaCoke Zero" is added to the order
    Then the order contains 3 drinks
      And the order contains 2 "PepsaCoke Zero"
      And the order contains 1 "DietCola Max"

