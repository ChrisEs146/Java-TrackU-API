# Track U API (Java version) -- In Progress
This project is a recreation of the [TrackU API](https://github.com/ChrisEs146/TrackU-API) I created with Node js, Express and MongoDb.

This time I'm using Spring Boot 3.0, Spring Security 6, MySQL and Spring Data JPA.

The API allows a user to create an account in the TrackU app and perform a set of actions like signing in, updating the username, changing the password and deleting the account.

With a valid account a user can add personal projects. In order to add a new project the user must input a title and a description. Each project will have the following fields:

- **Title**: Provided by the user on creation.
- **Description**: Provided by the user on creation.
- **Date**: Auto-generated.
- **Status**: Set by default to "Not Started".
- **Progress**: Set by default to 0;

A project can be edited or deleted, but it can also have updates, these updates are small insights about the project's progress. To add an update the user must provide a title and a description.

The API manages authentication using jwt Tokens. When the user signs in, the API sends an access token to the client and stores a refresh token in a httpOnly cookie. Once the access token expires a request is sent from the client to refresh the token, the API verifies the refresh token expiry and if the refresh token is still valid it proceeds to update the access token, otherwise an 401 status code is sent with the message "Token Expired" and the user is redirected to the sign-in form.