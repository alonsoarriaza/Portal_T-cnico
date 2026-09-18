import apiClient from './client';
import {
  ApiResponse,
  PaginatedResponse,
  AuthResponse,
  UserProfile,
  DashboardStats,
  ClienteList,
  ClienteDetail,
  ClienteRequest,
  Contacto,
  ContactoRequest,
  Equipo,
  EquipoRequest,
  Servicio,
  ServicioRequest,
  WebItem,
  WebRequest,
  Documento,
  Evento,
  EventoRequest,
  AuditoriaItem,
  UsuarioItem,
  UsuarioRequest,
  RoleItem,
  PermisoItem,
} from '../types';

// ==================== AUTH ====================
export const authApi = {
  login: async (credentials: { usernameOrEmail: string; password: string }) => {
    const res = await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', credentials);
    return res.data.data;
  },
  getMe: async () => {
    const res = await apiClient.get<ApiResponse<UserProfile>>('/auth/me');
    return res.data.data;
  },
  logout: async () => {
    const res = await apiClient.post<ApiResponse<void>>('/auth/logout');
    return res.data;
  },
};

// ==================== DASHBOARD ====================
export const dashboardApi = {
  getStats: async () => {
    const res = await apiClient.get<ApiResponse<DashboardStats>>('/dashboard');
    return res.data.data;
  },
};

