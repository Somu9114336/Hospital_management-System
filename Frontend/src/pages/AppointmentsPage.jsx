import { useEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { hospitalApi } from '../api/hospitalApi.js';
import { getErrorMessage } from '../api/errors.js';
import Spinner from '../components/Spinner.jsx';

const responseFields = [
  ['id', 'Appointment ID'],
  ['patientId', 'Patient ID'],
  ['patientName', 'Patient'],
  ['doctorId', 'Doctor ID'],
  ['doctorName', 'Doctor'],
  ['appointmentDate', 'Date'],
  ['startTime', 'Start time'],
  ['endTime', 'End time'],
  ['reason', 'Reason'],
  ['status', 'Status'],
  ['notes', 'Notes'],
  ['createdAt', 'Created at'],
  ['updatedAt', 'Updated at'],
];

function display(value) {
  if (value === null || value === undefined || value === '') return '—';
  return String(value);
}

function todayAsDateInput() {
  const now = new Date();
  const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}

function currentTimeAsTimeInput() {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
}

function formatAppointmentDate(value) {
  if (!value) return 'Date not set';
  const [year, month, day] = value.split('-').map(Number);
  const date = new Date(year, month - 1, day, 12);
  return new Intl.DateTimeFormat(undefined, { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' }).format(date);
}

function formatAppointmentMonth(value) {
  if (!value) return '—';
  const [year, month, day] = value.split('-').map(Number);
  return new Intl.DateTimeFormat(undefined, { month: 'short' }).format(new Date(year, month - 1, day, 12));
}

function formatAppointmentTime(value) {
  if (!value) return 'Time not set';
  const [hours, minutes] = value.split(':').map(Number);
  return new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit' }).format(new Date(2000, 0, 1, hours, minutes));
}

function StatusBadge({ status }) {
  const styles = {
    SCHEDULED: 'bg-blue-50 text-blue-700 ring-blue-200',
    CANCELLED: 'bg-rose-50 text-rose-700 ring-rose-200',
    COMPLETED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  };
  return (
    <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-bold ring-1 ring-inset ${styles[status] || 'bg-slate-100 text-slate-700 ring-slate-200'}`}>
      {display(status)}
    </span>
  );
}

export default function AppointmentsPage() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [status, setStatus] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('newest');
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [detailsError, setDetailsError] = useState('');
  const [actionError, setActionError] = useState('');
  const [actionNotice, setActionNotice] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [rescheduling, setRescheduling] = useState(false);
  const [schedule, setSchedule] = useState({ appointmentDate: '', startTime: '', endTime: '' });
  const detailsRequest = useRef(0);

  useEffect(() => {
    let active = true;
    hospitalApi.getMyAppointments()
      .then(({ data }) => {
        if (active) setAppointments(Array.isArray(data) ? data : []);
      })
      .catch((requestError) => {
        if (active) setError(getErrorMessage(requestError));
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => { active = false; };
  }, []);

  function openAppointment(appointment) {
    const requestId = ++detailsRequest.current;
    setSelectedAppointment(appointment);
    setDetailsLoading(true);
    setDetailsError('');
    setActionError('');
    setActionNotice('');
    setRescheduling(false);

    hospitalApi.getAppointmentById(appointment.id)
      .then(({ data }) => {
        if (requestId !== detailsRequest.current) return;
        setSelectedAppointment(data);
        setSchedule({
          appointmentDate: data.appointmentDate || '',
          startTime: String(data.startTime || '').slice(0, 5),
          endTime: String(data.endTime || '').slice(0, 5),
        });
      })
      .catch((requestError) => {
        if (requestId === detailsRequest.current) setDetailsError(getErrorMessage(requestError));
      })
      .finally(() => {
        if (requestId === detailsRequest.current) setDetailsLoading(false);
      });
  }

  function closeAppointment() {
    detailsRequest.current += 1;
    setSelectedAppointment(null);
    setDetailsError('');
    setActionError('');
    setActionNotice('');
    setRescheduling(false);
  }

  function updateAppointment(updatedAppointment) {
    setAppointments((current) => current.map((appointment) => (
      appointment.id === updatedAppointment.id ? updatedAppointment : appointment
    )));
    setSelectedAppointment((current) => (
      current?.id === updatedAppointment.id ? updatedAppointment : current
    ));
  }

  async function handleCancelAppointment() {
    if (!selectedAppointment || !window.confirm('Are you sure you want to cancel this appointment?')) return;

    setActionLoading(true);
    setActionError('');
    setActionNotice('');
    try {
      const { data } = await hospitalApi.cancelAppointment(selectedAppointment.id);
      updateAppointment(data);
      setActionNotice('Your appointment was cancelled.');
    } catch (requestError) {
      setActionError(getErrorMessage(requestError));
    } finally {
      setActionLoading(false);
    }
  }

  async function handleReschedule(event) {
    event.preventDefault();
    setActionError('');
    setActionNotice('');

    if (!selectedAppointment) return;
    const today = todayAsDateInput();
    if (!schedule.appointmentDate || schedule.appointmentDate < today) {
      setActionError('Choose today or a future date.');
      return;
    }
    if (!schedule.startTime || !schedule.endTime || schedule.startTime >= schedule.endTime) {
      setActionError('Choose a valid start and end time. The end time must be later.');
      return;
    }
    if (schedule.appointmentDate === today && schedule.startTime <= currentTimeAsTimeInput()) {
      setActionError('Choose a start time later than now for a same-day reschedule.');
      return;
    }

    setActionLoading(true);
    try {
      const { data } = await hospitalApi.rescheduleAppointment(selectedAppointment.id, {
        doctorId: Number(selectedAppointment.doctorId),
        appointmentDate: schedule.appointmentDate,
        startTime: `${schedule.startTime}:00`,
        endTime: `${schedule.endTime}:00`,
        reason: selectedAppointment.reason,
        notes: selectedAppointment.notes || null,
      });
      updateAppointment(data);
      setRescheduling(false);
      setActionNotice('Your appointment was rescheduled.');
    } catch (requestError) {
      setActionError(getErrorMessage(requestError));
    } finally {
      setActionLoading(false);
    }
  }

  const visibleAppointments = useMemo(() => {
    return appointments
      .filter((appointment) => status === 'ALL' || appointment.status === status)
      .slice()
      .sort((a, b) => {
        const comparison = String(a.appointmentDate || '').localeCompare(String(b.appointmentDate || ''));
        return sortOrder === 'newest' ? -comparison : comparison;
      });
  }, [appointments, status, sortOrder]);

  const statusCounts = useMemo(() => ({
    SCHEDULED: appointments.filter((appointment) => appointment.status === 'SCHEDULED').length,
    COMPLETED: appointments.filter((appointment) => appointment.status === 'COMPLETED').length,
    CANCELLED: appointments.filter((appointment) => appointment.status === 'CANCELLED').length,
  }), [appointments]);

  const statusFilters = [
    { value: 'ALL', label: 'All', count: appointments.length },
    { value: 'SCHEDULED', label: 'Scheduled', count: statusCounts.SCHEDULED },
    { value: 'COMPLETED', label: 'Completed', count: statusCounts.COMPLETED },
    { value: 'CANCELLED', label: 'Cancelled', count: statusCounts.CANCELLED },
  ];

  return (
    <div>
      <div className="mb-8 flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.18em] text-teal-700">Patient portal</p>
          <h1 className="mt-2 text-3xl font-semibold tracking-tight text-ink sm:text-4xl">My appointments</h1>
          <p className="mt-2 max-w-xl text-sm leading-6 text-muted">Your visits, all in one place. Open an appointment to review or manage it.</p>
        </div>
        <Link className="primary-button w-full sm:w-auto" to="/book">
          <span className="mr-2 text-lg leading-none" aria-hidden="true">+</span> Book a visit
        </Link>
      </div>

      <div className="mb-6 grid gap-3 sm:grid-cols-3">
        {[
          { label: 'Scheduled', count: statusCounts.SCHEDULED, tone: 'bg-teal-50 text-teal-700', mark: '↗' },
          { label: 'Completed', count: statusCounts.COMPLETED, tone: 'bg-emerald-50 text-emerald-700', mark: '✓' },
          { label: 'Cancelled', count: statusCounts.CANCELLED, tone: 'bg-rose-50 text-rose-700', mark: '×' },
        ].map((item) => (
          <div className="card flex items-center gap-4 p-4 sm:p-5" key={item.label}>
            <span className={`grid h-11 w-11 shrink-0 place-items-center rounded-xl text-lg font-bold ${item.tone}`} aria-hidden="true">{item.mark}</span>
            <div>
              <p className="text-2xl font-semibold leading-none text-ink">{item.count}</p>
              <p className="mt-1.5 text-sm text-muted">{item.label} {item.count === 1 ? 'appointment' : 'appointments'}</p>
            </div>
          </div>
        ))}
      </div>

      <section className="card overflow-hidden" aria-label="Appointment list">
        <div className="border-b border-slate-100 px-4 py-5 sm:px-6">
          <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-ink">Your visits</h2>
              <p className="mt-1 text-sm text-muted">{visibleAppointments.length} {visibleAppointments.length === 1 ? 'appointment' : 'appointments'} shown</p>
            </div>
            <label className="flex items-center gap-3 text-sm font-medium text-muted">
              Sort by
              <select className="field mt-0 min-w-40 py-2.5 text-sm" value={sortOrder} onChange={(event) => setSortOrder(event.target.value)} aria-label="Sort appointments by date">
                <option value="newest">Newest first</option>
                <option value="oldest">Oldest first</option>
              </select>
            </label>
          </div>
          <div className="mt-5 flex gap-2 overflow-x-auto pb-1" role="group" aria-label="Filter appointments by status">
            {statusFilters.map((filter) => (
              <button
                className={`inline-flex shrink-0 items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition ${
                  status === filter.value ? 'bg-teal-700 text-white shadow-sm' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
                type="button"
                key={filter.value}
                aria-pressed={status === filter.value}
                onClick={() => setStatus(filter.value)}
              >
                {filter.label}
                <span className={`rounded-full px-1.5 py-0.5 text-xs ${status === filter.value ? 'bg-white/15 text-white' : 'bg-white text-slate-500'}`}>{filter.count}</span>
              </button>
            ))}
          </div>
        </div>

        {loading && <div className="grid min-h-64 place-items-center"><Spinner label="Loading your appointments" /></div>}
        {!loading && error && (
          <div className="m-4 rounded-2xl border border-rose-200 bg-rose-50 p-5 text-sm text-rose-800 sm:m-6" role="alert">
            <p className="font-semibold">We couldn’t load your appointments.</p>
            <p className="mt-1">{error}</p>
            <button className="mt-3 font-semibold underline" type="button" onClick={() => window.location.reload()}>Try again</button>
          </div>
        )}
        {!loading && !error && visibleAppointments.length === 0 && (
          <div className="grid min-h-72 place-items-center px-6 py-12 text-center">
            <div>
              <span className="mx-auto grid h-14 w-14 place-items-center rounded-2xl bg-teal-50 text-2xl text-teal-700" aria-hidden="true">♡</span>
              <h2 className="mt-4 text-lg font-semibold text-ink">{appointments.length ? 'No appointments match this filter' : 'Your schedule is clear'}</h2>
              <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted">
                {appointments.length ? 'Try another status to find the appointment you’re looking for.' : 'When you book a visit, your appointment details will appear here.'}
              </p>
              {appointments.length ? (
                <button className="secondary-button mt-5" type="button" onClick={() => setStatus('ALL')}>Show all appointments</button>
              ) : (
                <Link className="primary-button mt-5" to="/doctors">Find a doctor</Link>
              )}
            </div>
          </div>
        )}

        {!loading && !error && visibleAppointments.length > 0 && (
          <div className="divide-y divide-slate-100">
            {visibleAppointments.map((appointment) => (
              <article className="px-4 py-5 transition hover:bg-slate-50/70 sm:px-6" key={appointment.id}>
                <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
                  <div className="flex min-w-0 flex-1 items-start gap-4">
                    <div className="grid h-14 w-14 shrink-0 place-content-center rounded-2xl bg-teal-50 text-center">
                      <span className="text-[11px] font-bold uppercase tracking-wide text-teal-700">
                        {formatAppointmentMonth(appointment.appointmentDate)}
                      </span>
                      <span className="text-xl font-bold leading-tight text-ink">
                        {appointment.appointmentDate ? Number(appointment.appointmentDate.split('-')[2]) : '—'}
                      </span>
                    </div>
                    <div className="min-w-0 flex-1">
                      <div className="flex flex-wrap items-center gap-2">
                        <StatusBadge status={appointment.status} />
                        <span className="text-xs font-medium text-muted">Appointment #{display(appointment.id)}</span>
                      </div>
                      <h3 className="mt-2 truncate text-lg font-semibold text-ink">{display(appointment.doctorName)}</h3>
                      <p className="mt-1 flex flex-wrap items-center gap-x-2 gap-y-1 text-sm text-muted">
                        <span>{formatAppointmentDate(appointment.appointmentDate)}</span>
                        <span aria-hidden="true">·</span>
                        <span>{formatAppointmentTime(appointment.startTime)} – {formatAppointmentTime(appointment.endTime)}</span>
                      </p>
                      <p className="mt-3 line-clamp-2 text-sm leading-5 text-slate-600">{display(appointment.reason)}</p>
                    </div>
                  </div>
                  <button className="secondary-button w-full shrink-0 sm:w-auto" type="button" onClick={() => openAppointment(appointment)}>
                    View details <span className="ml-2" aria-hidden="true">→</span>
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      {selectedAppointment && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-950/50 p-4" role="presentation">
          <section className="mx-auto my-6 max-w-2xl rounded-2xl bg-white p-5 shadow-xl sm:p-7" role="dialog" aria-modal="true" aria-labelledby="appointment-details-title">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-sm font-semibold text-teal-600">APPOINTMENT DETAILS</p>
                <h2 className="mt-1 text-xl font-semibold text-ink" id="appointment-details-title">Appointment #{display(selectedAppointment.id)}</h2>
              </div>
              <button className="secondary-button shrink-0" type="button" onClick={closeAppointment}>Close</button>
            </div>

            {detailsLoading && <div className="grid min-h-24 place-items-center"><Spinner label="Loading appointment details" /></div>}
            {detailsError && <p className="mt-5 rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800" role="alert">{detailsError}</p>}

            {!detailsLoading && !detailsError && (
              <>
                <dl className="mt-5 grid grid-cols-2 gap-4 rounded-xl bg-slate-50 p-4 text-sm">
                  {['doctorName', 'appointmentDate', 'startTime', 'endTime', 'reason', 'status', 'notes'].map((key) => (
                    <div className={key === 'reason' || key === 'notes' ? 'col-span-2' : ''} key={key}>
                      <dt className="text-xs font-medium text-muted">{responseFields.find(([field]) => field === key)?.[1] || key}</dt>
                      <dd className="mt-1 break-words text-ink">
                        {key === 'status' ? <StatusBadge status={selectedAppointment.status} />
                          : key === 'appointmentDate' ? formatAppointmentDate(selectedAppointment[key])
                            : key === 'startTime' || key === 'endTime' ? formatAppointmentTime(selectedAppointment[key])
                              : display(selectedAppointment[key])}
                      </dd>
                    </div>
                  ))}
                </dl>

                {actionError && <p className="mt-4 rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800" role="alert">{actionError}</p>}
                {actionNotice && <p className="mt-4 rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800" role="status">{actionNotice}</p>}

                {selectedAppointment.status === 'SCHEDULED' && (
                  rescheduling ? (
                    <form className="mt-5 space-y-4 border-t border-slate-100 pt-5" onSubmit={handleReschedule}>
                      <h3 className="font-semibold text-ink">Reschedule appointment</h3>
                      <div>
                        <label className="label" htmlFor="reschedule-date">New date</label>
                        <input className="field" id="reschedule-date" type="date" min={todayAsDateInput()} value={schedule.appointmentDate} onChange={(event) => setSchedule((current) => ({ ...current, appointmentDate: event.target.value }))} required />
                        <p className="mt-1 text-xs text-muted">For today, choose a start time that is still ahead.</p>
                      </div>
                      <div className="grid gap-4 sm:grid-cols-2">
                        <div>
                          <label className="label" htmlFor="reschedule-start">Start time</label>
                          <input className="field" id="reschedule-start" type="time" value={schedule.startTime} onChange={(event) => setSchedule((current) => ({ ...current, startTime: event.target.value }))} required />
                        </div>
                        <div>
                          <label className="label" htmlFor="reschedule-end">End time</label>
                          <input className="field" id="reschedule-end" type="time" value={schedule.endTime} onChange={(event) => setSchedule((current) => ({ ...current, endTime: event.target.value }))} required />
                        </div>
                      </div>
                      <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
                        <button className="secondary-button" type="button" disabled={actionLoading} onClick={() => setRescheduling(false)}>Keep current time</button>
                        <button className="primary-button" type="submit" disabled={actionLoading}>
                          {actionLoading ? <Spinner label="Rescheduling" small /> : 'Save new time'}
                        </button>
                      </div>
                    </form>
                  ) : (
                    <div className="mt-5 flex flex-col gap-3 border-t border-slate-100 pt-5 sm:flex-row sm:justify-end">
                      <button className="secondary-button" type="button" disabled={actionLoading} onClick={() => setRescheduling(true)}>Reschedule</button>
                      <button className="secondary-button border-rose-200 text-rose-700 hover:bg-rose-50" type="button" disabled={actionLoading} onClick={handleCancelAppointment}>
                        {actionLoading ? <Spinner label="Cancelling" small /> : 'Cancel appointment'}
                      </button>
                    </div>
                  )
                )}
              </>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
