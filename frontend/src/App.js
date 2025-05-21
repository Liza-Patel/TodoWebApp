import React, { useState, useEffect, useCallback } from 'react';
import { API } from './api';
import './App.css';
import { auth, provider, signInWithPopup, signOut, onAuthStateChanged } from './firebase';

function App() {
  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState('');
  const [msg, setMsg] = useState('');
  const [editId, setEditId] = useState(null);
  const [editText, setEditText] = useState('');
  const [user, setUser] = useState(null);

  const fetchTodos = useCallback(async () => {
    if (!user) return;
    try {
      const token = await user.getIdToken();
      const res = await API.get('/todos', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTodos(res.data);
    } catch (error) {
      console.error('Failed to fetch todos:', error);
    }
  }, [user]);

  const addTodo = async () => {
    if (title.trim() === '') return;
    const token = await user.getIdToken();
    await API.post('/todos', { title, completed: false }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    setTitle('');
    fetchTodos();
  };

  const deleteTodo = async (id) => {
    const token = await user.getIdToken();
    await API.delete(`/todos/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    fetchTodos();
  };

  const toggleCompleted = async (todo) => {
    const token = await user.getIdToken();
    await API.put(`/todos/${todo.id}`, {
      ...todo,
      completed: !todo.completed,
    }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    fetchTodos();
  };

  const startEditing = (todo) => {
    setEditId(todo.id);
    setEditText(todo.title);
  };

  const saveEdit = async (id) => {
    const token = await user.getIdToken();
    await API.put(`/todos/${id}`, {
      ...todos.find((t) => t.id === id),
      title: editText,
    }, {
      headers: { Authorization: `Bearer ${token}` }
    });
    setEditId(null);
    setEditText('');
    fetchTodos();
  };

  const summarize = async () => {
    try {
      const token = await user.getIdToken();
      const res = await API.post('/summarize', {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMsg(res.data);
    } catch {
      setMsg("Failed to summarize.");
    }
  };

  const loginWithGoogle = async () => {
    try {
      const result = await signInWithPopup(auth, provider);
      const loggedUser = result.user;
      setUser(loggedUser);

      const token = await loggedUser.getIdToken();
      await fetch("http://localhost:8080/auth/verify", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token }),
      });
    } catch (error) {
      console.error("Google Sign-In Error:", error);
    }
  };

  const logout = () => {
    signOut(auth);
    setUser(null);
    setTodos([]);
  };

  // Detect user login state on app start and persist session
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      if (firebaseUser) {
        setUser(firebaseUser);
      } else {
        setUser(null);
      }
    });
    return () => unsubscribe();
  }, []);

  // Fetch todos when user is set
  useEffect(() => {
    if (user) fetchTodos();
  }, [user, fetchTodos]);

  return (
    <div>
      <h1>Todo Summary Assistant</h1>

      {!user ? (
        <div className="login-container">
          <button onClick={loginWithGoogle}>Sign in with Google</button>
        </div>
      ) : (
        <>
          <p>Welcome, {user.displayName}</p>
          <div className="user-controls">
          <img src={user.photoURL} alt="profile" width={40} />
          <button onClick={logout}>Logout</button>
</div>

          <div className="input-container">
            <input
              placeholder="Enter new task"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
            <button onClick={addTodo}>Add</button>
          </div>

          <ul>
            {todos.map((todo) => (
              <li key={todo.id}>
                <input
                  type="checkbox"
                  checked={todo.completed}
                  onChange={() => toggleCompleted(todo)}
                />
                {editId === todo.id ? (
                  <>
                    <input
                      value={editText}
                      onChange={(e) => setEditText(e.target.value)}
                    />
                    <button onClick={() => saveEdit(todo.id)}>Save</button>
                  </>
                ) : (
                  <>
                    <span className={todo.completed ? 'completed' : ''}>
                      {todo.title}
                    </span>
                    <button onClick={() => startEditing(todo)}>Edit</button>
                  </>
                )}
                <button onClick={() => deleteTodo(todo.id)}>X</button>
              </li>
            ))}
          </ul>

          <div className="summary-button-container">
            <button onClick={summarize}>Summarize &amp; Send to Slack</button>
          </div>
          {msg && <p>{msg}</p>}
        </>
      )}
    </div>
  );
}

export default App;
