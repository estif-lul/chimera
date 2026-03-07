const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

async function request(method: string, path: string, body?: unknown) {
  const options: RequestInit = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
  };
  if (body !== undefined) {
    options.body = JSON.stringify(body);
  }
  const res = await fetch(`${BASE_URL}${path}`, options);
  if (!res.ok) {
    const problem = await res.json().catch(() => null);
    throw new Error(problem?.detail ?? `HTTP ${res.status}`);
  }
  if (res.status === 204 || res.status === 202) return null;
  return res.json();
}

/**
 * Thin HTTP client wrapping fetch with JSON serialization and credentials.
 */
export const apiClient = {
  get: (path: string) => request('GET', path),
  post: (path: string, body?: unknown) => request('POST', path, body),
  put: (path: string, body?: unknown) => request('PUT', path, body),
  patch: (path: string, body?: unknown) => request('PATCH', path, body),
  delete: (path: string) => request('DELETE', path),
};
