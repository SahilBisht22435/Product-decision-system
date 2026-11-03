export function ScoreBar({ label, value, max = 10 }: { label: string; value: number; max?: number }) {
  return (
    <div className="text-xs">
      <div className="mb-1 flex justify-between text-muted-foreground">
        <span>{label}</span>
        <span className="text-foreground">{value}/{max}</span>
      </div>
      <div className="h-1.5 rounded-full bg-muted">
        <div className="h-full rounded-full bg-primary" style={{ width: `${(value / max) * 100}%` }} />
      </div>
    </div>
  );
}
