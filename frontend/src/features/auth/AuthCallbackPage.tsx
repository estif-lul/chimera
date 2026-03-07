import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../services/api/client';
import { useSessionStore } from './useSession';

/**
 * OIDC callback landing page. Exchanges authorization result for a session.
 */
export function AuthCallbackPage() {
  const navigate = useNavigate();
  const setSession = useSessionStore((s) => s.setSession);

  useEffect(() => {
    apiClient.get('/api/v1/auth/session').then((session) => {
      setSession(session);
      navigate('/campaigns');
    });
  }, [navigate, setSession]);

  return <p>Authenticating…</p>;
}
