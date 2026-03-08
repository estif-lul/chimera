import type { SoulDefinitionView } from '../../services/api/agentProfile';
import { Sparkles } from 'lucide-react';

interface Props {
  soul: SoulDefinitionView;
}

/**
 * Immutable soul definition detail card.
 */
export default function SoulDefinitionCard({ soul }: Props) {
  return (
    <section className="card" aria-labelledby="soul-heading">
      <div className="card-header">
        <span className="card-title" id="soul-heading">
          <Sparkles size={18} />
          Soul Definition
        </span>
        <span className="badge badge-accent">{soul.personaSlug} v{soul.version}</span>
      </div>
      <div className="info-grid">
        <div className="info-item">
          <span className="info-label">Backstory</span>
          <span className="info-value" style={{ fontWeight: 400 }}>{soul.backstory}</span>
        </div>
        <div className="info-item">
          <span className="info-label">Voice &amp; Tone</span>
          <span className="info-value" style={{ fontWeight: 400 }}>{soul.voiceTone}</span>
        </div>
        <div className="info-item">
          <span className="info-label">Core Beliefs &amp; Values</span>
          <span className="info-value" style={{ fontWeight: 400 }}>{soul.coreBeliefsAndValues}</span>
        </div>
        <div className="info-item">
          <span className="info-label">Directives</span>
          <span className="info-value" style={{ fontWeight: 400 }}>{soul.directives}</span>
        </div>
      </div>
    </section>
  );
}
