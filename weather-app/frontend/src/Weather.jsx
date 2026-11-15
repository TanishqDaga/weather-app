import { useState } from "react";

export default function Weather({ token, onLogout }) {
  const [city, setCity] = useState("");
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSearch(e) {
    e.preventDefault();
    setError("");
    setData(null);
    setLoading(true);
    try {
      const res = await fetch(`/api/weather?city=${encodeURIComponent(city)}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const body = await res.json();
      if (res.status === 401) return onLogout(); // token no longer valid
      if (!res.ok) throw new Error(body.error || "Something went wrong");
      setData(body);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="card">
      <header className="bar">
        <h1>Weather</h1>
        <button className="link" onClick={onLogout}>Sign out</button>
      </header>

      <form onSubmit={handleSearch} className="row">
        <input
          value={city}
          onChange={(e) => setCity(e.target.value)}
          placeholder="Search a city, e.g. Chennai"
          required
        />
        <button type="submit" disabled={loading}>{loading ? "..." : "Search"}</button>
      </form>

      {error && <p className="error">{error}</p>}

      {data && (
        <section className="result">
          <h2>{data.city}, {data.country}</h2>
          <p className="temp">{Math.round(data.temperature)}°C</p>
          <p>{data.condition}</p>
          <p className="hint">Humidity {data.humidity}% · Wind {data.windSpeed} km/h</p>
        </section>
      )}
    </main>
  );
}
