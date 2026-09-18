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
  nombreEquipo?: string;
  tipo: string;
  ubicacion?: string;
  marca?: string;
  modelo?: string;
  numeroSerie?: string;
  estado: 'OPERATIVO' | 'EN_REPARACION' | 'BAJA' | 'OBSOLETO' | 'Alta' | string;
  ultimaRevision?: string;
  url?: string;
  fechaAlta?: string;
  fechaBaja?: string;
  observaciones?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface EquipoRequest {
  codigoInventario?: string;
  nombreEquipo?: string;
  tipo: string;
  ubicacion?: string;
  marca?: string;
  modelo?: string;
  numeroSerie?: string;
  estado?: string;
  ultimaRevision?: string;
  url?: string;
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

export interface FieldConflict {
  entityType: string;
  entityIdentifier: string;
  fieldName: string;
  existingValue: string;
  importedValue: string;
  message: string;
}

export interface EquipmentAssociationConflict {
  equipmentReference: string;
  existingClientCodigo: string;
  existingClientNombre: string;
  importedClientCodigo: string;
  importedClientNombre: string;
  message: string;
}

export interface ClientUpdatePreview {
  clientId: number;
  codigo: string;
  nombre: string;
  fieldsToFill: Record<string, string>;
}

export interface ParsedEquipmentPreview {
  reference: string;
  name: string;
  type: string;
  location?: string;
  status?: string;
  lastAction?: string;
  url?: string;
  clientName?: string;
  clientCodigo?: string;
}

export interface ParsedContactPreview {
  nombre: string;
  apellidos?: string;
  cargo?: string;
  email?: string;
  telefono?: string;
  clientCodigo?: string;
  clientNombre?: string;
}

export interface ParsedClientPreview {
  codigo?: string;
  nifCif?: string;
  nombre?: string;
  estado?: string;
  mantenimiento?: string;
  direccion?: string;
  poblacion?: string;
  provincia?: string;
  gerente?: string;
  equipments?: ParsedEquipmentPreview[];
  contacts?: ParsedContactPreview[];
}

export interface ImportPlan {
  clientsFound: number;
  clientsToCreateCount: number;
  clientsToUpdateCount: number;
  clientsToSkipCount: number;
  equipmentsFound: number;
  equipmentsToCreateCount: number;
  equipmentsToSkipCount: number;
  contactsFound: number;
  contactsToCreateCount: number;
  contactsToSkipCount: number;
  clientsToCreate: ParsedClientPreview[];
  clientsToUpdate: ClientUpdatePreview[];
  clientsToSkip: string[];
  equipmentsToCreate: ParsedEquipmentPreview[];
  equipmentsToSkip: string[];
  contactsToCreate: ParsedContactPreview[];
  contactsToSkip: string[];
  conflicts: FieldConflict[];
  associationConflicts: EquipmentAssociationConflict[];
  warnings: string[];
  errors: string[];
}

export interface ImportSummary {
  clientsFound: number;
  clientsCreated: number;
  clientsMatched: number;
  clientsUpdated: number;
  equipmentsFound: number;
  equipmentsCreated: number;
  equipmentsMatched: number;
  equipmentsSkipped: number;
  contactsFound: number;
  contactsCreated: number;
  contactsMatched: number;
  contactsSkipped: number;
  conflictsCount: number;
  errorsCount: number;
  conflicts: FieldConflict[];
  associationConflicts: EquipmentAssociationConflict[];
  warnings: string[];
  errors: string[];
  details: string[];
}
