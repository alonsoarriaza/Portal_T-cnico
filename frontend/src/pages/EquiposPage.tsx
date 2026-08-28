import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { HardDrive, Search, Building, RefreshCw, CheckCircle2 } from 'lucide-react';
import { equiposApi } from '../api/services';
import { Equipo } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';

export const EquiposPage: React.FC = () => {
  const [equipos, setEquipos] = useState<Equipo[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState<number | null>(null);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const [search, setSearch] = useState('');
  const [tipo, setTipo] = useState('');
  const [estado, setEstado] = useState('');

  const { hasPermission } = useAuth();
  const { success, error } = useToast();
  const navigate = useNavigate();

  const loadEquipos = useCallback(async () => {
    try {
      setLoading(true);
      const data = await equiposApi.list({
        search: search || undefined,
        tipo: tipo || undefined,
        estado: estado || undefined,
        page,
        size,
      });
      setEquipos(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar el inventario de equipos');
    } finally {
      setLoading(false);
    }
  }, [search, tipo, estado, page, size, error]);

  useEffect(() => {
    loadEquipos();
  }, [loadEquipos]);

  const handleEstadoChange = async (equipoId: number, nuevoEstado: string, e: React.MouseEvent | React.ChangeEvent<HTMLSelectElement>) => {
    e.stopPropagation();
    try {
      setUpdatingId(equipoId);
      await equiposApi.cambiarEstado(equipoId, nuevoEstado);
      setEquipos((prev) =>
        prev.map((eq) => (eq.id === equipoId ? { ...eq, estado: nuevoEstado } : eq))
      );
      success(`Estado del equipo actualizado a ${nuevoEstado}`);
    } catch (err) {
      error('Error al actualizar el estado del equipo');
    } finally {
      setUpdatingId(null);
    }
  };

  const getEstadoBadgeVariant = (st: string) => {
    switch (st) {
      case 'OPERATIVO':
        return 'emerald';
      case 'EN_REPARACION':
        return 'amber';
      case 'OBSOLETO':
        return 'slate';
      case 'BAJA':
      default:
        return 'rose';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2.5 tracking-tight">
            <div className="w-9 h-9 rounded-xl bg-sky-50 border border-sky-100 flex items-center justify-center text-sky-600">
              <HardDrive className="w-5 h-5" />
            </div>
            <span>Inventario de Equipos</span>
          </h1>
          <p className="text-sm font-medium text-slate-500 mt-1">
            Consulta, gestión y actualización de estado en tiempo real del parque informático.
          </p>
        </div>
      </div>

      <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm grid grid-cols-1 sm:grid-cols-3 gap-3">
        <Input
          placeholder="Buscar por código, marca, modelo, serie o cliente..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
          leftIcon={<Search className="w-4 h-4" />}
        />
        <Select value={tipo} onChange={(e) => { setTipo(e.target.value); setPage(0); }}>
          <option value="">Todos los Tipos</option>
          <option value="PC Sobremesa">PC Sobremesa</option>
          <option value="Portátil">Portátil</option>
          <option value="Servidor">Servidor</option>
          <option value="Router / Switch">Router / Switch</option>
          <option value="Impresora">Impresora</option>
          <option value="SAI / UPS">SAI / UPS</option>
        </Select>
        <Select value={estado} onChange={(e) => { setEstado(e.target.value); setPage(0); }}>
          <option value="">Todos los Estados</option>
          <option value="OPERATIVO">Operativo</option>
          <option value="EN_REPARACION">En Reparación</option>
          <option value="OBSOLETO">Obsoleto</option>
          <option value="BAJA">Baja</option>
        </Select>
      </div>

      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando equipos..." />
        ) : equipos.length === 0 ? (
          <EmptyState
            title="No hay equipos registrados"
            description="No se han encontrado equipos con los filtros seleccionados."
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-700">
              <thead className="bg-slate-50/80 border-b border-slate-200 text-xs font-bold uppercase tracking-wider text-slate-500">
                <tr>
                  <th className="px-5 py-3.5">Cód. Inventario</th>
                  <th className="px-5 py-3.5">Cliente</th>
                  <th className="px-5 py-3.5">Tipo</th>
                  <th className="px-5 py-3.5">Marca / Modelo</th>
                  <th className="px-5 py-3.5">Nº Serie</th>
                  <th className="px-5 py-3.5">Estado (Acción Rápida)</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {equipos.map((eq) => (
                  <tr
                    key={eq.id}
                    onClick={() => navigate(`/clientes/${eq.clienteId}`)}
                    className="hover:bg-slate-50/80 transition-colors cursor-pointer group"
                  >
                    <td className="px-5 py-4 font-mono font-bold text-brand-600 text-xs">
                      {eq.codigoInventario}
                    </td>
                    <td className="px-5 py-4 font-semibold text-slate-900">
                      <div className="flex items-center gap-1.5">
                        <Building className="w-4 h-4 text-slate-400 shrink-0" />
                        <span>{eq.clienteNombre}</span>
                      </div>
                    </td>
                    <td className="px-5 py-4 text-slate-700 font-medium">{eq.tipo}</td>
                    <td className="px-5 py-4 text-slate-900 font-semibold">{eq.marca} {eq.modelo}</td>
                    <td className="px-5 py-4 font-mono text-xs text-slate-500">{eq.numeroSerie || '-'}</td>
                    <td className="px-5 py-4" onClick={(e) => e.stopPropagation()}>
                      {hasPermission('EQUIPO_EDITAR') ? (
                        <div className="relative inline-block w-40">
                          <select
                            value={eq.estado}
                            disabled={updatingId === eq.id}
                            onChange={(e) => handleEstadoChange(eq.id, e.target.value, e)}
                            className={`w-full px-2.5 py-1.5 text-xs font-bold rounded-xl border transition-all cursor-pointer shadow-2xs focus:outline-none focus:ring-2 focus:ring-brand-500/20 ${
                              eq.estado === 'OPERATIVO'
                                ? 'bg-emerald-50 text-emerald-800 border-emerald-200 hover:bg-emerald-100/70'
                                : eq.estado === 'EN_REPARACION'
                                ? 'bg-amber-50 text-amber-800 border-amber-200 hover:bg-amber-100/70'
                                : eq.estado === 'OBSOLETO'
                                ? 'bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200/70'
                                : 'bg-rose-50 text-rose-800 border-rose-200 hover:bg-rose-100/70'
                            }`}
                          >
                            <option value="OPERATIVO" className="bg-white text-slate-900 font-medium">● Operativo</option>
                            <option value="EN_REPARACION" className="bg-white text-slate-900 font-medium">▲ En Reparación</option>
                            <option value="OBSOLETO" className="bg-white text-slate-900 font-medium">■ Obsoleto</option>
                            <option value="BAJA" className="bg-white text-slate-900 font-medium">✖ Baja</option>
                          </select>
                          {updatingId === eq.id && (
                            <div className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none">
                              <RefreshCw className="w-3.5 h-3.5 animate-spin text-brand-600" />
                            </div>
                          )}
                        </div>
                      ) : (
                        <Badge variant={getEstadoBadgeVariant(eq.estado)} size="sm">
                          {eq.estado}
                        </Badge>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div className="px-5 border-t border-slate-100">
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
    </div>
  );
};
