import React from 'react';
import { LogOut, User, Shield, Menu } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { Badge } from '../common/Badge';

interface NavbarProps {
  onOpenMobile?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenMobile }) => {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-4 sm:px-6 shrink-0 shadow-sm">
      <div className="flex items-center gap-3">
        {/* Mobile Hamburger Toggle */}
        <button
          type="button"
          onClick={onOpenMobile}
          className="lg:hidden p-2 -ml-1 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-xl transition-colors"
          aria-label="Abrir menú de navegación"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Mobile Header Brand */}
        <div className="flex items-center gap-2 lg:hidden">
          <img src="/logo-coanda.png" alt="Coanda" className="h-7 w-7 object-contain rounded" />
          <span className="font-black text-sm tracking-tight text-slate-900">Coanda</span>
        </div>

        <h2 className="text-sm font-semibold text-slate-800 hidden lg:block">
          Portal de Gestión Técnica
        </h2>
      </div>

      <div className="flex items-center gap-4">
        {/* User Info */}
        <div className="flex items-center gap-3 border-r border-slate-200 pr-4">
          <div className="w-8 h-8 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-600 font-bold text-xs">
            {user?.nombreCompleto?.charAt(0) || user?.username?.charAt(0) || <User className="w-4 h-4" />}
          </div>
          <div className="hidden sm:block text-left">
            <span className="text-xs font-bold text-slate-900 block leading-tight">
              {user?.nombreCompleto || user?.username}
            </span>
            <span className="text-[11px] text-slate-500 block leading-none">
              {user?.email}
            </span>
          </div>
          <div className="hidden md:flex items-center gap-1">
            {user?.roles?.map((r) => (
              <Badge key={r} variant={r === 'SUPER_ADMIN' ? 'brand' : 'slate'} size="sm">
                {r}
              </Badge>
            ))}
          </div>
        </div>

        {/* Logout Button */}
        <button
          onClick={logout}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-slate-600 hover:text-rose-600 hover:bg-rose-50 transition-colors"
          title="Cerrar sesión segura"
        >
          <LogOut className="w-4 h-4" />
          <span className="hidden sm:inline">Salir</span>
        </button>
      </div>
    </header>
  );
};
