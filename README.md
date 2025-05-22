# ✅ Todo Summary Assistant

A full-stack productivity app that allows users to manage their personal to-dos, generate AI-powered summaries of pending tasks, and send them to a Slack channel with one click.

---

## 🚀 Features

- 🔒 Firebase Authentication (Google Sign-In)
- 📝 Add, edit, and delete personal to-dos
- 📋 View current to-do list
- 🤖 Summarize pending to-dos using Cohere
- 📤 Send summary to Slack channel
- ✅ Success/failure feedback for Slack operation

---

## 🛠️ Tech Stack

| Layer         | Technology              |
|---------------|--------------------------|
| Frontend      | React                    |
| Backend       | Java Spring Boot         |
| Auth          | Firebase (Google Sign-In) |
| Database      | Firebase Firestore       |
| LLM API       | Cohere                   |
| Slack         | Incoming Webhook         |

---

🔁 Backend – Spring Boot (Java)
Create a New Spring Boot Project

Use Spring Initializr or your IDE (IntelliJ, Eclipse, etc.) to generate a new Spring Boot project with the following dependencies:

Spring Web
Firebase Admin
Spring Boot DevTools
Lombok

Clone the Repository

git clone https://github.com/Liza-Patel/TodoWebApp.git
cd todo-summary-assistant/backend-springboot
Add Source Code

Replace or copy the contents of the src/ folder in the cloned repo into your generated Spring Boot project’s src/ directory:

src/
├── main/
│   ├── java/com/example/todo
│   └── resources/
Configure Environment Variables

Rename application.properties.example to application.properties in src/main/resources/.

Fill in the required values:

FIREBASE.DATABASE.URL=https://your-firebase-project.firebaseio.com
COHERE.API.KEY=your-cohere-api-key
SLACK.WEBHOOK.URL=https://hooks.slack.com/services/your-webhook-url
FIREBASE_SERVICE_ACCOUNT=yourServiceAccountKeyInASingleLine

Run the Spring Boot Application

From your IDE or terminal:

./mvnw spring-boot:run
Or using the packaged jar:

./mvnw clean install
java -jar target/*.jar


💻 Frontend – React
Create a React App

If you haven't already:

npx create-react-app todo-summary-frontend
cd todo-summary-frontend
Integrate Project Files

Replace App.js and App.css with the versions provided in the GitHub repo.

Copy firebase.js and api.js into the src/ folder.

Configure Environment Variables

Create a .env file in the root of the React project.

Use the .env.example provided as a template:

REACT_APP_FIREBASE_API_KEY=...
REACT_APP_FIREBASE_AUTH_DOMAIN=...
REACT_APP_FIREBASE_PROJECT_ID=...
REACT_APP_FIREBASE_APP_ID=1:...
REACT_APP_BACKEND_URL=http://localhost:8080
Restart the React dev server after setting .env.

Start the React App

npm install
npm install axios firebase
npm start


## Setup guidance for LLM and Slack
##Cohere
Step 1: Sign Up & Get API Key
Go to Cohere

Create an account (free tier available).

Navigate to the Dashboard > API Keys

Copy the default API key (keep it safe).

🛠️ Step 2: Add API Key to Spring Boot
In your application.properties

## Slack

Step 1: Create a Slack App & Webhook
Visit: https://api.slack.com/apps

Click "Create New App" → From Scratch

Name it and select your workspace.

Go to Incoming Webhooks in the left menu

Click "Activate Incoming Webhooks"

Click "Add New Webhook to Workspace"

Choose the target channel and click Allow

Copy the generated webhook URL (starts with https://hooks.slack.com/services/)

🛠️ Step 2: Add Webhook URL to application.properties

## Desgin and Architechture decisions
1. The application is cleanly separated into:

Frontend (React): Handles UI, authentication, and user interaction.

Backend (Spring Boot): Handles API logic, database interaction, LLM integration, and Slack communication. 

Firebase: Provides secure authentication and scalable cloud database (Firestore).

External APIs: Cohere for natural language summarization and Slack for notification delivery.

This separation ensures scalability, maintainability, and secure handling of tokens and secrets.

2. Authentication with Firebase
Firebase Authentication is used for Google Sign-In.

The React frontend securely handles the sign-in process and passes the user's ID token to the backend.

The backend verifies the token using the Firebase Admin SDK and uses the authenticated UID to scope the user’s todos.

3. Firestore as the Database
A NoSQL document model is used to store to-do items

4. Spring Boot for API Layer
Lightweight, fast, and easy to integrate with Firebase and external APIs.

Offers strong type safety, dependency injection, and RESTful API support.

5. LLM Integration using Cohere
Prompt-based summarization using Cohere's generate endpoint.

Takes the list of pending todos and asks the model to return a concise, meaningful summary.

🤖 Why Cohere?

Offers a generous free tier and is simple to integrate.

6. Slack Integration via Incoming Webhooks
After generating the summary, the backend sends a POST request to a pre-configured Slack Webhook.

The summary appears in a designated Slack channel.

7. Frontend with React
Built using functional components and hooks (useState, useEffect).

8. Security Considerations
API secrets (OpenAI/Cohere keys, Slack URL) are never exposed in frontend.

Firebase tokens are verified in backend before processing requests.

All backend environment variables are injected from application.properties.



