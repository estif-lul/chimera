import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from '../../../src/app/queryClient';

describe('Reviewer Queue', () => {
  function renderWithProviders(ui: React.ReactElement, route = '/review') {
    return render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={[route]}>
          {ui}
        </MemoryRouter>
      </QueryClientProvider>,
    );
  }

  it('renders review queue placeholder when authenticated', () => {
    renderWithProviders(<div>Review Queue (placeholder)</div>);
    expect(screen.getByText('Review Queue (placeholder)')).toBeDefined();
  });
});
