import { Link } from 'react-router-dom';

export default function AuthShell({ title, subtitle, children, footer }) {
  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-10 sm:px-6">
      <div className="grid w-full max-w-5xl overflow-hidden rounded-3xl bg-white shadow-card md:grid-cols-[0.9fr_1.1fr]">
        <section className="hidden flex-col justify-between bg-teal-700 p-10 text-white md:flex">
          <Link to="/login" className="flex items-center gap-3 text-lg font-bold">
            <span className="grid h-10 w-10 place-items-center rounded-xl bg-white/15 text-2xl">+</span>
            Carewell
          </Link>
          <div className="py-12">
            <p className="mb-4 text-sm font-semibold uppercase tracking-[0.18em] text-teal-100">Care made simpler</p>
            <h2 className="max-w-sm text-4xl font-semibold leading-tight">A healthier day starts with a little peace of mind.</h2>
            <p className="mt-5 max-w-sm leading-7 text-teal-100">Manage your appointments and connect with your care team in one calm, secure place.</p>
          </div>
          <p className="text-sm text-teal-100">A welcoming space for your healthcare journey.</p>
        </section>
        <section className="p-6 sm:p-10 lg:p-12">
          <div className="mb-8 md:hidden">
            <Link to="/login" className="inline-flex items-center gap-2 font-bold text-teal-700">
              <span className="grid h-9 w-9 place-items-center rounded-xl bg-teal-600 text-xl text-white">+</span> Carewell
            </Link>
          </div>
          <p className="text-sm font-semibold text-teal-600">PATIENT PORTAL</p>
          <h1 className="mt-2 text-3xl font-semibold tracking-tight text-ink">{title}</h1>
          <p className="mt-2 text-sm leading-6 text-muted">{subtitle}</p>
          <div className="mt-8">{children}</div>
          <div className="mt-7 border-t border-slate-100 pt-5 text-center text-sm text-muted">{footer}</div>
        </section>
      </div>
    </main>
  );
}
