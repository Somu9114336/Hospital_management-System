import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { hospitalApi } from '../api/hospitalApi.js';
import { getErrorMessage } from '../api/errors.js';
import AuthShell from '../components/AuthShell.jsx';
import Spinner from '../components/Spinner.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';

const initialForm = { name: '', email: '', phone: '', password: '' };

export default function RegisterPage() {
  const { isAuthenticated } = useAuth();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  if (isAuthenticated) return <Navigate to="/appointments" replace />;

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    if (form.name.trim().length < 4) {
      setError('Name must be at least 4 characters.');
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
      setError('Enter a valid email address.');
      return;
    }
    if (!/^[0-9]{10}$/.test(form.phone)) {
      setError('Phone number must contain exactly 10 digits.');
      return;
    }
    if (form.password.length < 5 || form.password.length > 10) {
      setError('Password must be between 5 and 10 characters.');
      return;
    }

    setSubmitting(true);
    try {
      await hospitalApi.register({
        name: form.name.trim(),
        email: form.email.trim(),
        phone: form.phone,
        password: form.password,
      });
      toast('Your account is ready. Please sign in.');
      navigate('/login', { replace: true });
    } catch (requestError) {
      setError(getErrorMessage(requestError));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Create your account"
      subtitle="Register as a patient to get started."
      footer={<>Already registered? <Link className="font-semibold text-teal-700 hover:underline" to="/login">Sign in</Link></>}
    >
      <form className="space-y-4" onSubmit={handleSubmit} noValidate>
        {error && <p className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800" role="alert">{error}</p>}
        <div>
          <label className="label" htmlFor="name">Full name</label>
          <input className="field" id="name" autoComplete="name" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} minLength={4} required />
          <p className="mt-1 text-xs text-muted">At least 4 characters.</p>
        </div>
        <div>
          <label className="label" htmlFor="email">Email address</label>
          <input className="field" id="email" type="email" autoComplete="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required />
        </div>
        <div>
          <label className="label" htmlFor="phone">Phone number</label>
          <input className="field" id="phone" type="tel" inputMode="numeric" autoComplete="tel" value={form.phone} onChange={(event) => setForm({ ...form, phone: event.target.value })} pattern="[0-9]{10}" maxLength={10} required />
          <p className="mt-1 text-xs text-muted">Enter 10 digits, with no spaces or punctuation.</p>
        </div>
        <div>
          <label className="label" htmlFor="password">Password</label>
          <input className="field" id="password" type="password" autoComplete="new-password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} minLength={5} maxLength={10} required />
          <p className="mt-1 text-xs text-muted">Use 5–10 characters.</p>
        </div>
        <button className="primary-button mt-2 w-full" type="submit" disabled={submitting}>
          {submitting ? <Spinner label="Creating account" small /> : 'Create account'}
        </button>
      </form>
    </AuthShell>
  );
}
