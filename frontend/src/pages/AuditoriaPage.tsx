import React, { useState, useEffect, useCallback } from 'react';
import {
  History,
  Search,
  Shield,
  Eye,
  Calendar,
  Filter,
  ArrowRight,
  User,
  Activity,
  Layers,
} from 'lucide-react';
import { auditoriaApi } from '../api/services';
import { AuditoriaItem } from '../types';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { Button } from '../components/common/Button';
import { format } from 'date-fns';

export const AuditoriaPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditoriaItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(15);

  // Filters
  const [search, setSearch] = useState('');
  const [accion, setAccion] = useState('');
  const [entidad, setEntidad] = useState('');
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');

  const [selectedLog, setSelectedLog] = useState<AuditoriaItem | null>(null);

  const { error } = useToast();

  const loadLogs = useCallback(async () => {
    try {
      setLoading(true);
      const data = await auditoriaApi.list({
        search: search || undefined,
        accion: accion || undefined,
        entidad: entidad || undefined,
        desde: desde ? `${desde}T00:00:00` : undefined,
        hasta: hasta ? `${hasta}T23:59:59` : undefined,
        page,
        size,
      });
      setLogs(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar registros de auditoría');
    } finally {
      setLoading(false);
    }
  }, [search, accion, entidad, desde, hasta, page, size, error]);

  useEffect(() => {
    loadLogs();
  }, [loadLogs]);

  const handleResetFilters = () => {
    setSearch('');
    setAccion('');
    setEntidad('');
    setDesde('');
    setHasta('');
    setPage(0);
  };

  // Helper to parse "Campo: antes 'X' -> después 'Y'" diff items
  const parseDiffs = (detalles?: string) => {
    if (!detalles) return [];
    if (!detalles.includes('Cambios:')) return [];

    const parts = detalles.split('Cambios:');
    if (parts.length < 2) return [];

    const changesText = parts[1].trim();
    const rawItems = changesText.split('|');

    return rawItems
      .map((item) => {
        const trimmed = item.trim();
        // Regex: Field: antes 'old' -> después 'new'
        const match = trimmed.match(/^(.*?):\s*antes\s*'(.*?)'\s*->\s*despu[eé]s\s*'(.*?)'$/i);
        if (match) {
          return {
            field: match[1].trim(),
            oldVal: match[2],
            newVal: match[3],
          };
        }
        return { field: trimmed, oldVal: '', newVal: '' };
      })
      .filter((d) => d.field);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2.5 tracking-tight">
            <div className="w-9 h-9 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-700">
              <History className="w-5 h-5" />
            </div>
            <span>Auditoría y Registro de Actividad</span>
          </h1>
          <p className="text-sm font-medium text-slate-500 mt-1">
            Trazabilidad completa de cambios, creaciones, accesos y operaciones en la plataforma.
          </p>
        </div>

        <div className="flex items-center gap-2 font-semibold text-xs text-slate-600 bg-slate-50 px-3.5 py-2 rounded-xl border border-slate-200">
          <Activity className="w-4 h-4 text-emerald-600" />
          <span>{totalElements} registros encontrados</span>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm space-y-3">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          <div className="relative lg:col-span-2">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Buscar por usuario, acción o detalles..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              className="w-full pl-9 pr-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white placeholder:text-slate-400 transition-all"
            />
          </div>

          <select
            value={accion}
            onChange={(e) => {
              setAccion(e.target.value);
              setPage(0);
            }}
            className="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all cursor-pointer"
          >
            <option value="" className="bg-white text-slate-900">Todas las Acciones</option>
            <option value="LOGIN" className="bg-white text-slate-900">LOGIN</option>
            <option value="LOGOUT" className="bg-white text-slate-900">LOGOUT</option>
            <option value="CLIENTE_CREADO" className="bg-white text-slate-900">CLIENTE_CREADO</option>
            <option value="CLIENTE_MODIFICADO" className="bg-white text-slate-900">CLIENTE_MODIFICADO</option>
            <option value="CLIENTE_DESACTIVADO" className="bg-white text-slate-900">CLIENTE_DESACTIVADO</option>
            <option value="EQUIPO_CREADO" className="bg-white text-slate-900">EQUIPO_CREADO</option>
            <option value="EQUIPO_MODIFICADO" className="bg-white text-slate-900">EQUIPO_MODIFICADO</option>
            <option value="EQUIPO_ESTADO_MODIFICADO" className="bg-white text-slate-900">EQUIPO_ESTADO_MODIFICADO</option>
            <option value="DOCUMENTO_SUBIDO" className="bg-white text-slate-900">DOCUMENTO_SUBIDO</option>
            <option value="DOCUMENTO_NUEVA_VERSION" className="bg-white text-slate-900">DOCUMENTO_NUEVA_VERSION</option>
            <option value="DOCUMENTO_DESCARGADO" className="bg-white text-slate-900">DOCUMENTO_DESCARGADO</option>
            <option value="USUARIO_CREADO" className="bg-white text-slate-900">USUARIO_CREADO</option>
            <option value="USUARIO_MODIFICADO" className="bg-white text-slate-900">USUARIO_MODIFICADO</option>
            <option value="USUARIO_DESACTIVADO" className="bg-white text-slate-900">USUARIO_DESACTIVADO</option>
          </select>

          <select
            value={entidad}
            onChange={(e) => {
              setEntidad(e.target.value);
              setPage(0);
            }}
            className="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all cursor-pointer"
          >
            <option value="" className="bg-white text-slate-900">Todas las Entidades</option>
            <option value="Usuario" className="bg-white text-slate-900">Usuario</option>
            <option value="Cliente" className="bg-white text-slate-900">Cliente</option>
            <option value="Contacto" className="bg-white text-slate-900">Contacto</option>
            <option value="Equipo" className="bg-white text-slate-900">Equipo</option>
            <option value="Servicio" className="bg-white text-slate-900">Servicio</option>
            <option value="Web" className="bg-white text-slate-900">Web</option>
            <option value="Documento" className="bg-white text-slate-900">Documento</option>
            <option value="Evento" className="bg-white text-slate-900">Evento</option>
          </select>

          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={handleResetFilters}
              className="w-full text-xs font-semibold shadow-none hover:bg-slate-100"
            >
              Limpiar filtros
            </Button>
          </div>
        </div>

        {/* Date Filters */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 pt-3 border-t border-slate-100">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-600 shrink-0">Desde:</span>
            <input
              type="date"
              value={desde}
              onChange={(e) => {
                setDesde(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-1.5 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all"
            />
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-600 shrink-0">Hasta:</span>
            <input
              type="date"
              value={hasta}
              onChange={(e) => {
                setHasta(e.target.value);
                setPage(0);
              }}
              className="w-full px-3 py-1.5 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all"
            />
          </div>
        </div>
      </div>

      {/* Audit Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando registros de auditoría..." />
        ) : logs.length === 0 ? (
          <EmptyState
            title="Sin registros"
            description="No se han encontrado eventos de auditoría para los filtros aplicados."
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-700">
              <thead className="bg-slate-50/80 border-b border-slate-200 text-xs font-extrabold text-slate-600 uppercase tracking-wider">
                <tr>
                  <th className="px-5 py-3.5">Fecha y Hora</th>
                  <th className="px-5 py-3.5">Usuario</th>
                  <th className="px-5 py-3.5">Acción</th>
                  <th className="px-5 py-3.5">Entidad</th>
                  <th className="px-5 py-3.5">Detalles del Cambio</th>
                  <th className="px-5 py-3.5">IP</th>
                  <th className="px-5 py-3.5 text-right">Ver</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {logs.map((log) => (
                  <tr
                    key={log.id}
                    onClick={() => setSelectedLog(log)}
                    className="hover:bg-slate-50/80 transition-colors cursor-pointer"
                  >
                    <td className="px-5 py-3.5 text-xs font-mono text-slate-500 whitespace-nowrap">
                      {format(new Date(log.fecha), 'dd/MM/yyyy HH:mm:ss')}
                    </td>
                    <td className="px-5 py-3.5 font-bold text-slate-900">
                      <div className="flex items-center gap-1.5">
                        <Shield className="w-4 h-4 text-slate-400" />
                        <span>{log.username}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <Badge
                        variant={
                          log.accion.includes('DESACTIVADO') || log.accion.includes('ELIMINADO')
                            ? 'rose'
                            : log.accion.includes('CREADO') || log.accion.includes('SUBIDO')
                            ? 'emerald'
                            : log.accion === 'LOGIN'
                            ? 'cyan'
                            : 'brand'
                        }
                        size="md"
                      >
                        {log.accion}
                      </Badge>
                    </td>
                    <td className="px-5 py-3.5 font-semibold text-slate-800">
                      <div className="flex items-center gap-1.5">
                        <Layers className="w-3.5 h-3.5 text-slate-400" />
                        <span>{log.entidad}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5 text-xs text-slate-600 max-w-md truncate">
                      {log.detalles}
                    </td>
                    <td className="px-5 py-3.5 text-xs font-mono text-slate-400">
                      {log.ip || '-'}
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedLog(log);
                        }}
                        className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
                        title="Ver detalle del registro"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div className="px-6 py-4 border-t border-slate-100">
          <Pagination
            page={page}
            totalPages={totalPages}
            totalElements={totalElements}
            size={size}
            onPageChange={setPage}
            onSizeChange={setSize}
          />
        </div>
      </div>

      {/* Modal: Detalle de Auditoría con Resaltado de Cambios */}
      <Modal
        isOpen={!!selectedLog}
        onClose={() => setSelectedLog(null)}
        title="Detalle del Registro de Auditoría"
        size="lg"
      >
        {selectedLog && (
          <div className="space-y-5">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 p-4 rounded-2xl bg-slate-50 border border-slate-200 text-xs">
              <div>
                <span className="font-bold uppercase tracking-wider text-slate-400 block mb-0.5">
                  Usuario responsable
                </span>
                <p className="text-sm font-black text-slate-900">{selectedLog.username}</p>
              </div>
              <div>
                <span className="font-bold uppercase tracking-wider text-slate-400 block mb-0.5">
                  Fecha y Hora
                </span>
                <p className="font-mono text-xs font-semibold text-slate-700">
                  {format(new Date(selectedLog.fecha), 'dd/MM/yyyy HH:mm:ss')}
                </p>
              </div>
              <div>
                <span className="font-bold uppercase tracking-wider text-slate-400 block mb-0.5">
                  Acción
                </span>
                <p className="text-sm font-bold text-brand-700">{selectedLog.accion}</p>
              </div>
              <div>
                <span className="font-bold uppercase tracking-wider text-slate-400 block mb-0.5">
                  Entidad y Registro ID
                </span>
                <p className="text-sm font-semibold text-slate-800">
                  {selectedLog.entidad} {selectedLog.entidadId ? `(ID: #${selectedLog.entidadId})` : ''}
                </p>
              </div>
              <div className="col-span-1 sm:col-span-2 pt-2 border-t border-slate-200">
                <span className="font-bold uppercase tracking-wider text-slate-400 block mb-0.5">
                  Dirección IP y Agente
                </span>
                <p className="font-mono text-xs text-slate-600">
                  IP: {selectedLog.ip || 'Localhost'} • {selectedLog.userAgent || 'Cliente de escritorio'}
                </p>
              </div>
            </div>

            {/* Diffs Breakdown or Details box */}
            <div>
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">
                Detalle de Cambios Realizados
              </h4>

              {parseDiffs(selectedLog.detalles).length > 0 ? (
                <div className="space-y-2">
                  {parseDiffs(selectedLog.detalles).map((diff, idx) => (
                    <div
                      key={idx}
                      className="p-3.5 rounded-xl border border-slate-200 bg-white shadow-xs space-y-1.5"
                    >
                      <div className="text-xs font-black text-slate-900 uppercase tracking-wide">
                        {diff.field}
                      </div>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                        <div className="p-2.5 rounded-lg bg-rose-50 border border-rose-100 text-rose-900">
                          <span className="font-bold uppercase text-[10px] text-rose-500 block">
                            Valor Anterior:
                          </span>
                          <span className="font-medium">{diff.oldVal || '(vacío)'}</span>
                        </div>
                        <div className="p-2.5 rounded-lg bg-emerald-50 border border-emerald-100 text-emerald-900">
                          <span className="font-bold uppercase text-[10px] text-emerald-600 block">
                            Valor Nuevo:
                          </span>
                          <span className="font-bold">{diff.newVal || '(vacío)'}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="p-4 rounded-xl bg-slate-100 border border-slate-200 text-xs text-slate-800 font-mono whitespace-pre-wrap leading-relaxed">
                  {selectedLog.detalles || 'Sin detalles registrados.'}
                </div>
              )}
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
