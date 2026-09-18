import React, { useState, useEffect, useCallback } from 'react';
import { ShieldCheck, Plus, Search, UserX, Edit2, Shield, Key } from 'lucide-react';
import { usuariosApi } from '../api/services';
import { UsuarioItem, UsuarioRequest, RoleItem, PermisoItem } from '../types';
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
import { format } from 'date-fns';

export const UsuariosPage: React.FC = () => {
  const [usuarios, setUsuarios] = useState<UsuarioItem[]>([]);
  const [roles, setRoles] = useState<RoleItem[]>([]);
  const [permisos, setPermisos] = useState<PermisoItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState<number | 'ALL'>(10);
  const [search, setSearch] = useState('');
  const [activeTab, setActiveTab] = useState<'usuarios' | 'roles'>('usuarios');

  // Modals
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UsuarioItem | null>(null);
  const [formData, setFormData] = useState<UsuarioRequest>({
    username: '',
    email: '',
    password: '',
    nombre: '',
    apellidos: '',
    activo: true,
    roles: ['TECNICO'],
  });

  const { hasRole } = useAuth();
  const { success, error } = useToast();

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const isAll = size === 'ALL';
      const [usersData, rolesData, permisosData] = await Promise.all([
        usuariosApi.list({ search: search || undefined, page: isAll ? 0 : page, size: isAll ? 'ALL' : size }),
        usuariosApi.listRoles(),
        usuariosApi.listPermisos(),
      ]);
      setUsuarios(usersData.content);
      setTotalElements(usersData.totalElements);
      setTotalPages(usersData.totalPages);
      setRoles(rolesData);
      setPermisos(permisosData);
    } catch (err) {
      error('Error al cargar usuarios y roles');
    } finally {
      setLoading(false);
    }
  }, [search, page, size, error]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleOpenCreate = () => {
    setSelectedUser(null);
    setFormData({
      username: '',
      email: '',
      password: '',
      nombre: '',
      apellidos: '',
      activo: true,
      roles: ['TECNICO'],
    });
    setIsUserModalOpen(true);
  };

  const handleOpenEdit = (u: UsuarioItem) => {
    setSelectedUser(u);
    setFormData({
      username: u.username,
      email: u.email,
      password: '',
      nombre: u.nombre,
      apellidos: u.apellidos,
      activo: u.activo,
      roles: u.roles,
    });
    setIsUserModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.email?.trim() || !formData.nombre?.trim()) {
      error('Email y Nombre son obligatorios');
      return;
    }

    try {
      if (selectedUser) {
        await usuariosApi.update(selectedUser.id, formData);
        success('Usuario actualizado con éxito');
      } else {
        if (!formData.username?.trim() || !formData.password?.trim()) {
          error('Usuario y Contraseña son obligatorios para crear una cuenta');
          return;
        }
        await usuariosApi.create(formData);
        success('Usuario registrado con éxito');
      }
      setIsUserModalOpen(false);
      loadData();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error guardando usuario');
    }
  };

  const handleDeactivate = async (u: UsuarioItem) => {
    try {
      await usuariosApi.deactivate(u.id);
      success('Usuario desactivado');
      loadData();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error desactivando usuario');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-indigo-600" />
            <span>Usuarios, Roles y Permisos</span>
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">
            Administración de accesos y matriz de permisos por rol.
          </p>
        </div>
        {hasRole('SUPER_ADMIN') && (
          <Button variant="primary" size="sm" onClick={handleOpenCreate} leftIcon={<Plus className="w-4 h-4" />}>
            Nuevo Usuario
          </Button>
        )}
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 pb-px">
        <button
          onClick={() => setActiveTab('usuarios')}
          className={`px-3 py-2 text-xs font-bold rounded-t-lg border-b-2 transition-colors ${
            activeTab === 'usuarios'
              ? 'border-brand-600 text-brand-600 bg-brand-50/50'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          Cuentas de Usuario ({totalElements})
        </button>
        <button
          onClick={() => setActiveTab('roles')}
          className={`px-3 py-2 text-xs font-bold rounded-t-lg border-b-2 transition-colors ${
            activeTab === 'roles'
              ? 'border-brand-600 text-brand-600 bg-brand-50/50'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          Matriz de Roles y Permisos ({permisos.length} permisos)
        </button>
      </div>

      {activeTab === 'usuarios' ? (
        <>
          <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm">
            <Input
              placeholder="Buscar usuario por nombre, email o username..."
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
              leftIcon={<Search className="w-4 h-4" />}
            />
          </div>

          <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
            {loading ? (
              <LoadingSpinner message="Cargando usuarios..." />
            ) : usuarios.length === 0 ? (
              <EmptyState title="No hay usuarios" description="No se han encontrado cuentas registradas." />
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm text-slate-600">
                  <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-600">
                    <tr>
                      <th className="px-4 py-3">Usuario</th>
                      <th className="px-4 py-3">Nombre Completo</th>
                      <th className="px-4 py-3">Email</th>
                      <th className="px-4 py-3">Rol</th>
                      <th className="px-4 py-3">Estado</th>
                      <th className="px-4 py-3">Fecha de Alta</th>
                      <th className="px-4 py-3 text-right">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {usuarios.map((u) => (
                      <tr key={u.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-4 py-3 font-mono font-bold text-slate-900 flex items-center gap-1.5">
                          <Shield className="w-3.5 h-3.5 text-slate-400" />
                          <span>{u.username}</span>
                        </td>
                        <td className="px-4 py-3 font-medium text-slate-800">{u.nombreCompleto}</td>
                        <td className="px-4 py-3 text-xs text-slate-500">{u.email}</td>
                        <td className="px-4 py-3">
                          <div className="flex gap-1 flex-wrap">
                            {u.roles.map((r) => (
                              <Badge key={r} variant={r === 'SUPER_ADMIN' ? 'brand' : 'slate'} size="sm">
                                {r}
                              </Badge>
                            ))}
                          </div>
                        </td>
                        <td className="px-4 py-3">
                          <Badge variant={u.activo ? 'emerald' : 'rose'} dot size="sm">
                            {u.activo ? 'Activo' : 'Inactivo'}
                          </Badge>
                        </td>
                        <td className="px-4 py-3 text-xs text-slate-400">
                          {u.fechaAlta ? format(new Date(u.fechaAlta), 'dd/MM/yyyy') : '-'}
                        </td>
                        <td className="px-4 py-3 text-right">
                          <div className="flex items-center justify-end gap-1">
                            <button
                              onClick={() => handleOpenEdit(u)}
                              className="p-1 rounded text-slate-400 hover:text-amber-600 hover:bg-slate-100"
                              title="Editar Usuario"
                            >
                              <Edit2 className="w-4 h-4" />
                            </button>
                            {u.username !== 'admin' && u.activo && (
                              <button
                                onClick={() => handleDeactivate(u)}
                                className="p-1 rounded text-slate-400 hover:text-rose-600 hover:bg-slate-100"
                                title="Desactivar Usuario"
                              >
                                <UserX className="w-4 h-4" />
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
        </>
      ) : (
        /* TAB: ROLES Y PERMISOS */
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {roles.map((rol) => (
            <div key={rol.id} className="p-5 rounded-xl bg-white border border-slate-200 shadow-sm space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-base text-slate-900 flex items-center gap-1.5">
                    <Key className="w-4 h-4 text-brand-600" />
                    <span>{rol.nombre}</span>
                  </h3>
                  <p className="text-xs text-slate-500 mt-0.5">{rol.descripcion}</p>
                </div>
                <Badge variant={rol.codigo === 'SUPER_ADMIN' ? 'brand' : 'slate'}>{rol.codigo}</Badge>
              </div>
              <div>
                <h4 className="text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-2">
                  Permisos Autorizados ({rol.permisos?.length || 0})
                </h4>
                <div className="flex flex-wrap gap-1 max-h-48 overflow-y-auto p-2 bg-slate-50 rounded-lg border border-slate-200">
                  {rol.permisos?.map((p) => (
                    <span key={p} className="text-[11px] font-mono px-2 py-0.5 rounded bg-white text-slate-700 border border-slate-200">
                      {p}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal: Crear / Editar Usuario */}
      <Modal
        isOpen={isUserModalOpen}
        onClose={() => setIsUserModalOpen(false)}
        title={selectedUser ? 'Editar Usuario' : 'Nuevo Usuario'}
        subtitle={selectedUser ? `Modificando ${selectedUser.username}` : 'Registrar nueva cuenta'}
        footer={
          <>
            <Button variant="outline" size="sm" onClick={() => setIsUserModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" size="sm" onClick={handleSave}>{selectedUser ? 'Guardar Cambios' : 'Crear Usuario'}</Button>
          </>
        }
      >
        <form onSubmit={handleSave} className="space-y-3">
          {!selectedUser && (
            <Input
              label="Nombre de Usuario (Username) *"
              required
              placeholder="juan.perez"
              value={formData.username || ''}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
            />
          )}
          <Input
            label="Correo Electrónico *"
            type="email"
            required
            placeholder="juan.perez@coanda.es"
            value={formData.email || ''}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <Input
              label="Nombre *"
              required
              placeholder="Juan"
              value={formData.nombre || ''}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
            />
            <Input
              label="Apellidos"
              placeholder="Pérez"
              value={formData.apellidos || ''}
              onChange={(e) => setFormData({ ...formData, apellidos: e.target.value })}
            />
          </div>
          <Input
            label={selectedUser ? 'Cambiar Contraseña (dejar en blanco para mantener)' : 'Contraseña *'}
            type="password"
            placeholder="••••••••••••"
            value={formData.password || ''}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
          />
          <Select
            label="Rol en el Portal"
            value={formData.roles ? formData.roles[0] : 'TECNICO'}
            onChange={(e) => setFormData({ ...formData, roles: [e.target.value] })}
          >
            <option value="TECNICO">TÉCNICO (Gestión Técnica y Soporte)</option>
            <option value="SUPER_ADMIN">SUPER_ADMIN (Control Total y Configuración)</option>
          </Select>
        </form>
      </Modal>
    </div>
  );
};
