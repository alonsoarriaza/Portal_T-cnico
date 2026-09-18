import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Users2,
  Plus,
  Search,
  Eye,
  Edit2,
  Trash2,
  FileDown,
  HardDrive,
  UserCheck,
  UserX,
  Upload,
} from 'lucide-react';
import { clientesApi } from '../api/services';
import { ClienteList, ClienteRequest } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { ImportClientsModal } from '../components/clients/ImportClientsModal';

export const ClientesPage: React.FC = () => {
  const [clientes, setClientes] = useState<ClienteList[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState<number | 'ALL'>(10);

  // Filters
  const [search, setSearch] = useState('');
  const [estado, setEstado] = useState('');
  const [mantenimiento, setMantenimiento] = useState('');
  const [provincia, setProvincia] = useState('');
  const [includeInactive, setIncludeInactive] = useState(false);

  // Modals
  const [isFormModalOpen, setIsFormModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isPermanentDeleteModalOpen, setIsPermanentDeleteModalOpen] = useState(false);
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [selectedCliente, setSelectedCliente] = useState<ClienteList | null>(null);
  const [formData, setFormData] = useState<ClienteRequest>({
    codigo: '',
    nifCif: '',
    nombre: '',
    estado: 'ALTA',
    mantenimiento: 'ESTANDAR',
    direccion: '',
    poblacion: '',
    provincia: '',
    gerente: '',
    activo: true,
  });
  const [isSaving, setIsSaving] = useState(false);

  const { hasPermission, hasRole } = useAuth();
  const { success, error } = useToast();
  const navigate = useNavigate();

  const loadClientes = useCallback(async () => {
    try {
      setLoading(true);
      const isAll = size === 'ALL';
      const data = await clientesApi.list({
        search: search || undefined,
        estado: estado || undefined,
        mantenimiento: mantenimiento || undefined,
        provincia: provincia || undefined,
        includeInactive,
        page: isAll ? 0 : page,
        size: isAll ? 'ALL' : size,
      });
      setClientes(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar la lista de clientes');
    } finally {
      setLoading(false);
    }
  }, [search, estado, mantenimiento, provincia, includeInactive, page, size, error]);

  useEffect(() => {
    loadClientes();
  }, [loadClientes]);

  const handleOpenCreate = () => {
    setSelectedCliente(null);
    setFormData({
      codigo: '',
      nifCif: '',
      nombre: '',
      estado: 'ALTA',
      mantenimiento: 'ESTANDAR',
      direccion: '',
      poblacion: '',
      provincia: '',
      gerente: '',
      activo: true,
    });
    setIsFormModalOpen(true);
  };

  const handleOpenEdit = (cliente: ClienteList) => {
    setSelectedCliente(cliente);
    setFormData({
      codigo: cliente.codigo,
      nifCif: cliente.nifCif,
      nombre: cliente.nombre,
      estado: cliente.estado,
      mantenimiento: cliente.mantenimiento || 'ESTANDAR',
      direccion: cliente.direccion || '',
      poblacion: cliente.poblacion || '',
      provincia: cliente.provincia || '',
      gerente: cliente.gerente || '',
      activo: cliente.activo,
    });
    setIsFormModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.nifCif.trim() || !formData.nombre.trim()) {
      error('El NIF/CIF y el Nombre son obligatorios');
      return;
    }

    try {
      setIsSaving(true);
      const payload: ClienteRequest = {
        ...formData,
        activo: formData.estado === 'ALTA',
      };
      if (selectedCliente) {
        await clientesApi.update(selectedCliente.id, payload);
        success('Cliente actualizado con éxito');
      } else {
        await clientesApi.create(payload);
        success('Cliente creado con éxito');
      }
      setIsFormModalOpen(false);
      loadClientes();
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Error al guardar el cliente';
      error(msg);
    } finally {
      setIsSaving(false);
    }
  };

  const handleDarAlta = async (cliente: ClienteList) => {
    try {
      setLoading(true);
      await clientesApi.darAlta(cliente.id);
      success(`Cliente "${cliente.nombre}" dado de alta exitosamente`);
      loadClientes();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al dar de alta el cliente');
      setLoading(false);
    }
  };

  const handleDeactivate = async () => {
    if (!selectedCliente) return;
    try {
      await clientesApi.deactivate(selectedCliente.id);
      success(`Cliente "${selectedCliente.nombre}" dado de baja`);
      setIsDeleteModalOpen(false);
      loadClientes();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al desactivar el cliente');
    }
  };

  const handlePermanentDelete = async () => {
    if (!selectedCliente) return;
    try {
      await clientesApi.deletePermanente(selectedCliente.id);
      success(`Cliente "${selectedCliente.nombre}" eliminado permanentemente`);
      setIsPermanentDeleteModalOpen(false);
      loadClientes();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al eliminar el cliente');
    }
  };

  const handleDownloadPdf = async (e: React.MouseEvent, id: number, nombre: string) => {
    e.stopPropagation();
    try {
      const blob = await clientesApi.downloadPdf(id);
      const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Ficha_${nombre.replace(/\s+/g, '_')}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
      success('Ficha PDF descargada');
    } catch (err) {
      error('Error al generar el PDF del cliente');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Users2 className="w-5 h-5 text-brand-600" />
            <span>Gestión de Clientes</span>
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Administración de empresas clientes, contratos y parque de activos.
          </p>
        </div>
        <div className="flex items-center gap-2.5">
          {hasPermission('CLIENTE_CREAR') && (
            <>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setIsImportModalOpen(true)}
                leftIcon={<Upload className="w-4 h-4 text-brand-600" />}
              >
                Importar Clientes
              </Button>
              <Button variant="primary" size="sm" onClick={handleOpenCreate} leftIcon={<Plus className="w-4 h-4" />}>
                Nuevo Cliente
              </Button>
            </>
          )}
        </div>
      </div>

      {/* Filters Bar */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm space-y-3">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <Input
            placeholder="Buscar por nombre, código o NIF..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            leftIcon={<Search className="w-4 h-4" />}
          />
          <Select
            value={estado}
            onChange={(e) => {
              setEstado(e.target.value);
              setPage(0);
            }}
          >
            <option value="">Todos los Estados</option>
            <option value="ALTA">Alta</option>
            <option value="BAJA">Baja</option>
          </Select>
          <Select
            value={mantenimiento}
            onChange={(e) => {
              setMantenimiento(e.target.value);
              setPage(0);
            }}
          >
            <option value="">Tipo de Mantenimiento</option>
            <option value="INTEGRAL">Integral</option>
            <option value="ESTANDAR">Estándar</option>
            <option value="BASICO">Básico</option>
            <option value="BOLSA_HORAS">Bolsa de Horas</option>
            <option value="NINGUNO">Sin Mantenimiento</option>
          </Select>
          <Input
            placeholder="Filtrar por provincia..."
            value={provincia}
            onChange={(e) => {
              setProvincia(e.target.value);
              setPage(0);
            }}
          />
        </div>

        <div className="flex items-center justify-between pt-2 border-t border-slate-100 text-xs text-slate-500">
          <label className="flex items-center gap-2 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={includeInactive}
              onChange={(e) => {
                setIncludeInactive(e.target.checked);
                setPage(0);
              }}
              className="rounded border-slate-300 text-brand-600 focus:ring-brand-500"
            />
            <span>Mostrar también clientes inactivos (dados de baja)</span>
          </label>
        </div>
      </div>

      {/* Clientes Table */}
      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando clientes..." />
        ) : clientes.length === 0 ? (
          <EmptyState
            title="No se encontraron clientes"
            description="No hay clientes registrados o que coincidan con la búsqueda."
            actionText={hasPermission('CLIENTE_CREAR') ? 'Crear Nuevo Cliente' : undefined}
            onAction={hasPermission('CLIENTE_CREAR') ? handleOpenCreate : undefined}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Código</th>
                  <th className="px-4 py-3">NIF/CIF</th>
                  <th className="px-4 py-3">Nombre / Razón Social</th>
                  <th className="px-4 py-3">Fecha Alta</th>
                  <th className="px-4 py-3 text-center">Equipos</th>
                  <th className="px-4 py-3">Estado</th>
                  <th className="px-4 py-3">Mantenimiento</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {clientes.map((c) => (
                  <tr
                    key={c.id}
                    onClick={() => navigate(`/clientes/${c.id}`)}
                    className="hover:bg-slate-50/80 transition-colors cursor-pointer"
                  >
                    <td className="px-4 py-3 font-mono font-bold text-brand-600 text-xs">
                      {c.codigo}
                    </td>
                    <td className="px-4 py-3 font-mono text-xs text-slate-700">{c.nifCif}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      <div>{c.nombre}</div>
                      {c.poblacion && (
                        <div className="text-[11px] text-slate-400 font-normal">
                          {c.poblacion} {c.provincia ? `(${c.provincia})` : ''}
                        </div>
                      )}
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-500">{c.fechaAlta}</td>
                    <td className="px-4 py-3 text-center">
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded bg-slate-100 text-slate-700 font-bold text-xs">
                        <HardDrive className="w-3 h-3 text-slate-400" />
                        {c.totalEquipos}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <Badge variant={c.estado === 'ALTA' && c.activo ? 'emerald' : 'rose'} dot size="sm">
                        {c.estado === 'ALTA' && c.activo ? 'ALTA' : 'BAJA'}
                      </Badge>
                    </td>
                    <td className="px-4 py-3">
                      <Badge variant="slate" size="sm">
                        {c.mantenimiento || 'ESTANDAR'}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-right" onClick={(e) => e.stopPropagation()}>
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => navigate(`/clientes/${c.id}`)}
                          className="p-1 rounded text-slate-400 hover:text-slate-700 hover:bg-slate-100"
                          title="Ver Ficha 360"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={(e) => handleDownloadPdf(e, c.id, c.nombre)}
                          className="p-1 rounded text-sky-600 hover:bg-sky-50"
                          title="Descargar Ficha PDF"
                        >
                          <FileDown className="w-4 h-4" />
                        </button>
                        {hasPermission('CLIENTE_EDITAR') && (c.estado === 'BAJA' || !c.activo) && (
                          <button
                            onClick={() => handleDarAlta(c)}
                            className="p-1 rounded text-emerald-600 hover:bg-emerald-50"
                            title="Dar de alta"
                          >
                            <UserCheck className="w-4 h-4" />
                          </button>
                        )}
                        {hasPermission('CLIENTE_EDITAR') && (
                          <button
                            onClick={() => handleOpenEdit(c)}
                            className="p-1 rounded text-amber-600 hover:bg-amber-50"
                            title="Editar Cliente"
                          >
                            <Edit2 className="w-4 h-4" />
                          </button>
                        )}
                        {hasPermission('CLIENTE_ELIMINAR') && (c.estado === 'ALTA' && c.activo) && (
                          <button
                            onClick={() => {
                              setSelectedCliente(c);
                              setIsDeleteModalOpen(true);
                            }}
                            className="p-1 rounded text-rose-600 hover:bg-rose-50"
                            title="Dar de baja"
                          >
                            <UserX className="w-4 h-4" />
                          </button>
                        )}
                        {hasRole('SUPER_ADMIN') && (
                          <button
                            onClick={() => {
                              setSelectedCliente(c);
                              setIsPermanentDeleteModalOpen(true);
                            }}
                            className="p-1 rounded text-red-700 hover:bg-red-50"
                            title="Eliminar permanentemente"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div className="px-4 border-t border-slate-100">
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

      {/* Modal: Crear / Editar Cliente */}
      <Modal
        isOpen={isFormModalOpen}
        onClose={() => setIsFormModalOpen(false)}
        title={selectedCliente ? 'Editar Cliente' : 'Nuevo Cliente'}
        subtitle={selectedCliente ? `Modificando datos de ${selectedCliente.nombre}` : 'Registrar nueva empresa en el portal'}
        maxWidth="2xl"
        footer={
          <>
            <Button variant="outline" size="sm" onClick={() => setIsFormModalOpen(false)}>
              Cancelar
            </Button>
            <Button variant="primary" size="sm" onClick={handleSave} isLoading={isSaving}>
              {selectedCliente ? 'Guardar Cambios' : 'Crear Cliente'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleSave} className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Código (Opcional - Autogenerado)"
            placeholder="CLI-0001"
            value={formData.codigo || ''}
            onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
          />
          <Input
            label="NIF / CIF *"
            required
            placeholder="B12345678"
            value={formData.nifCif}
            onChange={(e) => setFormData({ ...formData, nifCif: e.target.value })}
          />
          <div className="sm:col-span-2">
            <Input
              label="Nombre / Razón Social *"
              required
              placeholder="Ej. ACME Corporation S.L."
              value={formData.nombre}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
            />
          </div>
          <Select
            label="Estado"
            value={formData.estado || 'ALTA'}
            onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
          >
            <option value="ALTA">ALTA</option>
            <option value="BAJA">BAJA</option>
          </Select>
          <Select
            label="Tipo de Mantenimiento"
            value={formData.mantenimiento || 'ESTANDAR'}
            onChange={(e) => setFormData({ ...formData, mantenimiento: e.target.value })}
          >
            <option value="INTEGRAL">Integral (24/7)</option>
            <option value="ESTANDAR">Estándar (Horario Laboral)</option>
            <option value="BASICO">Básico (Preventivo)</option>
            <option value="BOLSA_HORAS">Bolsa de Horas</option>
            <option value="NINGUNO">Sin Contrato</option>
          </Select>
          <div className="sm:col-span-2">
            <Input
              label="Dirección"
              placeholder="Calle Mayor 12, Planta 3"
              value={formData.direccion || ''}
              onChange={(e) => setFormData({ ...formData, direccion: e.target.value })}
            />
          </div>
          <Input
            label="Población"
            placeholder="Madrid"
            value={formData.poblacion || ''}
            onChange={(e) => setFormData({ ...formData, poblacion: e.target.value })}
          />
          <Input
            label="Provincia"
            placeholder="Madrid"
            value={formData.provincia || ''}
            onChange={(e) => setFormData({ ...formData, provincia: e.target.value })}
          />
          <Input
            label="Persona de Contacto / Gerente"
            placeholder="Alonso Feria"
            value={formData.gerente || ''}
            onChange={(e) => setFormData({ ...formData, gerente: e.target.value })}
          />
          <Input
            label="Fecha de Alta"
            type="date"
            value={formData.fechaAlta || ''}
            onChange={(e) => setFormData({ ...formData, fechaAlta: e.target.value })}
          />
        </form>
      </Modal>

      {/* Modal: Confirmar Desactivación */}
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="Dar de baja cliente"
        subtitle="Confirmación de borrado lógico"
        maxWidth="md"
        footer={
          <>
            <Button variant="outline" size="sm" onClick={() => setIsDeleteModalOpen(false)}>
              Cancelar
            </Button>
            <Button variant="danger" size="sm" onClick={handleDeactivate}>
              Confirmar Baja
            </Button>
          </>
        }
      >
        <p className="text-sm text-slate-700">
          ¿Estás seguro de que deseas dar de baja al cliente{' '}
          <strong className="text-slate-900">{selectedCliente?.nombre}</strong>?
        </p>
        <p className="text-xs text-slate-500 mt-2">
          La información y registros asociados permanecerán en el sistema para fines de trazabilidad.
        </p>
      </Modal>

      {/* Modal: Confirmar Eliminación Permanente */}
      <Modal
        isOpen={isPermanentDeleteModalOpen}
        onClose={() => setIsPermanentDeleteModalOpen(false)}
        title="Eliminar cliente permanentemente"
        subtitle="⚠️ Acción irreversible"
        maxWidth="md"
        footer={
          <>
            <Button variant="outline" size="sm" onClick={() => setIsPermanentDeleteModalOpen(false)}>
              Cancelar
            </Button>
            <Button variant="danger" size="sm" onClick={handlePermanentDelete}>
              Eliminar Permanentemente
            </Button>
          </>
        }
      >
        <div className="space-y-3">
          <p className="text-sm text-red-700 font-semibold">
            ¿Estás seguro de que deseas ELIMINAR PERMANENTEMENTE al cliente{' '}
            <strong className="text-red-900">{selectedCliente?.nombre}</strong>?
          </p>
          <p className="text-xs text-red-600 bg-red-50 p-3 rounded-lg border border-red-200">
            Esta acción es <strong>IRREVERSIBLE</strong>. Se eliminará el cliente y TODOS sus registros asociados
            (equipos, contactos, servicios, webs, documentos y eventos) de forma permanente de la base de datos.
          </p>
        </div>
      </Modal>

      {/* Modal: Importación Inteligente */}
      <ImportClientsModal
        isOpen={isImportModalOpen}
        onClose={() => setIsImportModalOpen(false)}
        onSuccess={() => {
          loadClientes();
        }}
      />
    </div>
  );
};
