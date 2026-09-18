import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { HardDrive, Search, Building, RefreshCw, CheckCircle2, ExternalLink, Edit2 } from 'lucide-react';
import { equiposApi } from '../api/services';
import { Equipo, EquipoRequest } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Modal } from '../components/common/Modal';
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
  const [size, setSize] = useState<number | 'ALL'>(50);

  const [search, setSearch] = useState('');
  const [tipo, setTipo] = useState('');
  const [estado, setEstado] = useState('');

  const { hasPermission } = useAuth();
  const { success, error } = useToast();
  const navigate = useNavigate();

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedEquipo, setSelectedEquipo] = useState<Equipo | null>(null);
  const [editForm, setEditForm] = useState<EquipoRequest>({
    codigoInventario: '',
    nombreEquipo: '',
    tipo: 'Sobremesa',
    ubicacion: '',
    estado: 'Alta',
    ultimaRevision: '',
    url: '',
    marca: '',
    modelo: '',
    numeroSerie: '',
    observaciones: '',
  });
  const [isSaving, setIsSaving] = useState(false);

  const handleOpenEdit = (eq: Equipo) => {
    setSelectedEquipo(eq);
    setEditForm({
      codigoInventario: eq.codigoInventario || '',
      nombreEquipo: eq.nombreEquipo || '',
      tipo: eq.tipo || 'Sobremesa',
      ubicacion: eq.ubicacion || '',
      estado: eq.estado || 'Alta',
      ultimaRevision: eq.ultimaRevision || '',
      url: eq.url || '',
      marca: eq.marca || '',
      modelo: eq.modelo || '',
      numeroSerie: eq.numeroSerie || '',
      observaciones: eq.observaciones || '',
    });
    setIsEditModalOpen(true);
  };

  const handleSaveEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedEquipo) return;
    if (!editForm.tipo?.trim()) {
      error('El tipo de equipo es obligatorio');
      return;
    }

    try {
      setIsSaving(true);
      const updated = await equiposApi.update(selectedEquipo.id, editForm);
      setEquipos((prev) => prev.map((eq) => (eq.id === updated.id ? updated : eq)));
      success(`Equipo ${updated.codigoInventario} actualizado con éxito`);
      setIsEditModalOpen(false);
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al actualizar el equipo');
    } finally {
      setIsSaving(false);
    }
  };

  const loadEquipos = useCallback(async () => {
    try {
      setLoading(true);
      const isAll = size === 'ALL';
      const data = await equiposApi.list({
        search: search || undefined,
        tipo: tipo || undefined,
        estado: estado || undefined,
        page: isAll ? 0 : page,
        size: isAll ? 'ALL' : size,
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
    switch (st?.toUpperCase()) {
      case 'OPERATIVO':
      case 'ALTA':
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
            <span>Inventario de Equipos ({totalElements})</span>
          </h1>
          <p className="text-sm font-medium text-slate-500 mt-1">
            Consulta, gestión y actualización de estado en tiempo real del parque informático.
          </p>
        </div>
      </div>

      <div className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm grid grid-cols-1 sm:grid-cols-3 gap-3">
        <Input
          placeholder="Buscar por código, equipo, tipo, cliente..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
          leftIcon={<Search className="w-4 h-4" />}
        />
        <Select value={tipo} onChange={(e) => { setTipo(e.target.value); setPage(0); }}>
          <option value="">Todos los Tipos</option>
          <option value="Sobremesa">Sobremesa</option>
          <option value="Portátil">Portátil</option>
          <option value="Servidor">Servidor</option>
          <option value="Router / Switch">Router / Switch</option>
          <option value="Impresora">Impresora</option>
          <option value="SAI / UPS">SAI / UPS</option>
        </Select>
        <Select value={estado} onChange={(e) => { setEstado(e.target.value); setPage(0); }}>
          <option value="">Todos los Estados</option>
          <option value="Alta">Alta</option>
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
                  <th className="px-5 py-3.5">Ref / N/S</th>
                  <th className="px-5 py-3.5">Equipo</th>
                  <th className="px-5 py-3.5">Cliente</th>
                  <th className="px-5 py-3.5">Tipo</th>
                  <th className="px-5 py-3.5">Última Revisión</th>
                  <th className="px-5 py-3.5">Estado</th>
                  <th className="px-5 py-3.5 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {equipos.map((eq) => (
                  <tr
                    key={eq.id}
                    onClick={() => navigate(`/clientes/${eq.clienteId}`)}
                    className="hover:bg-slate-50/80 transition-colors cursor-pointer group"
                  >
                    <td className="px-5 py-4 font-mono font-bold text-slate-900 text-xs">
                      {eq.codigoInventario}
                    </td>
                    <td className="px-5 py-4 font-semibold text-slate-900" onClick={(e) => eq.url && e.stopPropagation()}>
                      {eq.url ? (
                        <a
                          href={eq.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="text-brand-600 hover:text-brand-800 underline inline-flex items-center gap-1 font-bold transition-colors"
                          title={`Abrir soporte: ${eq.url}`}
                        >
                          <span>{eq.nombreEquipo || eq.codigoInventario}</span>
                          <ExternalLink className="w-3 h-3 text-brand-500 shrink-0" />
                        </a>
                      ) : (
                        <span>{eq.nombreEquipo || eq.modelo || '-'}</span>
                      )}
                    </td>
                    <td className="px-5 py-4 font-semibold text-slate-900">
                      <div className="flex items-center gap-1.5">
                        <Building className="w-4 h-4 text-slate-400 shrink-0" />
                        <span className="truncate max-w-[220px]">{eq.clienteNombre}</span>
                      </div>
                    </td>
                    <td className="px-5 py-4 text-slate-700 font-medium">{eq.tipo}</td>
                    <td className="px-5 py-4 text-xs whitespace-nowrap">
                      <span className={eq.ultimaRevision && eq.ultimaRevision !== 'Sin acciones' ? 'text-slate-800 font-semibold' : 'text-slate-400 italic'}>
                        {eq.ultimaRevision || 'Sin acciones'}
                      </span>
                    </td>
                    <td className="px-5 py-4 whitespace-nowrap" onClick={(e) => e.stopPropagation()}>
                      {hasPermission('EQUIPO_EDITAR') ? (
                        <div className="relative inline-block w-36">
                          <select
                            value={eq.estado}
                            disabled={updatingId === eq.id}
                            onChange={(e) => handleEstadoChange(eq.id, e.target.value, e)}
                            className={`w-full px-2.5 py-1.5 text-xs font-bold rounded-xl border transition-all cursor-pointer shadow-2xs focus:outline-none focus:ring-2 focus:ring-brand-500/20 ${
                              eq.estado?.toUpperCase() === 'OPERATIVO' || eq.estado?.toUpperCase() === 'ALTA'
                                ? 'bg-emerald-50 text-emerald-800 border-emerald-200 hover:bg-emerald-100/70'
                                : eq.estado?.toUpperCase() === 'EN_REPARACION'
                                ? 'bg-amber-50 text-amber-800 border-amber-200 hover:bg-amber-100/70'
                                : eq.estado?.toUpperCase() === 'OBSOLETO'
                                ? 'bg-slate-100 text-slate-700 border-slate-300 hover:bg-slate-200/70'
                                : 'bg-rose-50 text-rose-800 border-rose-200 hover:bg-rose-100/70'
                            }`}
                          >
                            <option value="Alta" className="bg-white text-slate-900 font-medium">● Alta</option>
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
                    <td className="px-5 py-4 text-right whitespace-nowrap" onClick={(e) => e.stopPropagation()}>
                      {hasPermission('EQUIPO_EDITAR') && (
                        <button
                          onClick={() => handleOpenEdit(eq)}
                          className="inline-flex items-center gap-1 px-2.5 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:text-brand-600 hover:border-brand-300 hover:bg-brand-50 transition-colors cursor-pointer"
                          title="Editar información completa del equipo"
                        >
                          <Edit2 className="w-3.5 h-3.5" />
                          <span>Editar</span>
                        </button>
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
            onSizeChange={(newSize) => {
              setSize(newSize);
              setPage(0);
            }}
            sizeOptions={[10, 25, 50, 100, 'ALL']}
          />
        </div>
      </div>

      {/* Modal: Editar Equipo */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        title={`Editar Equipo — ${selectedEquipo?.codigoInventario}`}
        subtitle={`Cliente asociado: ${selectedEquipo?.clienteNombre || '-'}`}
        size="lg"
      >
        <form onSubmit={handleSaveEdit} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Input
              label="Código / Ref. Inventario"
              required
              value={editForm.codigoInventario || ''}
              onChange={(e) => setEditForm({ ...editForm, codigoInventario: e.target.value })}
              placeholder="Ej. 43037-01"
            />
            <Input
              label="Nombre del Equipo / Hostname"
              value={editForm.nombreEquipo || ''}
              onChange={(e) => setEditForm({ ...editForm, nombreEquipo: e.target.value })}
              placeholder="Ej. recepcion-pc"
            />
            <Select
              label="Tipo de Equipo"
              required
              value={editForm.tipo || 'Sobremesa'}
              onChange={(e) => setEditForm({ ...editForm, tipo: e.target.value })}
            >
              <option value="Sobremesa">Sobremesa</option>
              <option value="Portátil">Portátil</option>
              <option value="Servidor">Servidor</option>
              <option value="Router / Switch">Router / Switch</option>
              <option value="Impresoras">Impresoras</option>
              <option value="Impresora">Impresora</option>
              <option value="SAI / UPS">SAI / UPS</option>
              <option value="Otros">Otros</option>
            </Select>
            <Select
              label="Estado"
              value={editForm.estado || 'Alta'}
              onChange={(e) => setEditForm({ ...editForm, estado: e.target.value })}
            >
              <option value="Alta">Alta</option>
              <option value="OPERATIVO">Operativo</option>
              <option value="EN_REPARACION">En Reparación</option>
              <option value="OBSOLETO">Obsoleto</option>
              <option value="BAJA">Baja</option>
            </Select>
            <Input
              label="Ubicación Física"
              value={editForm.ubicacion || ''}
              onChange={(e) => setEditForm({ ...editForm, ubicacion: e.target.value })}
              placeholder="Ej. Recepción / Planta 1"
            />
            <Input
              label="Última Acción / Revisión"
              value={editForm.ultimaRevision || ''}
              onChange={(e) => setEditForm({ ...editForm, ultimaRevision: e.target.value })}
              placeholder="Ej. 13/06/2014 12:30 o Sin acciones"
            />
            <Input
              label="Marca"
              value={editForm.marca || ''}
              onChange={(e) => setEditForm({ ...editForm, marca: e.target.value })}
              placeholder="Ej. HP, Dell, Lenovo"
            />
            <Input
              label="Modelo"
              value={editForm.modelo || ''}
              onChange={(e) => setEditForm({ ...editForm, modelo: e.target.value })}
              placeholder="Ej. ProDesk 400"
            />
            <Input
              label="Número de Serie"
              value={editForm.numeroSerie || ''}
              onChange={(e) => setEditForm({ ...editForm, numeroSerie: e.target.value })}
              placeholder="Ej. SN-12345678"
            />
            <Input
              label="Enlace / URL de Soporte"
              value={editForm.url || ''}
              onChange={(e) => setEditForm({ ...editForm, url: e.target.value })}
              placeholder="https://soporte.abaxial.es/pc_list.php?ref=..."
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Observaciones Técnicas
            </label>
            <textarea
              rows={2}
              value={editForm.observaciones || ''}
              onChange={(e) => setEditForm({ ...editForm, observaciones: e.target.value })}
              className="w-full px-3 py-2 text-sm rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500"
              placeholder="Notas técnicas sobre este activo..."
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsEditModalOpen(false)}>
              Cancelar
            </Button>
            <Button variant="primary" type="submit" isLoading={isSaving}>
              Guardar Cambios
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
