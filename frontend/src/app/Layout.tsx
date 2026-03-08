import { useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useSessionStore } from '../features/auth/useSession';
import {
  Hexagon,
  LayoutDashboard,
  Users,
  ClipboardCheck,
  Wallet,
  ScrollText,
  LogOut,
  Menu,
  X,
  Plus,
} from 'lucide-react';

/**
 * Application shell with sidebar navigation and top bar.
 * Wraps all authenticated routes via React Router Outlet.
 */
export function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();
  const clearSession = useSessionStore((s) => s.clearSession);

  const pageTitle = getPageTitle(location.pathname);

  function closeSidebar() {
    setSidebarOpen(false);
  }

  return (
    <div className="app-layout">
      {/* Mobile overlay */}
      <div
        className={`sidebar-overlay${sidebarOpen ? ' open' : ''}`}
        onClick={closeSidebar}
      />

      {/* Sidebar */}
      <aside className={`sidebar${sidebarOpen ? ' open' : ''}`}>
        <div className="sidebar-header">
          <Hexagon size={22} className="sidebar-logo-icon" />
          <span className="sidebar-logo">Chimera</span>
        </div>

        <nav className="sidebar-nav">
          <span className="sidebar-section-label">Operations</span>
          <NavLink
            to="/campaigns"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <LayoutDashboard size={18} />
            Campaigns
          </NavLink>
          <NavLink
            to="/campaigns/new"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <Plus size={18} />
            New Campaign
          </NavLink>

          <span className="sidebar-section-label">Agents</span>
          <NavLink
            to="/agents"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <Users size={18} />
            Agent Roster
          </NavLink>

          <span className="sidebar-section-label">Governance</span>
          <NavLink
            to="/review"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <ClipboardCheck size={18} />
            Review Queue
          </NavLink>
          <NavLink
            to="/wallets"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <Wallet size={18} />
            Wallets
          </NavLink>
          <NavLink
            to="/audit"
            className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
            onClick={closeSidebar}
          >
            <ScrollText size={18} />
            Audit Log
          </NavLink>
        </nav>

        <div className="sidebar-footer">
          <button className="nav-link" onClick={clearSession} style={{ width: '100%', border: 'none', background: 'none', cursor: 'pointer' }}>
            <LogOut size={18} />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main */}
      <div className="main-content">
        <header className="topbar">
          <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
            <button className="mobile-menu-btn" onClick={() => setSidebarOpen(true)}>
              {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
            <span className="topbar-title">{pageTitle}</span>
          </div>
        </header>

        <div className="page-container">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

function getPageTitle(pathname: string): string {
  if (pathname.startsWith('/campaigns/new')) return 'New Campaign';
  if (pathname.includes('/plan')) return 'Execution Plan';
  if (pathname.startsWith('/campaigns')) return 'Campaigns';
  if (pathname.startsWith('/agents/')) return 'Agent Profile';
  if (pathname.startsWith('/agents')) return 'Agents';
  if (pathname.startsWith('/review/')) return 'Review Decision';
  if (pathname.startsWith('/review')) return 'Review Queue';
  if (pathname.startsWith('/wallets')) return 'Wallets';
  if (pathname.startsWith('/audit')) return 'Audit Log';
  return 'Chimera';
}
