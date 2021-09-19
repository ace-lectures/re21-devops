Feature: Ordering drinks

  Background:
    Given Seb who wants to create an Order
    When Jean-Michel is declared as recipient

  Scenario: Creating an empty order
    Then the order does not contain any drinks

  Scenario: Adding a drink to an order
    When a "PepsaCoke Zero" is added to the order
    Then the order contains 1 drink

  Scenario: Checking the contents of an order
    When a "PepsaCoke Zero" is added to the order
      And a "DietCola Max" is added to the order
      And another "PepsaCoke Zero" is added to the order
    Then the order contains 3 drinks
      And the order contains 2 "PepsaCoke Zero"
      And the order contains 1 "DietCola Max"

  Scenario: Paying the price
    Given the price of a "PepsaCoke Zero" being 2.75 dollars
      And the price of a "DietCola Max" being 2.55 dollars
      And taxes in Quebec being 15%
    When a "PepsaCoke Zero" is added to the order
      And a "DietCola Max" is added to the order
    Then the price with taxes is 5.30 dollars
      And the price including taxes is 6.10 dollars