# Multi-Page Quiz Application

A Java Servlet-based web application that allows users to register, login, and take quizzes across multiple pages. The application stores quiz data and user results in a MySQL database and tracks user progress using sessions.

## Features

- ✅ User registration with username, password, and email  
- ✅ Secure login validating credentials from the database  
- ✅ Multi-page quiz flow with at least 3 questions  
- ✅ Questions and answers stored in a MySQL database  
- ✅ Tracks user-selected answers across pages using sessions  
- ✅ Calculates and displays the final score at the end of the quiz  
- ✅ Saves quiz results linked to the user in the database  
- ✅ Logout option to end the session and clear tracking information  

## Database Structure

- **users** table: Stores user credentials and basic information  
- **admins** table: Stores admin credentials  
- **login_sessions** table: Tracks user login sessions  
- **questions** table: Stores quiz questions, options, correct answer, and section/category  
- **results** table: Stores quiz results linked to users, including total questions, correct/wrong answers, and exam date  
- **user_answers** table: Stores answers selected by users for each question, with validation for allowed options (A, B, C, D)  

## Technologies Used

- Java  
- Java Servlets  
- JDBC (Java Database Connectivity)  
- MySQL  
- HTML, CSS for front-end pages  
- HTTP Sessions for user tracking  
