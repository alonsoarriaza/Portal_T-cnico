import React from 'react';
import { LogOut, User, Shield } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { Badge } from '../common/Badge';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6 shrink-0 shadow-sm">
      <div className="flex items-center gap-3">
        <h2 className="text-sm font-semibold text-slate-800 hidden sm:block">
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
          {user?.roles?.map((r) => (
            <Badge key={r} variant={r === 'SUPER_ADMIN' ? 'brand' : 'slate'} size="sm">
              {r}
            </Badge>
          ))}
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
