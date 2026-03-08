import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';
import {
  Loader2,
  AlertCircle,
  Inbox,
  User,
  Bot,
  FileText,
  Clock,
} from 'lucide-react';

interface AuditEvent {
  id: string;
  actorType: string;
  actorId: string;
  eventType: string;
  resourceType: string;
  resourceId: string;
  createdAt: string;
}

function actorIcon(actorType: string) {
  return actorType.toLowerCase() === 'agent' ? <Bot size={14} /> : <User size={14} />;
}

/**
 * Displays a timeline of audit events scoped to the current tenant.
 */
export default function AuditTimelinePage() {
  const events = useQuery<AuditEvent[]>({
    queryKey: ['audit'],
    queryFn: () => apiClient.get('/api/v1/audit'),
  });

  if (events.isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading audit timeline</span>
      </div>
    );
  }

  if (events.isError) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        {events.error.message}
      </div>
    );
  }

  const data = events.data ?? [];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Audit Log</h1>
          <p className="page-subtitle">{data.length} event{data.length !== 1 ? 's' : ''} recorded</p>
        </div>
      </div>

      {data.length === 0 ? (
        <div className="empty-state">
          <Inbox size={48} />
          <p>No audit events recorded yet.</p>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Time</th>
                <th>Actor</th>
                <th>Event</th>
                <th>Resource</th>
              </tr>
            </thead>
            <tbody>
              {data.map((evt) => (
                <tr key={evt.id}>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)', whiteSpace: 'nowrap' }}>
                      <Clock size={14} style={{ color: 'var(--color-text-tertiary)' }} />
                      {new Date(evt.createdAt).toLocaleString()}
                    </span>
                  </td>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                      {actorIcon(evt.actorType)}
                      <span>
                        <span className="badge badge-default" style={{ marginRight: 'var(--space-1)' }}>{evt.actorType}</span>
                        {evt.actorId}
                      </span>
                    </span>
                  </td>
                  <td>
                    <span className="badge badge-accent">{evt.eventType}</span>
                  </td>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                      <FileText size={14} style={{ color: 'var(--color-text-tertiary)' }} />
                      <span style={{ color: 'var(--color-text-tertiary)' }}>{evt.resourceType}:</span>
                      <span style={{ fontFamily: 'var(--font-mono)', fontSize: 'var(--text-xs)' }}>{evt.resourceId}</span>
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
