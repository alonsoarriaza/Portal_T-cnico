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
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface SidebarItem {
  name: string;
  path: string;
  icon: React.ReactNode;
  permission?: string;
  role?: string;
}

export const Sidebar: React.FC = () => {
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

  return (
    <aside className="w-64 bg-white border-r border-slate-200 flex flex-col shrink-0 min-h-screen select-none">
      {/* Brand Header with clickable Link to Dashboard */}
      <Link
        to="/dashboard"
        className="h-16 flex items-center gap-3 px-6 border-b border-slate-200 bg-white hover:bg-slate-50/80 transition-colors cursor-pointer group"
        title="Ir al Dashboard de ABAXIAL"
      >
        <div className="w-10 h-10 rounded-xl bg-brand-600 group-hover:bg-brand-700 flex items-center justify-center text-white shadow-sm font-black text-xl transition-transform group-hover:scale-105">
          A
        </div>
        <div>
          <span className="font-black text-base tracking-tight text-slate-900 block leading-tight group-hover:text-brand-700 transition-colors">
            ABAXIAL
          </span>
          <span className="text-[11px] uppercase font-bold tracking-widest text-brand-600 block">
            Portal Técnico
          </span>
        </div>
      </Link>

      {/* Navigation Links */}
      <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
        <div className="px-3 pb-2 text-xs font-bold uppercase tracking-wider text-slate-400">
          Menú Principal
        </div>
        {filteredNav.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
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
      <div className="p-4 border-t border-slate-200 bg-slate-50/60 text-xs font-medium text-slate-500 flex items-center justify-between">
        <span>Abaxial v1.0</span>
        <div className="flex items-center gap-1.5 text-emerald-700 font-semibold text-[11px]">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
          <span>En línea</span>
        </div>
      </div>
    </aside>
  );
};
