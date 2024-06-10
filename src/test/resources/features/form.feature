Feature: Contact form validation

  Scenario: Correct submission of the contact form using data from dummyAPI
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form with gathered data
    And I submit the form
    Then I should see a success message

  Scenario: Failure on submission gender not selected
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form without selecting gender
    And I submit the form
    Then I should see select error message

  Scenario: Failure on submission message not defined
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form without writting a message
    And I submit the form
    Then I should see select error message

  Scenario: Failure on submission due to wrong phone number format
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form with invalid phone number
    And I submit the form
    Then I should see invalid phone number message
  
  Scenario: Failure on submission due to missing name
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form without fill in the name
    And I submit the form
    Then I should see fill in all fields
  
  Scenario: Failure on submission due to wrong name format
    Given I am on the contact page
    When I retrieve all data from dummyAPI
    And I fill in the contact form with invalid name
    And I submit the form
    Then I should see special character not supported

