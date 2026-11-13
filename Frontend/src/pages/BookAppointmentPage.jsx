import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { hospitalApi } from '../api/hospitalApi.js';
import { getErrorMessage, getFieldErrors } from '../api/errors.js';
import Spinner from '../components/Spinner.jsx';
import { useToast } from '../context/ToastContext.jsx';

const initialForm = {
  doctorId: '',
  appointmentDate: '',
  startTime: '',
  endTime: '',
  reason: '',
  notes: '',
};

function todayAsDateInput() {
  const now = new Date();
  const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 10);
}

function currentTimeAsTimeInput() {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
}

function FieldError({ children }) {
  return children ? <p className="mt-1.5 text-sm text-rose-700" role="alert">{children}</p> : null;
}

export default function BookAppointmentPage() {
  const location = useLocation();
  const doctor = location.state?.doctor;
  const navigate = useNavigate();
  const toast = useToast();
  const [form, setForm] = useState({ ...initialForm, doctorId: doctor?.id ? String(doctor.id) : '' });
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
    setFieldErrors((current) => ({ ...current, [name]: undefined }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setFieldErrors({});

    const errors = {};
    if (!/^[1-9]\d*$/.test(form.doctorId)) errors.doctorId = 'Choose a doctor by name from Find a doctor.';
    if (!form.appointmentDate) errors.appointmentDate = 'Choose an appointment date.';
    else if (form.appointmentDate < todayAsDateInput()) errors.appointmentDate = 'Choose today or a future date.';
    if (!form.startTime) errors.startTime = 'Choose a start time.';
    else if (form.appointmentDate === todayAsDateInput() && form.startTime <= currentTimeAsTimeInput()) {
      errors.startTime = 'Choose a start time later than now for today’s appointment.';
    }
    if (!form.endTime) errors.endTime = 'Choose an end time.';
    if (form.startTime && form.endTime && form.startTime >= form.endTime) errors.endTime = 'End time must be after start time.';
    if (!form.reason.trim()) errors.reason = 'Reason for appointment is required.';
    if (form.reason.length > 255) errors.reason = 'Reason cannot exceed 255 characters.';
    if (form.notes.length > 1000) errors.notes = 'Notes cannot exceed 1000 characters.';
    if (Object.keys(errors).length) {
      setFieldErrors(errors);
      return;
    }

    setSubmitting(true);
    try {
      await hospitalApi.bookAppointment({
        doctorId: Number(form.doctorId),
        appointmentDate: form.appointmentDate,
        startTime: `${form.startTime}:00`,
        endTime: `${form.endTime}:00`,
        reason: form.reason.trim(),
        notes: form.notes.trim() || null,
      });
      toast('Your appointment request was submitted.');
      navigate('/appointments', { replace: true });
    } catch (requestError) {
      setFieldErrors(getFieldErrors(requestError));
      setError(getErrorMessage(requestError));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div>
      <div className="mb-8">
        <p className="text-sm font-semibold text-teal-600">PLAN YOUR VISIT</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-tight text-ink sm:text-4xl">Book an appointment</h1>
        <p className="mt-2 text-muted">Share a few details so the care team can prepare for your visit.</p>
      </div>

      <div className="grid items-start gap-6 lg:grid-cols-[minmax(0,1fr)_20rem]">
        <form className="card space-y-5 p-5 sm:p-7" onSubmit={handleSubmit} noValidate>
          {error && <p className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800" role="alert">{error}</p>}
          <div>
            <p className="label">Selected doctor</p>
            {doctor?.id ? (
              <div className="field flex min-h-11 items-center justify-between gap-3" id="doctorId" aria-describedby="doctor-id-help">
                <span className="min-w-0">
                  <span className="block truncate font-semibold text-ink">{doctor.name || 'Doctor'}</span>
                  <span className="block truncate text-sm text-muted">{doctor.specialization || 'Specialization unavailable'}</span>
                </span>
                <Link className="shrink-0 text-sm font-semibold text-teal-700 underline" to="/doctors">Change</Link>
              </div>
            ) : (
              <div className="field flex min-h-11 items-center justify-between gap-3" id="doctorId" aria-describedby="doctor-id-help">
                <span className="text-sm text-muted">Choose a doctor before booking.</span>
                <Link className="shrink-0 text-sm font-semibold text-teal-700 underline" to="/doctors">Find a doctor</Link>
              </div>
            )}
            <FieldError>{fieldErrors.doctorId}</FieldError>
            <p className="sr-only" id="doctor-id-help">The selected doctor’s ID will be included with your appointment request.</p>
          </div>
          <div>
            <label className="label" htmlFor="appointmentDate">Appointment date</label>
            <input className="field" id="appointmentDate" name="appointmentDate" type="date" min={todayAsDateInput()} value={form.appointmentDate} onChange={updateField} required />
            <FieldError>{fieldErrors.appointmentDate}</FieldError>
          </div>
          <div className="grid gap-5 sm:grid-cols-2">
            <div>
              <label className="label" htmlFor="startTime">Start time</label>
              <input className="field" id="startTime" name="startTime" type="time" value={form.startTime} onChange={updateField} required />
              <FieldError>{fieldErrors.startTime}</FieldError>
            </div>
            <div>
              <label className="label" htmlFor="endTime">End time</label>
              <input className="field" id="endTime" name="endTime" type="time" value={form.endTime} onChange={updateField} required />
              <FieldError>{fieldErrors.endTime}</FieldError>
            </div>
          </div>
          <div>
            <label className="label" htmlFor="reason">Reason for appointment</label>
            <input className="field" id="reason" name="reason" maxLength={255} value={form.reason} onChange={updateField} required />
            <div className="mt-1 flex justify-between gap-2">
              <FieldError>{fieldErrors.reason}</FieldError>
              <span className="shrink-0 text-xs text-muted">{form.reason.length}/255</span>
            </div>
          </div>
          <div>
            <label className="label" htmlFor="notes">Notes <span className="font-normal text-muted">(optional)</span></label>
            <textarea className="field min-h-28 resize-y" id="notes" name="notes" maxLength={1000} value={form.notes} onChange={updateField} />
            <div className="mt-1 flex justify-between gap-2">
              <FieldError>{fieldErrors.notes}</FieldError>
              <span className="shrink-0 text-xs text-muted">{form.notes.length}/1000</span>
            </div>
          </div>
          <div className="flex flex-col-reverse gap-3 pt-1 sm:flex-row sm:justify-end">
            <Link className="secondary-button" to={doctor ? '/doctors' : '/appointments'}>Back</Link>
            <button className="primary-button" type="submit" disabled={submitting}>
              {submitting ? <Spinner label="Submitting request" small /> : 'Request appointment'}
            </button>
          </div>
        </form>

        <aside className="rounded-2xl border border-teal-100 bg-teal-50/70 p-5 sm:p-6">
          <span className="grid h-11 w-11 place-items-center rounded-xl bg-white text-xl text-teal-700 shadow-sm" aria-hidden="true">i</span>
          <h2 className="mt-4 font-semibold text-ink">Before you book</h2>
          <p className="mt-2 text-sm leading-6 text-muted">You can book for today if the start time is still ahead. The end time must be later than the start time.</p>
          <p className="mt-3 text-sm leading-6 text-muted">You can review your appointment details on the My Appointments page after it has been submitted.</p>
        </aside>
      </div>
    </div>
  );
}
