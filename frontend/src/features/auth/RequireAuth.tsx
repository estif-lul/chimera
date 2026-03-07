import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useSessionStore } from './useSession';

/**
 * Route guard that redirects unauthenticated users to the login page.
 */
export function RequireAuth() {
  const session = useSessionStore((s) => s.session);
  const location = useLocation();

  if (!session) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
}
