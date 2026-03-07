import { useState } from 'react';
import { useCreateCampaign } from './hooks/useCampaigns';
import { useAgents } from '../../services/api/agents';

/**
 * Campaign creation form. Lets operator pick agents, name, goals, and submit.
 */
export default function CampaignCreatePage() {
  const agents = useAgents();
  const createCampaign = useCreateCampaign();

  const [name, setName] = useState('');
  const [goal, setGoal] = useState('');
  const [audience, setAudience] = useState('');
  const [selectedAgents, setSelectedAgents] = useState<string[]>([]);

  function toggleAgent(id: string) {
    setSelectedAgents((prev) =>
      prev.includes(id) ? prev.filter((a) => a !== id) : [...prev, id],
    );
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    createCampaign.mutate({
      name,
      goalDescription: goal,
      targetAudience: audience,
      agentIds: selectedAgents,
    });
  }

  return (
    <div>
      <h1>Create Campaign</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Campaign Name</label>
          <input id="name" value={name} onChange={(e) => setName(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="goal">Goal Description</label>
          <textarea id="goal" value={goal} onChange={(e) => setGoal(e.target.value)} required />
        </div>
        <div>
          <label htmlFor="audience">Target Audience</label>
          <input id="audience" value={audience} onChange={(e) => setAudience(e.target.value)} required />
        </div>
        <fieldset>
          <legend>Assign Agents</legend>
          {agents.isLoading && <p>Loading agents...</p>}
          {agents.data?.map((agent) => (
            <label key={agent.id}>
              <input
                type="checkbox"
                checked={selectedAgents.includes(agent.id)}
                onChange={() => toggleAgent(agent.id)}
              />
              {agent.displayName} ({agent.personaSlug})
            </label>
          ))}
        </fieldset>
        <button type="submit" disabled={createCampaign.isPending}>
          {createCampaign.isPending ? 'Creating...' : 'Create Campaign'}
        </button>
      </form>
      {createCampaign.isError && <p role="alert">Error: {createCampaign.error.message}</p>}
      {createCampaign.isSuccess && <p>Campaign created successfully.</p>}
    </div>
  );
}
