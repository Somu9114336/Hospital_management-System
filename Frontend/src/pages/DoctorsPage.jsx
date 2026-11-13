import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { hospitalApi } from '../api/hospitalApi.js';
import { getErrorMessage } from '../api/errors.js';
import Spinner from '../components/Spinner.jsx';

export default function DoctorsPage() {
  const [query, setQuery] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const name = query.trim();
    setError('');
    if (!name) {
      setDoctors([]);
      setSearched(false);
      setLoading(false);
      return undefined;
    }

    let active = true;
    setLoading(true);
    const timer = window.setTimeout(() => {
      hospitalApi.findDoctors(name)
        .then(({ data }) => {
          if (!active) return;
          setDoctors(Array.isArray(data) ? data : (data ? [data] : []));
        })
        .catch((requestError) => {
          if (!active) return;
          if (requestError.response?.status === 404) {
            setDoctors([]);
          } else {
            setDoctors([]);
            setError(getErrorMessage(requestError));
          }
        })
        .finally(() => {
          if (active) {
            setSearched(true);
            setLoading(false);
          }
        });
    }, 350);

    return () => {
      active = false;
      window.clearTimeout(timer);
    };
  }, [query]);

  return (
    <div>
      <div className="mb-8">
        <p className="text-sm font-semibold text-teal-600">YOUR CARE TEAM</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-ink sm:text-4xl">Find a doctor</h1>
        <p className="mt-2 max-w-2xl text-muted">Search by name to discover a doctor and request an appointment.</p>
      </div>

      <section className="card p-5 sm:p-7">
        <label className="label" htmlFor="doctor-search">Doctor’s name</label>
        <div className="relative mt-1.5">
          <span className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-lg text-muted" aria-hidden="true">⌕</span>
          <input
            className="field py-3.5 pl-11"
            id="doctor-search"
            type="search"
            autoComplete="off"
            placeholder="Start typing a name…"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            aria-describedby="search-help"
          />
        </div>
        <p className="mt-2 text-xs text-muted" id="search-help">Results update as you type.</p>

        {loading && <div className="grid min-h-48 place-items-center"><Spinner label="Searching doctors" /></div>}
        {!loading && error && (
          <div className="mt-6 rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900" role="alert">
            <p>{error}</p>
            {error.includes('permission') || error.includes('backend') ? (
              <p className="mt-2">Patient access to <code>/doctor/getbyname/&#123;name&#125;</code> needs to be enabled in the backend security rules.</p>
            ) : null}
          </div>
        )}
        {!loading && !error && searched && doctors.length === 0 && (
          <div className="mt-6 rounded-xl bg-teal-50/60 px-6 py-10 text-center">
            <span className="text-3xl text-teal-600" aria-hidden="true">⌕</span>
            <h2 className="mt-3 text-lg font-semibold text-ink">No doctors found</h2>
            <p className="mt-1 text-sm text-muted">Try another spelling or search for a different name.</p>
          </div>
        )}
        {!loading && !error && doctors.length > 0 && (
          <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {doctors.map((doctor) => (
              <article className="rounded-2xl border border-slate-100 p-5 transition hover:border-teal-100 hover:shadow-sm" key={doctor.id}>
                <div className="flex items-start gap-3">
                  <span className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-teal-50 text-lg font-bold text-teal-700" aria-hidden="true">
                    {doctor.name?.trim()?.[0]?.toUpperCase() || 'D'}
                  </span>
                  <div className="min-w-0">
                    <h2 className="truncate text-lg font-semibold text-ink">{doctor.name || 'Doctor'}</h2>
                    <p className="mt-0.5 text-sm font-medium text-teal-700">{doctor.specialization || 'Specialization unavailable'}</p>
                  </div>
                </div>
                <dl className="mt-5 space-y-2 border-t border-slate-100 pt-4 text-sm">
                  <div className="flex justify-between gap-3"><dt className="text-muted">Email</dt><dd className="break-all text-right text-ink">{doctor.email || '—'}</dd></div>
                  <div className="flex justify-between gap-3"><dt className="text-muted">Phone</dt><dd className="text-ink">{doctor.phone || '—'}</dd></div>
                  <div className="flex justify-between gap-3"><dt className="text-muted">Status</dt><dd className="text-ink">{doctor.active === false ? 'Inactive' : 'Active'}</dd></div>
                </dl>
                {doctor.active === false ? (
                  <button className="secondary-button mt-5 w-full cursor-not-allowed opacity-60" type="button" disabled>Currently unavailable</button>
                ) : (
                  <Link className="primary-button mt-5 w-full" to="/book" state={{ doctor }}>Book appointment</Link>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
