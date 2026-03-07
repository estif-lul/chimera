import type { SoulDefinitionView } from '../../services/api/agentProfile';

interface Props {
  soul: SoulDefinitionView;
}

/**
 * Immutable soul definition detail card.
 */
export default function SoulDefinitionCard({ soul }: Props) {
  return (
    <section aria-labelledby="soul-heading">
      <h3 id="soul-heading">
        Soul Definition — {soul.personaSlug} v{soul.version}
      </h3>
      <dl>
        <dt>Backstory</dt>
        <dd>{soul.backstory}</dd>

        <dt>Voice &amp; Tone</dt>
        <dd>{soul.voiceTone}</dd>

        <dt>Core Beliefs &amp; Values</dt>
        <dd>{soul.coreBeliefsAndValues}</dd>

        <dt>Directives</dt>
        <dd>{soul.directives}</dd>
      </dl>
    </section>
  );
}
