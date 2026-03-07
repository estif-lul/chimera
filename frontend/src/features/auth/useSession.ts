import { create } from 'zustand';

interface Session {
  userId: string;
  tenantWorkspaceId: string;
  roles: string[];
  authProviderType: string;
}

interface SessionState {
  session: Session | null;
  setSession: (session: Session) => void;
  clearSession: () => void;
}

/**
 * Minimal session store for the authenticated principal.
 * Replaced by server-state when a more robust auth flow is in place.
 */
export const useSessionStore = create<SessionState>((set) => ({
  session: null,
  setSession: (session) => set({ session }),
  clearSession: () => set({ session: null }),
}));
