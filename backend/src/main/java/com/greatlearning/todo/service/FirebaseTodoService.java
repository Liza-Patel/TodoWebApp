package com.greatlearning.todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.greatlearning.todo.model.Todo;

@Service
public class FirebaseTodoService {
    private final Firestore db = FirestoreClient.getFirestore();

    public List<Todo> getTodosByUid(String uid) throws ExecutionException, InterruptedException {
        List<Todo> todos = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = db.collection("todos")
            .whereEqualTo("uid", uid)
            .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            todos.add(doc.toObject(Todo.class));
        }
        return todos;
    }

    public String addTodo(Todo todo) throws Exception {
        DocumentReference docRef = db.collection("todos").document();
        todo.setId(docRef.getId());
        docRef.set(todo);
        return docRef.getId();
    }

    public void updateTodoIfOwnedByUser(Todo todo) throws Exception {
        DocumentReference docRef = db.collection("todos").document(todo.getId());
        DocumentSnapshot doc = docRef.get().get();
        Todo existing = doc.toObject(Todo.class);
        if (existing != null && todo.getUid().equals(existing.getUid())) {
            docRef.set(todo);
        } else {
            throw new SecurityException("Unauthorized to update this todo.");
        }
    }

    public void deleteTodoIfOwnedByUser(String id, String uid) throws Exception {
        DocumentReference docRef = db.collection("todos").document(id);
        DocumentSnapshot doc = docRef.get().get();
        Todo existing = doc.toObject(Todo.class);
        if (existing != null && uid.equals(existing.getUid())) {
            docRef.delete();
        } else {
            throw new SecurityException("Unauthorized to delete this todo.");
        }
    }
}
