export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  nombreCompleto: string;
  activo: boolean;
  roles: string[];
  permisos: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInMs: number;
  user: UserProfile;
}

export interface ClienteList {
  id: number;
  codigo: string;
  nifCif: string;
  nombre: string;
  estado: 'ALTA' | 'BAJA' | string;
  mantenimiento: string;
  direccion?: string;
  poblacion?: string;
  provincia?: string;
  gerente?: string;
  fechaAlta: string;
  activo: boolean;
  totalEquipos: number;
  totalServicios: number;
  totalWebs: number;
  totalDocumentos: number;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface Contacto {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  nombre: string;
  apellidos?: string;
  cargo?: string;
  email?: string;
  telefono?: string;
  telefonoFijo?: string;
  observaciones?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface ContactoRequest {
  nombre: string;
  apellidos?: string;
  cargo?: string;
  email?: string;
  telefono?: string;
  telefonoFijo?: string;
  observaciones?: string;
  activo?: boolean;
}

export interface Equipo {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  codigoInventario: string;
  tipo: string;
  marca?: string;
  modelo?: string;
  numeroSerie?: string;
  estado: 'OPERATIVO' | 'EN_REPARACION' | 'BAJA' | 'OBSOLETO' | string;
  fechaAlta?: string;
  fechaBaja?: string;
  observaciones?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface EquipoRequest {
  codigoInventario?: string;
  tipo: string;
  marca?: string;
  modelo?: string;
  numeroSerie?: string;
  estado?: string;
  fechaAlta?: string;
  fechaBaja?: string;
  observaciones?: string;
  activo?: boolean;
}

export interface Servicio {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  nombre: string;
  descripcion?: string;
  estado: 'ACTIVO' | 'PAUSADO' | 'FINALIZADO' | 'INACTIVO' | string;
  fechaInicio?: string;
  fechaFin?: string;
  observaciones?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface ServicioRequest {
  nombre: string;
  descripcion?: string;
  estado?: string;
  fechaInicio?: string;
  fechaFin?: string;
  observaciones?: string;
  activo?: boolean;
}

export interface WebItem {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  nombre: string;
  url: string;
  estado: 'ONLINE' | 'OFFLINE' | 'MANTENIMIENTO' | string;
  descripcion?: string;
  fechaRegistro?: string;
  observaciones?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface WebRequest {
  nombre: string;
  url: string;
  estado?: string;
  descripcion?: string;
  fechaRegistro?: string;
  observaciones?: string;
  activo?: boolean;
}

export interface DocumentoVersion {
  id: number;
  documentoId: number;
  version: number;
  nombreArchivo: string;
  extension: string;
  mimeType: string;
  tamano: number;
  usuarioId?: number;
  usuarioNombre?: string;
  fechaSubida: string;
}

export interface Documento {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  nombreOriginal: string;
  categoria: string;
  descripcion?: string;
  versionActual: number;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
  ultimaVersion?: DocumentoVersion;
  versiones: DocumentoVersion[];
}

export interface Evento {
  id: number;
  recurrenciaId?: string;
  usuarioId?: number;
  usuarioNombre?: string;
  clienteId?: number;
  clienteNombre?: string;
  titulo: string;
  descripcion?: string;
  fechaInicio: string;
  fechaFin?: string;
  tipo?: 'RECURRENTE' | 'PUNTUAL' | string;
  recurrencia?: 'DIARIO' | 'SEMANAL' | 'QUINCENAL' | 'MENSUAL' | 'ANUAL' | string;
  fechaFinRecurrencia?: string;
  diasSemana?: string;
  diaMes?: number;
  eventoPadreId?: number;
  fechaOriginalOcurrencia?: string;
  esExcepcion?: boolean;
  esRecurrente?: boolean;
  fechasExcluidas?: string;
  prioridad: 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE' | string;
  estado: 'PENDIENTE' | 'EN_PROCESO' | 'COMPLETADO' | 'CANCELADO' | string;
  visibilidad: 'COMPARTIDO' | 'PRIVADO' | string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface EventoRequest {
  clienteId?: number;
  titulo: string;
  descripcion?: string;
  fechaInicio: string;
  fechaFin?: string;
  tipo?: 'RECURRENTE' | 'PUNTUAL' | string;
  recurrencia?: string;
  fechaFinRecurrencia?: string;
  diasSemana?: string;
  diaMes?: number;
  eventoPadreId?: number;
  fechaOriginalOcurrencia?: string;
  esExcepcion?: boolean;
  fechasExcluidas?: string;
  prioridad?: string;
  estado?: string;
  visibilidad?: string;
  activo?: boolean;
}

export interface AuditoriaItem {
  id: number;
  usuarioId?: number;
  username: string;
  accion: string;
  entidad: string;
  entidadId?: number;
  fecha: string;
  ip?: string;
  userAgent?: string;
  detalles?: string;
}

export interface DashboardStats {
  totalClientes: number;
  clientesActivos: number;
  clientesInactivos: number;
  totalEquipos: number;
  totalServicios: number;
  totalWebs: number;
  totalDocumentos: number;
  totalUsuarios: number;
  eventosPendientes: number;
  proximosEventos: Evento[];
  actividadReciente: AuditoriaItem[];
}

export interface ClienteDetail extends ClienteList {
  contactos: Contacto[];
  equipos: Equipo[];
  servicios: Servicio[];
  webs: WebItem[];
  documentos: Documento[];
  eventos: Evento[];
}

export interface ClienteRequest {
  codigo?: string;
  nifCif: string;
  nombre: string;
  estado?: string;
  mantenimiento?: string;
  direccion?: string;
  poblacion?: string;
  provincia?: string;
  gerente?: string;
  fechaAlta?: string;
  activo?: boolean;
}

export interface UsuarioItem {
  id: number;
  username: string;
  email: string;
  nombre: string;
  apellidos: string;
  nombreCompleto: string;
  activo: boolean;
  fechaAlta: string;
  fechaModificacion: string;
  roles: string[];
  permisos: string[];
}

export interface UsuarioRequest {
  username?: string;
  email?: string;
  password?: string;
  nombre?: string;
  apellidos?: string;
  activo?: boolean;
  roles?: string[];
}

export interface RoleItem {
  id: number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  activo: boolean;
  permisos: string[];
}

export interface PermisoItem {
  id: number;
  codigo: string;
  nombre: string;
  categoria: string;
  descripcion?: string;
}
