import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../services/api/client';
import { useSessionStore } from './useSession';

export function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const setSession = useSessionStore((s) => s.setSession);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    try {
      const session = await apiClient.post('/api/v1/auth/local/login', { email, password });
      setSession(session);
      navigate('/campaigns');
    } catch {
      setError('Invalid credentials');
    }
  }

  return (
    <main>
      <h1>Chimera Login</h1>
      <form onSubmit={handleSubmit}>
        <label htmlFor="email">Email</label>
        <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />

        <label htmlFor="password">Password</label>
        <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />

        {error && <p role="alert">{error}</p>}
        <button type="submit">Sign in</button>
      </form>
    </main>
  );
}
