# Vobot Databasing

## Getting Started
# Prerequisites
  1. Node.js
  2. MySQLWorkbench

# Installing Prerequisites
 Installing Node.js
  - Follow directions on: [NodeJS Installation](http://blog.teamtreehouse.com/install-node-js-npm-mac)
  
 Installing MySQLWorkbench
  - Follow directions on: [MySQLWorkbench Installation](https://dev.mysql.com/doc/workbench/en/wb-installing.html)
  - Note that you only need to install MySQLWorkbench if you want to add, create, or delete tables from the database

## Usage
 1. Navigate to a directory on your machine where you want to run the server.
 2. Download the server.js file to this directory. 
 3. In the directory, run the following on terminal to create a package.json file: ``` npm init ``` 
 4. Launch the server by running the following on terminal: ``` node server.js ```
 5. Open Postman to push and pull information from the database
 
 # Using Postman
  - In order to make requests to the database on Postman, type the following into the address bar:
     ``` http://localhost:8080/ ```
  - Change the route of the request by changing the extension of the link in the address bar. For example,
     ``` http://localhost:8080/sessions ```
  - Based on whether you are trying to make a get request or a post request, change the request tab accordingly
  - If you are trying to make a post request, type in the information you are trying to send inside the body of the request. Use a raw format, and change the text type to JSON.
  
  # Examples
   - Getting information from the sessions table
    - Request Type: GET
    - Address: http://localhost:8080/sessions
    - Output: 
      ```
      {
        "error": false,
        "data": [
          {
            "phone_number": 0,
            "session": 0,
            "childs_word": "Mother",
            "childs_name": "",
            "indiv_score": 0,
            "level": 0
          },
          {
            "phone_number": 1111111111,
            "session": 20,
            "childs_word": "Mom",
            "childs_name": "Ava",
            "indiv_score": 40,
            "level": 4
          }
      }
    ```
    - Adding information to the sessions table
      - Request Type: POST
      - Address: http://localhost:8080/sessions
      - Body:
      ```
      {
	      "phone_number": 1234567890,
	      "session": 4,
	      "childs_word": "Mother",
	      "childs_name": "Ava",
      	"indiv_score": 40,
	      "level": 5
      }
      ```
