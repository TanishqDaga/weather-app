import { useState } from "react";
import Login from "./Login.jsx";
import Weather from "./Weather.jsx";

export default function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));

  function handleLogin(t) {
    localStorage.setItem("token", t);
    setToken(t);
  }

  function handleLogout() {
    localStorage.removeItem("token");
    setToken(null);
  }

  // Login page first; weather page only once we have a token
  return token ? (
    <Weather token={token} onLogout={handleLogout} />
  ) : (
    <Login onLogin={handleLogin} />
  );
}
