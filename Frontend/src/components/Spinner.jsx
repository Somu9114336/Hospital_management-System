export default function Spinner({ label = 'Loading', small = false }) {
  return (
    <span className="inline-flex items-center gap-2 text-sm text-muted" role="status">
      <span className={`animate-spin rounded-full border-2 border-teal-100 border-t-teal-600 ${small ? 'h-4 w-4' : 'h-6 w-6'}`} />
      <span>{label}</span>
    </span>
  );
}
