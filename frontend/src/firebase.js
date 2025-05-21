
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, signInWithPopup, signOut, onAuthStateChanged } from "firebase/auth";

const apiKey = process.env.REACT_APP_FIREBASE_API_KEY;
const authDomain = process.env.REACT_APP_FIREBASE_AUTH_DOMAIN;
const projectId = process.env.REACT_APP_FIREBASE_PROJECT_ID
const appId = process.env.REACT_APP_FIREBASE_APP_ID

console.log("Firebase API Key:", process.env.REACT_APP_FIREBASE_API_KEY);


const firebaseConfig = {
  apiKey,
  authDomain,
  projectId,
  appId,
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();

export { auth, provider, signInWithPopup, signOut , onAuthStateChanged};