// ==================== CLIENTES ====================
export const clientesApi = {
  list: async (params?: {
    search?: string;
    estado?: string;
    mantenimiento?: string;
    provincia?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
    sortBy?: string;
    sortDir?: string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<ClienteList>>>('/clientes', { params });
    return res.data.data;
  },
  getDetail: async (id: number) => {
    const res = await apiClient.get<ApiResponse<ClienteDetail>>(`/clientes/${id}`);
    return res.data.data;
  },
  create: async (data: ClienteRequest) => {
    const res = await apiClient.post<ApiResponse<ClienteDetail>>('/clientes', data);
    return res.data.data;
  },
  update: async (id: number, data: ClienteRequest) => {
    const res = await apiClient.put<ApiResponse<ClienteDetail>>(`/clientes/${id}`, data);
    return res.data.data;
  },
  cambiarEstado: async (id: number, estado: string) => {
    const res = await apiClient.patch<ApiResponse<ClienteDetail>>(`/clientes/${id}/estado`, null, {
      params: { estado },
    });
    return res.data.data;
  },
  darAlta: async (id: number) => {
    const res = await apiClient.patch<ApiResponse<ClienteDetail>>(`/clientes/${id}/alta`);
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/clientes/${id}`);
    return res.data;
  },
  deletePermanente: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/clientes/${id}/permanente`);
    return res.data;
  },
  getDashboardStats: async () => {
    const res = await apiClient.get<ApiResponse<DashboardStats>>('/dashboard');
    return res.data.data;
  },
  downloadPdf: async (id: number) => {
    const res = await apiClient.get(`/clientes/${id}/pdf`, { responseType: 'blob' });
    return res.data;
  },
  analizarImportacion: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await apiClient.post<ApiResponse<any>>('/clientes/importar/analizar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },
  ejecutarImportacion: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await apiClient.post<ApiResponse<any>>('/clientes/importar/ejecutar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },
};

// ==================== CONTACTOS ====================
export const contactosApi = {
  listByCliente: async (clienteId: number, soloActivos = true) => {
    const res = await apiClient.get<ApiResponse<Contacto[]>>(`/clientes/${clienteId}/contactos`, {
      params: { soloActivos },
    });
    return res.data.data;
  },
  create: async (clienteId: number, data: ContactoRequest) => {
    const res = await apiClient.post<ApiResponse<Contacto>>(`/clientes/${clienteId}/contactos`, data);
    return res.data.data;
  },
  update: async (id: number, data: ContactoRequest) => {
    const res = await apiClient.put<ApiResponse<Contacto>>(`/contactos/${id}`, data);
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/contactos/${id}`);
    return res.data;
  },
};

// ==================== EQUIPOS ====================
export const equiposApi = {
  list: async (params?: {
    clienteId?: number;
    tipo?: string;
    estado?: string;
    search?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<Equipo>>>('/equipos', { params });
    return res.data.data;
  },
  listByCliente: async (clienteId: number, soloActivos = true) => {
    const res = await apiClient.get<ApiResponse<Equipo[]>>(`/clientes/${clienteId}/equipos`, {
      params: { soloActivos },
    });
    return res.data.data;
  },
  create: async (clienteId: number, data: EquipoRequest) => {
    const res = await apiClient.post<ApiResponse<Equipo>>(`/clientes/${clienteId}/equipos`, data);
    return res.data.data;
  },
  update: async (id: number, data: EquipoRequest) => {
    const res = await apiClient.put<ApiResponse<Equipo>>(`/equipos/${id}`, data);
    return res.data.data;
  },
  cambiarEstado: async (id: number, estado: string) => {
    const res = await apiClient.patch<ApiResponse<Equipo>>(`/equipos/${id}/estado`, null, {
      params: { estado },
    });
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/equipos/${id}`);
    return res.data;
  },
};

// ==================== SERVICIOS ====================
export const serviciosApi = {
  list: async (params?: {
    clienteId?: number;
    estado?: string;
    search?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<Servicio>>>('/servicios', { params });
    return res.data.data;
  },
  listByCliente: async (clienteId: number, soloActivos = true) => {
    const res = await apiClient.get<ApiResponse<Servicio[]>>(`/clientes/${clienteId}/servicios`, {
      params: { soloActivos },
    });
    return res.data.data;
  },
  create: async (clienteId: number, data: ServicioRequest) => {
    const res = await apiClient.post<ApiResponse<Servicio>>(`/clientes/${clienteId}/servicios`, data);
    return res.data.data;
  },
  update: async (id: number, data: ServicioRequest) => {
    const res = await apiClient.put<ApiResponse<Servicio>>(`/servicios/${id}`, data);
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/servicios/${id}`);
    return res.data;
  },
};

// ==================== WEBS ====================
export const websApi = {
  list: async (params?: {
    clienteId?: number;
    estado?: string;
    search?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<WebItem>>>('/webs', { params });
    return res.data.data;
  },
  listByCliente: async (clienteId: number, soloActivos = true) => {
    const res = await apiClient.get<ApiResponse<WebItem[]>>(`/clientes/${clienteId}/webs`, {
      params: { soloActivos },
    });
    return res.data.data;
  },
  create: async (clienteId: number, data: WebRequest) => {
    const res = await apiClient.post<ApiResponse<WebItem>>(`/clientes/${clienteId}/webs`, data);
    return res.data.data;
  },
  update: async (id: number, data: WebRequest) => {
    const res = await apiClient.put<ApiResponse<WebItem>>(`/webs/${id}`, data);
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/webs/${id}`);
    return res.data;
  },
};

// ==================== DOCUMENTOS ====================
export const documentosApi = {
  list: async (params?: {
    clienteId?: number;
    categoria?: string;
    search?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<Documento>>>('/documentos', { params });
    return res.data.data;
  },
  listByCliente: async (clienteId: number, soloActivos = true) => {
    const res = await apiClient.get<ApiResponse<Documento[]>>(`/clientes/${clienteId}/documentos`, {
      params: { soloActivos },
    });
    return res.data.data;
  },
  getById: async (id: number) => {
    const res = await apiClient.get<ApiResponse<Documento>>(`/documentos/${id}`);
    return res.data.data;
  },
  upload: async (
    clienteId: number,
    file: File,
    categoria?: string,
    descripcion?: string,
    onProgress?: (percent: number) => void
  ) => {
    const formData = new FormData();
    formData.append('file', file);
    if (categoria) formData.append('categoria', categoria);
    if (descripcion) formData.append('descripcion', descripcion);

    const res = await apiClient.post<ApiResponse<Documento>>(`/clientes/${clienteId}/documentos`, formData, {
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percent);
        }
      },
    });
    return res.data.data;
  },
  uploadBatch: async (
    clienteId: number,
    files: File[],
    categoria?: string,
    descripcion?: string,
    onProgress?: (percent: number) => void
  ) => {
    const formData = new FormData();
    files.forEach((f) => formData.append('files', f));
    if (categoria) formData.append('categoria', categoria);
    if (descripcion) formData.append('descripcion', descripcion);

    const res = await apiClient.post<ApiResponse<Documento[]>>(`/clientes/${clienteId}/documentos/batch`, formData, {
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percent);
        }
      },
    });
    return res.data.data;
  },
  uploadNewVersion: async (
    documentoId: number,
    file: File,
    onProgress?: (percent: number) => void
  ) => {
    const formData = new FormData();
    formData.append('file', file);

    const res = await apiClient.post<ApiResponse<Documento>>(`/documentos/${documentoId}/versiones`, formData, {
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percent);
        }
      },
    });
    return res.data.data;
  },
  download: async (id: number, version?: number) => {
    const params = version ? { version } : {};
    const res = await apiClient.get(`/documentos/${id}/download`, {
      params,
      responseType: 'blob',
    });
    return res.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/documentos/${id}`);
    return res.data;
  },
  delete: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/documentos/${id}`);
    return res.data;
  },
};

