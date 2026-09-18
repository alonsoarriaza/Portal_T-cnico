import React from 'react';
import { NavLink, Link } from 'react-router-dom';
import {
  LayoutDashboard,
  Users2,
  HardDrive,
  Briefcase,
  Globe,
  FileText,
  Calendar,
  History,
  ShieldCheck,
  X,
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface SidebarProps {
  mobileOpen?: boolean;
  onCloseMobile?: () => void;
}

interface SidebarItem {
  name: string;
  path: string;
  icon: React.ReactNode;
  permission?: string;
  role?: string;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen, onCloseMobile }) => {
  const { hasPermission, hasRole } = useAuth();

  const navigation: SidebarItem[] = [
    { name: 'Dashboard', path: '/dashboard', icon: <LayoutDashboard className="w-5 h-5" /> },
    {
      name: 'Clientes',
      path: '/clientes',
      icon: <Users2 className="w-5 h-5" />,
      permission: 'CLIENTE_VER',
    },
    {
      name: 'Equipos',
      path: '/equipos',
      icon: <HardDrive className="w-5 h-5" />,
      permission: 'EQUIPO_VER',
    },
    {
      name: 'Servicios',
      path: '/servicios',
      icon: <Briefcase className="w-5 h-5" />,
      permission: 'SERVICIO_VER',
    },
    {
      name: 'Sitios Web',
      path: '/webs',
      icon: <Globe className="w-5 h-5" />,
      permission: 'WEB_VER',
    },
    {
      name: 'Documentación',
      path: '/documentos',
      icon: <FileText className="w-5 h-5" />,
      permission: 'DOCUMENTO_VER',
    },
    {
      name: 'Cronograma',
      path: '/cronograma',
      icon: <Calendar className="w-5 h-5" />,
      permission: 'CRONOGRAMA_VER',
    },
    {
      name: 'Auditoría',
      path: '/auditoria',
      icon: <History className="w-5 h-5" />,
      permission: 'HISTORIAL_VER',
    },
    {
      name: 'Usuarios y Roles',
      path: '/usuarios',
      icon: <ShieldCheck className="w-5 h-5" />,
      permission: 'USUARIO_VER',
    },
  ];

  const filteredNav = navigation.filter((item) => {
    if (item.role && !hasRole(item.role)) return false;
    if (item.permission && !hasPermission(item.permission)) return false;
    return true;
  });

  const renderNavContent = (isMobile = false) => (
    <>
      {/* Brand Header with clickable Link to Dashboard */}
      <div className="h-16 flex items-center justify-between px-5 border-b border-slate-200 bg-white">
        <Link
          to="/dashboard"
          onClick={isMobile ? onCloseMobile : undefined}
          className="flex items-center gap-3 hover:opacity-95 transition-opacity cursor-pointer group"
          title="Ir al Dashboard de Coanda"
        >
          <div className="w-10 h-10 rounded-xl bg-slate-50 border border-slate-200/80 flex items-center justify-center p-1 shadow-sm transition-transform group-hover:scale-105 overflow-hidden">
            <img
              src="/logo-coanda.png"
              alt="Coanda"
              className="w-full h-full object-contain"
            />
          </div>
          <div>
            <span className="font-black text-base tracking-tight text-slate-900 block leading-tight group-hover:text-brand-700 transition-colors">
              Coanda
            </span>
            <span className="text-[10px] uppercase font-bold tracking-widest text-brand-600 block">
              Portal Técnico
            </span>
          </div>
        </Link>

        {isMobile && (
          <button
            type="button"
            onClick={onCloseMobile}
            className="p-1.5 rounded-xl text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
            aria-label="Cerrar menú"
          >
            <X className="w-5 h-5" />
          </button>
        )}
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
        <div className="px-3 pb-2 text-xs font-bold uppercase tracking-wider text-slate-400">
          Menú Principal
        </div>
        {filteredNav.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            onClick={isMobile ? onCloseMobile : undefined}
            className={({ isActive }) =>
              `flex items-center gap-3.5 px-3.5 py-2.5 text-sm font-semibold rounded-xl transition-all ${
                isActive
                  ? 'bg-brand-50 text-brand-700 font-bold shadow-sm'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`
            }
          >
            {item.icon}
            <span>{item.name}</span>
          </NavLink>
        ))}
      </nav>

      {/* Footer Branding */}
      <div className="p-3.5 border-t border-slate-200 bg-slate-50/70 space-y-2.5">
        <div className="flex items-center gap-2.5">
          <div className="w-6 h-6 rounded-full bg-gradient-to-tr from-brand-600 to-emerald-500 flex items-center justify-center text-white font-black text-[10px] shadow-xs shrink-0">
            AF
          </div>
          <div className="text-[11px] leading-tight text-slate-600 min-w-0">
            <span className="block text-[9px] text-slate-400 font-semibold uppercase tracking-wider">Creada y gestionada por</span>
            <span className="font-bold text-slate-800 truncate block">Alonso Feria Arriaza</span>
          </div>
        </div>
        <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1.5 border-t border-slate-200/60">
          <span className="font-medium text-[10px] text-slate-400">Portal Coanda v1.0</span>
          <div className="flex items-center gap-1.5 text-emerald-700 font-semibold text-[10px]">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span>En línea</span>
          </div>
        </div>
      </div>
    </>
  );

  return (
    <>
      {/* Desktop Permanent Sidebar */}
      <aside className="hidden lg:flex w-64 bg-white border-r border-slate-200 flex-col shrink-0 min-h-screen select-none">
        {renderNavContent(false)}
      </aside>

      {/* Mobile Drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 lg:hidden">
          {/* Backdrop */}
          <div
            className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs transition-opacity animate-in fade-in duration-200"
            onClick={onCloseMobile}
          />
          {/* Drawer Sidebar */}
          <aside className="relative flex flex-col w-72 max-w-[85vw] h-full bg-white shadow-2xl z-50 animate-in slide-in-from-left duration-200 select-none">
            {renderNavContent(true)}
          </aside>
        </div>
      )}
    </>
  );
};
