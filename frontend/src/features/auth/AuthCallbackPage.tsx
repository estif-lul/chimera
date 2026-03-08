import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../services/api/client';
import { useSessionStore } from './useSession';
import { Loader2 } from 'lucide-react';

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

  return (
    <main style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    }}>
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Authenticating</span>
      </div>
    </main>
  );
}
