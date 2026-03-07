import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';

interface AuditEvent {
  id: string;
  actorType: string;
  actorId: string;
  eventType: string;
  resourceType: string;
  resourceId: string;
  createdAt: string;
}

/**
 * Displays a timeline of audit events scoped to the current tenant.
 */
export default function AuditTimelinePage() {
  const events = useQuery({
    queryKey: ['audit'],
    queryFn: () => apiClient<AuditEvent[]>('/api/v1/audit'),
  });

  if (events.isLoading) return <p>Loading audit timeline...</p>;
  if (events.isError) return <p role="alert">Error: {events.error.message}</p>;

  return (
    <div>
      <h1>Audit Timeline</h1>
      {events.data?.length === 0 && <p>No audit events yet.</p>}

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
          {events.data?.map((evt) => (
            <tr key={evt.id}>
              <td>{new Date(evt.createdAt).toLocaleString()}</td>
              <td>{evt.actorType}: {evt.actorId}</td>
              <td>{evt.eventType}</td>
              <td>{evt.resourceType}: {evt.resourceId}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
