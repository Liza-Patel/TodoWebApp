package com.greatlearning.todo.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

 @PostMapping("/verify")
 public ResponseEntity<String> verifyToken(@RequestBody Map<String, String> body) {
     String idToken = body.get("token");
     try {
         FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
         String uid = decodedToken.getUid();
         return ResponseEntity.ok("Authenticated: " + uid);
     } catch (FirebaseAuthException e) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
     }
 }
}

