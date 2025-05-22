package com.greatlearning.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.greatlearning.todo.model.Todo;
import com.greatlearning.todo.service.FirebaseTodoService;
import com.greatlearning.todo.util.FirebaseAuthUtil;

@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = "http://localhost:3000")
public class TodoController {

    @Autowired
    private FirebaseTodoService service;

    @GetMapping
    public List<Todo> getTodos(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String uid = FirebaseAuthUtil.verifyAndGetUid(token);
        return service.getTodosByUid(uid);
    }

    @PostMapping
    public ResponseEntity<String> addTodo(
            @RequestBody Todo todo,
            @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String uid = FirebaseAuthUtil.verifyAndGetUid(token);
        todo.setUid(uid);
        return ResponseEntity.ok(service.addTodo(todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String uid = FirebaseAuthUtil.verifyAndGetUid(token);
        service.deleteTodoIfOwnedByUser(id, uid);
        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTodo(
            @PathVariable String id,
            @RequestBody Todo updatedTodo,
            @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        String uid = FirebaseAuthUtil.verifyAndGetUid(token);
        updatedTodo.setId(id);
        updatedTodo.setUid(uid); // Ensure it still belongs to this user
        service.updateTodoIfOwnedByUser(updatedTodo);
        return ResponseEntity.ok("Updated");
    }
}
