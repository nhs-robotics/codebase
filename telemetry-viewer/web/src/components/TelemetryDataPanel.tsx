import type { TelemetryDataMap } from '../types/telemetry';
import { useTheme } from '../theme';

interface Props {
  telemetryData: TelemetryDataMap;
}

export function TelemetryDataPanel({ telemetryData }: Props) {
  const { theme } = useTheme();
  const entries = Object.entries(telemetryData);

  return (
    <div style={{ background: theme.bgPanel, overflow: 'auto', padding: 12, borderRadius: 4 }}>
      <div style={{ color: theme.textTertiary, marginBottom: 8, fontSize: 11, textTransform: 'uppercase', letterSpacing: 1 }}>
        Telemetry Data
      </div>
      {entries.length === 0 ? (
        <div style={{ color: theme.textFaded, fontSize: 12 }}>No data</div>
      ) : (
        entries.map(([key, { type, value }]) => (
          <div
            key={key}
            style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 3, gap: 12, fontSize: 13 }}
          >
            <span style={{ color: theme.textLabel }}>{key}</span>
            <span style={{ color: type === 'double' || type === 'integer' ? theme.colorNumeric : theme.colorString }}>
              {value == null
                ? 'null'
                : typeof value === 'number'
                ? value.toFixed(4)
                : String(value)}
            </span>
          </div>
        ))
      )}
    </div>
  );
}
