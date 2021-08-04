# Fintal

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Fintal is a mobile app to manage personal finances. With Fintal you can register income and expenses, set categories to them and keep track of how and on what you spend your money in an easy way.

### App Evaluation

- **Category:** Finance
- **Mobile:** Easy to use on the go and register any type of expense/income from a mobile environment, uses camera
- **Story:** Allows users to register their finances and keep track of their income and expenses
- **Market:** Anyone that wants to have a better personal financial health, start saving or just organize their money.
- **Habit:** Users can register their incomes and expenses daily in order to get them updated to the actual money they have
- **Scope:** First version can implement a feature to allow users to upload tickets to a particual expense/income.Future versions of the app could implement more complex functions, such as adding credit or debit cards and synchronise them to automatically register transactions in your income and expenses list.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

[x] Users can login by email and password or create an account
[x] Users can register expenses
    [x] Delete or edit expenses
[x] Users can register income
    [x] Delete or edit income
[x] Users can see a global balance of their money, including total expenses and total income
[x] Users can categorize any register by some default categories (food, entertainment, home, transport, education, clothing, payments, others)
[x] Users can see a graph of their expenses by category
[x] Users can filter expenses by categories
[x] Users can add a photo of a ticket to a register

**Optional Nice-to-have Stories**
[x] Users can see their finances insights by month/year
[] Users can add, edit or delete categories
[] Users can create virtual money accounts associated with their real accounts 
    * example: Wallet, account associated with my cash money
    * example: Main account, account associated with my BBVA Bank account
[x] Users can link their real bank accounts (Only Latin American Bank Institutions)
    [x] See total balance
    [] See last transactions
    [] Check total, remaining and spent credit on credit cards
    [] See payment deadlines
[] Push notifications
    [] Daily reminder to update your expenses/income list
    [] Reminder of payment deadlines


### 2. Screen Archetypes

* Login/Signup screen
   * Users can login by email and password or create an account
* Total balance screen (Home screen)
    * Users can see a global balance of their money, including total expenses and total income
    * Users can see their finances insights by month/year
* Expense list screen
    * Users can register expenses
    * Users can categorize any register by some default categories (food, entertainment, home, transport, education, clothing, payments, others)
    * Users can see a graph of their expenses by category
    * Users can search for an specific register by name or filter categories
    * Users can add a photo of a ticket to a register
* Income list screen
    * Users can register income
    * Users can categorize any register by some default categories (food, entertainment, home, transport, education, clothing, payments, others)
    * Users can see a graph of their expenses by category
    * Users can search for an specific register by name or filter categories
    * Users can add a photo of a ticket to a register
* Profile screen
    * Users can see their information and logout
    * Users can add, edit or delete categories
* Cards and accounts - OPTIONAL
    * Users can create virtual money accounts associated with their real accounts
    * Users can create virtual credit cards associated with their real cards

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Total balance screen (Home Screen)
* Expense list screen
* Income list screen
* Profile screen
* Cards and accounts - OPTIONAL

**Flow Navigation** (Screen to Screen)

* Login/Signup screen ---> Total balance screen (Home Screen)
* Expense list screen ---> Create new expense screen
* Income list screen ----> Crete income screen

## Wireframes
<img src="https://raw.githubusercontent.com/luisedgtz/Fintal-FBU/main/wireframe.png" width=600>

### Digital Wireframes & Mockups

### Interactive Prototype

## Schema 

### Models

User
| Property      | Type   | Description                     |
| ------------- | ------ | ------------------------------- |
| objectId      | String | unique id for a user            |
| name          | String | name of the user                |
| lastName      | String | lastname of the user            |
| profilePhoto  | File   | File of profile picture         |
| email         | String | email of the user               |
| password      | String | password fot the user's account |
| totalIncome   | Number | total income of the user        |
| totalExpenses | Number | total expenses of the user      |

Register
| Property  | Type                | Description                                      |
| --------- | ------------------- | ------------------------------------------------ |
| objectId  | String              | unique id for a register                         |
| createdAt | Date Time           | date in which the register was created           |
| type      | Boolean             | Type of register, true = income, false = expense |
| user      | Pointer to User     | User that created this register                  |
| ammount   | Number              | Ammount related to income or expense             |
| category  | Pointer to Category | Category to what this register is related        |
| filePhoto | File                | photo of ticket related to the register          |

Category
| Property | Type   | Column 3                     |
| -------- | ------ | ---------------------------- |
| objectId | String | unique id for the category   |
| name     | String | Name of the category         |
| iconFile | File   | icon related to the category |


### Networking
- List of network requests by screen
    - Total balance screen (Home screen)
        - Read/GET Total income ammount
        - Read/GET Total expense ammount
        - Read/GET Query last 5 registers
    - Expense list screen
        - Read/GET Query registers where type = false
        - Create/POST Register with type = false
        - Delete existing expense register
    - Income list screen
        - Read/GET Query registers where type = true
        - Create/POST Register with type = true
        - Delete existing income register
    - Profile screen
        - Read/GET Query logged in user
        - Update/PUT Update user profile image
