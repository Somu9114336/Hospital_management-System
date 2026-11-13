import { useState } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const navItems = [
  { to: '/appointments', label: 'My appointments' },
  { to: '/doctors', label: 'Find a doctor' },
  { to: '/book', label: 'Book appointment' },
];

export default function AppLayout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  function signOut() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-30 border-b border-teal-100/80 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 sm:px-6 lg:px-8">
          <Link to="/appointments" className="flex items-center gap-3 rounded-lg" aria-label="Carewell patient portal home">
            <span className="grid h-10 w-10 place-items-center rounded-xl bg-teal-600 text-lg font-bold text-white" aria-hidden="true">+</span>
            <span>
              <span className="block text-base font-bold tracking-tight text-ink">Carewell</span>
              <span className="block text-xs text-muted">Patient portal</span>
            </span>
          </Link>

          <button
            type="button"
            className="grid h-11 w-11 place-items-center rounded-xl border border-slate-200 text-xl text-ink md:hidden"
            aria-label={menuOpen ? 'Close navigation menu' : 'Open navigation menu'}
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((open) => !open)}
          >
            <span aria-hidden="true">{menuOpen ? '×' : '☰'}</span>
          </button>

          <nav className="hidden items-center gap-1 md:flex" aria-label="Main navigation">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) => `rounded-lg px-3 py-2 text-sm font-semibold transition ${
                  isActive ? 'bg-teal-50 text-teal-700' : 'text-muted hover:bg-slate-50 hover:text-ink'
                }`}
              >
                {item.label}
              </NavLink>
            ))}
            <span className="mx-2 h-7 border-l border-slate-200" aria-hidden="true" />
            <span className="max-w-36 truncate text-sm text-muted" title={user?.name}>{user?.name}</span>
            <button type="button" className="secondary-button ml-2 min-h-10 px-3" onClick={signOut}>Log out</button>
          </nav>
        </div>

        {menuOpen && (
          <nav className="border-t border-slate-100 bg-white px-4 py-3 md:hidden" aria-label="Mobile navigation">
            <div className="mx-auto flex max-w-7xl flex-col gap-1">
              {navItems.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  onClick={() => setMenuOpen(false)}
                  className={({ isActive }) => `rounded-lg px-3 py-3 text-sm font-semibold ${
                    isActive ? 'bg-teal-50 text-teal-700' : 'text-muted'
                  }`}
                >
                  {item.label}
                </NavLink>
              ))}
              <div className="mt-2 flex items-center justify-between border-t border-slate-100 pt-3">
                <span className="truncate pr-3 text-sm text-muted">{user?.name}</span>
                <button type="button" className="secondary-button min-h-10 px-3" onClick={signOut}>Log out</button>
              </div>
            </div>
          </nav>
        )}
      </header>

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 sm:py-10 lg:px-8" key={location.pathname}>
        <Outlet />
      </main>
      <footer className="mx-auto max-w-7xl px-4 pb-8 text-center text-xs text-muted sm:px-6 lg:px-8">
        Your care, all in one place.
      </footer>
    </div>
  );
}
