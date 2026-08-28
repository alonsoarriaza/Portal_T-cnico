import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Users2,
  HardDrive,
  Briefcase,
  FileText,
  Plus,
  ArrowRight,
  Clock,
  Calendar,
  Globe,
  ShieldCheck,
  RotateCw,
  AlertCircle,
} from 'lucide-react';
import { dashboardApi, eventosApi, auditoriaApi } from '../api/services';
import { DashboardStats, Evento, AuditoriaItem } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [proximosEventos, setProximosEventos] = useState<Evento[]>([]);
  const [logsRecientes, setLogsRecientes] = useState<AuditoriaItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const { user, hasPermission } = useAuth();
  const { error, success } = useToast();
  const navigate = useNavigate();

  const loadDashboardData = useCallback(async (showToast = false) => {
    try {
      if (showToast) setRefreshing(true);
      else setLoading(true);

      const [statsData, eventsData, logsData] = await Promise.all([
        dashboardApi.getStats(),
        eventosApi.list({ estado: 'PENDIENTE' }),
        auditoriaApi.getRecent(),
      ]);
      setStats(statsData);
      setProximosEventos(eventsData.slice(0, 6));
      setLogsRecientes(logsData.slice(0, 6));
      if (showToast) success('Datos actualizados en tiempo real');
    } catch (err) {
      error('Error al cargar datos del panel principal');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [error, success]);

  useEffect(() => {
    loadDashboardData();
  }, [loadDashboardData]);

  if (loading && !stats) {
    return <LoadingSpinner message="Cargando panel principal..." />;
  }

  const statCards = [
    {
      title: 'Clientes Activos',
      subtitle: `${stats?.clientesInactivos || 0} inactivos / ${stats?.totalClientes || 0} total`,
      value: stats?.clientesActivos ?? 0,
      icon: <Users2 className="w-6 h-6 text-brand-600" />,
      bgIcon: 'bg-brand-50 border-brand-100',
      path: '/clientes',
    },
    {
      title: 'Equipos Registrados',
      subtitle: 'Inventario técnico total',
      value: stats?.totalEquipos ?? 0,
      icon: <HardDrive className="w-6 h-6 text-sky-600" />,
      bgIcon: 'bg-sky-50 border-sky-100',
      path: '/equipos',
    },
    {
      title: 'Servicios y Mantenimientos',
      subtitle: 'Contratos activos',
      value: stats?.totalServicios ?? 0,
      icon: <Briefcase className="w-6 h-6 text-indigo-600" />,
      bgIcon: 'bg-indigo-50 border-indigo-100',
      path: '/servicios',
    },
    {
      title: 'Sitios Web Gestionados',
      subtitle: 'Dominios y servidores',
      value: stats?.totalWebs ?? 0,
      icon: <Globe className="w-6 h-6 text-cyan-600" />,
      bgIcon: 'bg-cyan-50 border-cyan-100',
      path: '/webs',
    },
    {
      title: 'Documentos Técnicos',
      subtitle: 'Archivos y versiones',
      value: stats?.totalDocumentos ?? 0,
      icon: <FileText className="w-6 h-6 text-emerald-600" />,
      bgIcon: 'bg-emerald-50 border-emerald-100',
      path: '/documentos',
    },
    {
      title: 'Eventos Pendientes',
      subtitle: 'Intervenciones programadas',
      value: stats?.eventosPendientes ?? 0,
      icon: <Calendar className="w-6 h-6 text-amber-600" />,
      bgIcon: 'bg-amber-50 border-amber-100',
      path: '/cronograma',
    },
    {
      title: 'Usuarios del Sistema',
      subtitle: 'Técnicos y administradores',
      value: stats?.totalUsuarios ?? 0,
      icon: <ShieldCheck className="w-6 h-6 text-purple-600" />,
      bgIcon: 'bg-purple-50 border-purple-100',
      path: '/usuarios',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            ¡Hola, {user?.nombreCompleto || user?.username}!
          </h1>
          <p className="text-sm font-medium text-slate-500 mt-1">
            Panel de control centralizado de <span className="font-semibold text-brand-600">ABAXIAL Portal Técnico</span>.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            size="md"
            onClick={() => loadDashboardData(true)}
            isLoading={refreshing}
            leftIcon={<RotateCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />}
            title="Refrescar estadísticas desde la base de datos"
          >
            Actualizar
          </Button>
          {hasPermission('CLIENTE_CREAR') && (
            <Button
              variant="primary"
              size="md"
              onClick={() => navigate('/clientes')}
              leftIcon={<Plus className="w-5 h-5" />}
            >
              Nuevo Cliente
            </Button>
          )}
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {statCards.map((card, i) => (
          <div
            key={i}
            onClick={() => navigate(card.path)}
            className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:border-slate-300 hover:shadow-md transition-all cursor-pointer flex items-center justify-between group"
          >
            <div className="space-y-1">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400 block">
                {card.title}
              </span>
              <span className="text-3xl font-black text-slate-900 group-hover:text-brand-600 transition-colors">
                {card.value}
              </span>
              <span className="text-xs font-medium text-slate-500 block">
                {card.subtitle}
              </span>
            </div>
            <div className={`w-12 h-12 rounded-xl border flex items-center justify-center shadow-xs transition-transform group-hover:scale-110 ${card.bgIcon}`}>
              {card.icon}
            </div>
          </div>
        ))}
      </div>

      {/* 2-Column Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Próximas Intervenciones */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg bg-amber-50 border border-amber-100 flex items-center justify-center">
                <Calendar className="w-4 h-4 text-amber-600" />
              </div>
              <span>Próximas Intervenciones</span>
            </h3>
            <button
              onClick={() => navigate('/cronograma')}
              className="text-sm font-semibold text-brand-600 hover:text-brand-700 hover:underline inline-flex items-center gap-1.5"
            >
              Ver calendario <ArrowRight className="w-4 h-4" />
            </button>
          </div>

          {proximosEventos.length === 0 ? (
            <div className="py-8 text-center space-y-2">
              <AlertCircle className="w-8 h-8 text-slate-300 mx-auto" />
              <p className="text-sm font-medium text-slate-500">
                No hay intervenciones pendientes programadas.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {proximosEventos.map((ev) => (
                <div
                  key={ev.id}
                  onClick={() => navigate('/cronograma')}
                  className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 hover:border-slate-200 hover:bg-slate-100/70 transition-colors cursor-pointer flex items-center justify-between"
                >
                  <div className="space-y-1">
                    <h4 className="text-sm font-bold text-slate-900">{ev.titulo}</h4>
                    <p className="text-xs font-medium text-slate-500 flex items-center gap-2">
                      <Clock className="w-3.5 h-3.5 text-slate-400" />
                      <span>{format(new Date(ev.fechaInicio), 'dd/MM/yyyy HH:mm', { locale: es })}</span>
                      {ev.clienteNombre && (
                        <>
                          <span className="text-slate-300">•</span>
                          <span className="text-slate-700 font-semibold">{ev.clienteNombre}</span>
                        </>
                      )}
                    </p>
                  </div>
                  <Badge
                    variant={
                      ev.prioridad === 'URGENTE'
                        ? 'rose'
                        : ev.prioridad === 'ALTA'
                        ? 'amber'
                        : ev.prioridad === 'MEDIA'
                        ? 'brand'
                        : 'slate'
                    }
                    size="md"
                  >
                    {ev.prioridad}
                  </Badge>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Auditoría Reciente */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center">
                <Clock className="w-4 h-4 text-slate-700" />
              </div>
              <span>Actividad Reciente del Sistema</span>
            </h3>
            <button
              onClick={() => navigate('/auditoria')}
              className="text-sm font-semibold text-brand-600 hover:text-brand-700 hover:underline inline-flex items-center gap-1.5"
            >
              Ver registro completo <ArrowRight className="w-4 h-4" />
            </button>
          </div>

          {logsRecientes.length === 0 ? (
            <div className="py-8 text-center space-y-2">
              <AlertCircle className="w-8 h-8 text-slate-300 mx-auto" />
              <p className="text-sm font-medium text-slate-500">
                Sin actividad registrada aún.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {logsRecientes.map((log) => (
                <div
                  key={log.id}
                  className="p-3.5 rounded-xl bg-slate-50 border border-slate-100 flex items-center justify-between gap-4"
                >
                  <div className="space-y-0.5 overflow-hidden">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-bold text-slate-900">{log.username}</span>
                      <span className="text-xs font-semibold px-2 py-0.5 rounded bg-slate-200/70 text-slate-700 font-mono">
                        {log.accion}
                      </span>
                    </div>
                    <p className="text-xs text-slate-600 truncate max-w-sm">{log.detalles}</p>
                  </div>
                  <span className="text-xs font-medium text-slate-400 shrink-0 font-mono">
                    {format(new Date(log.fecha), 'HH:mm:ss')}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