// ==================== EVENTOS ====================
export const eventosApi = {
  list: async (params?: {
    usuarioId?: number;
    clienteId?: number;
    estado?: string;
    tipo?: string;
    desde?: string;
    hasta?: string;
    includeInactive?: boolean;
  }) => {
    const res = await apiClient.get<ApiResponse<Evento[]>>('/eventos', { params });
    return res.data.data;
  },
  create: async (data: EventoRequest) => {
    const res = await apiClient.post<ApiResponse<Evento>>('/eventos', data);
    return res.data.data;
  },
  update: async (id: number, data: EventoRequest) => {
    const res = await apiClient.put<ApiResponse<Evento>>(`/eventos/${id}`, data);
    return res.data.data;
  },
  modificarExcepcion: async (eventoPadreId: number, data: EventoRequest) => {
    const res = await apiClient.post<ApiResponse<Evento>>(`/eventos/${eventoPadreId}/excepcion`, data);
    return res.data.data;
  },
  excluirOcurrencia: async (eventoPadreId: number, fechaOriginal: string) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/eventos/${eventoPadreId}/excepcion`, {
      params: { fechaOriginal },
    });
    return res.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/eventos/${id}`);
    return res.data;
  },
};

// ==================== AUDITORIA ====================
export const auditoriaApi = {
  list: async (params?: {
    usuarioId?: number;
    search?: string;
    accion?: string;
    entidad?: string;
    desde?: string;
    hasta?: string;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<AuditoriaItem>>>('/auditoria', { params });
    return res.data.data;
  },
  getRecent: async () => {
    const res = await apiClient.get<ApiResponse<AuditoriaItem[]>>('/auditoria/recientes');
    return res.data.data;
  },
};

// ==================== USUARIOS & ROLES ====================
export const usuariosApi = {
  list: async (params?: {
    search?: string;
    includeInactive?: boolean;
    page?: number;
    size?: number | string;
  }) => {
    const res = await apiClient.get<ApiResponse<PaginatedResponse<UsuarioItem>>>('/usuarios', { params });
    return res.data.data;
  },
  getById: async (id: number) => {
    const res = await apiClient.get<ApiResponse<UsuarioItem>>(`/usuarios/${id}`);
    return res.data.data;
  },
  create: async (data: UsuarioRequest) => {
    const res = await apiClient.post<ApiResponse<UsuarioItem>>('/usuarios', data);
    return res.data.data;
  },
  update: async (id: number, data: UsuarioRequest) => {
    const res = await apiClient.put<ApiResponse<UsuarioItem>>(`/usuarios/${id}`, data);
    return res.data.data;
  },
  deactivate: async (id: number) => {
    const res = await apiClient.delete<ApiResponse<void>>(`/usuarios/${id}`);
    return res.data;
  },
  listRoles: async () => {
    const res = await apiClient.get<ApiResponse<RoleItem[]>>('/roles');
    return res.data.data;
  },
  listPermisos: async () => {
    const res = await apiClient.get<ApiResponse<PermisoItem[]>>('/roles/permisos');
    return res.data.data;
  },
};
