import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Building,
  Users,
  HardDrive,
  Briefcase,
  Globe,
  FileText,
  Calendar,
  FileDown,
  Plus,
  Phone,
  Mail,
  ExternalLink,
  Upload,
  Clock,
  History,
  Download,
  AlertCircle,
  FileCheck,
  CheckCircle2,
  ChevronDown,
  ChevronRight,
  Layers,
  Edit2,
  Save,
  X,
  UserCheck,
  UserX,
  Trash2,
} from 'lucide-react';
import {
  clientesApi,
  contactosApi,
  equiposApi,
  serviciosApi,
  websApi,
  documentosApi,
  eventosApi,
} from '../api/services';
import {
  ClienteDetail,
  ClienteRequest,
  ContactoRequest,
  EquipoRequest,
  ServicioRequest,
  WebRequest,
  Documento,
  EventoRequest,
} from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export const ClienteDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const clienteId = Number(id);

  const [cliente, setCliente] = useState<ClienteDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'info' | 'contactos' | 'equipos' | 'servicios' | 'webs' | 'documentos' | 'cronograma'>('info');

  // Modals state
  const [isContactModalOpen, setIsContactModalOpen] = useState(false);
  const [isEquipoModalOpen, setIsEquipoModalOpen] = useState(false);
  const [isServicioModalOpen, setIsServicioModalOpen] = useState(false);
  const [isWebModalOpen, setIsWebModalOpen] = useState(false);
  const [isDocModalOpen, setIsDocModalOpen] = useState(false);
  const [isEventModalOpen, setIsEventModalOpen] = useState(false);
  const [isDocVersionConflictModalOpen, setIsDocVersionConflictModalOpen] = useState(false);
  const [isDocHistoryModalOpen, setIsDocHistoryModalOpen] = useState(false);

  // Forms
  const [contactForm, setContactForm] = useState<ContactoRequest>({ nombre: '', apellidos: '', cargo: '', email: '', telefono: '', telefonoFijo: '', observaciones: '' });
  const [equipoForm, setEquipoForm] = useState<EquipoRequest>({ tipo: 'PC Sobremesa', marca: '', modelo: '', numeroSerie: '', estado: 'OPERATIVO', observaciones: '' });
  const [servicioForm, setServicioForm] = useState<ServicioRequest>({ nombre: '', descripcion: '', estado: 'ACTIVO', observaciones: '' });
  const [webForm, setWebForm] = useState<WebRequest>({ nombre: '', url: '', estado: 'ONLINE', descripcion: '', observaciones: '' });
  const [eventForm, setEventForm] = useState<EventoRequest>({ titulo: '', descripcion: '', fechaInicio: '', prioridad: 'MEDIA', estado: 'PENDIENTE' });

  // Document Upload
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [docCategoria, setDocCategoria] = useState('CONTRATOS');
  const [docDescripcion, setDocDescripcion] = useState('');
  const [uploadProgress, setUploadProgress] = useState<number | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [conflictDocInfo, setConflictDocInfo] = useState<{ id: number; name: string; version: number } | null>(null);
  const [selectedDocForHistory, setSelectedDocForHistory] = useState<Documento | null>(null);
  const [expandedDocIds, setExpandedDocIds] = useState<Record<number, boolean>>({});

  const [isEditingClient, setIsEditingClient] = useState(false);
  const [savingClient, setSavingClient] = useState(false);
  const [editClientForm, setEditClientForm] = useState<ClienteRequest>({
    nombre: '',
    nifCif: '',
    estado: 'ALTA',
    mantenimiento: 'ESTANDAR',
    direccion: '',
    poblacion: '',
    provincia: '',
    gerente: '',
  });

  const { hasPermission } = useAuth();
  const { success, error } = useToast();
  const navigate = useNavigate();

  const loadCliente = useCallback(async () => {
    if (!clienteId) return;
    try {
      setLoading(true);
      const data = await clientesApi.getDetail(clienteId);
      setCliente(data);
    } catch (err) {
      error('Error al cargar la ficha del cliente');
      navigate('/clientes');
    } finally {
      setLoading(false);
    }
  }, [clienteId, error, navigate]);

  useEffect(() => {
    loadCliente();
  }, [loadCliente]);

  const handleStartEditClient = () => {
    if (!cliente) return;
    setEditClientForm({
      nombre: cliente.nombre,
      nifCif: cliente.nifCif,
      estado: cliente.estado || 'ALTA',
      mantenimiento: cliente.mantenimiento || 'ESTANDAR',
      direccion: cliente.direccion || '',
      poblacion: cliente.poblacion || '',
      provincia: cliente.provincia || '',
      gerente: cliente.gerente || '',
    });
    setActiveTab('info');
    setIsEditingClient(true);
  };

  const handleCancelEditClient = () => {
    setIsEditingClient(false);
  };

  const handleDarDeAlta = async () => {
    if (!cliente) return;
    try {
      setLoading(true);
      await clientesApi.darAlta(cliente.id);
      success(`Cliente "${cliente.nombre}" dado de alta exitosamente`);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al dar de alta el cliente');
      setLoading(false);
    }
  };

  const handleDarDeBaja = async () => {
    if (!cliente) return;
    if (!window.confirm(`¿Estás seguro de que deseas dar de baja al cliente "${cliente.nombre}"?`)) return;
    try {
      setLoading(true);
      await clientesApi.deactivate(cliente.id);
      success(`Cliente "${cliente.nombre}" dado de baja`);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al dar de baja el cliente');
      setLoading(false);
    }
  };

  const handleSaveClient = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!editClientForm.nombre.trim()) {
      error('La razón social del cliente es obligatoria');
      return;
    }
    if (!editClientForm.nifCif.trim()) {
      error('El NIF/CIF del cliente es obligatorio');
      return;
    }

    try {
      setSavingClient(true);
      await clientesApi.update(clienteId, {
        ...editClientForm,
        activo: editClientForm.estado === 'ALTA',
      });
      success('Datos del cliente actualizados correctamente');
      setIsEditingClient(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al actualizar el cliente');
    } finally {
      setSavingClient(false);
    }
  };

  const handleDownloadPdf = async () => {
    if (!cliente) return;
    try {
      const blob = await clientesApi.downloadPdf(cliente.id);
      const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Ficha_${cliente.nombre.replace(/\s+/g, '_')}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
      success('Ficha técnica descargada en PDF');
    } catch (err) {
      error('Error generando PDF de la ficha');
    }
  };

  const handleSaveContact = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!contactForm.nombre.trim()) return;
    try {
      await contactosApi.create(clienteId, contactForm);
      success('Contacto guardado');
      setIsContactModalOpen(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error guardando contacto');
    }
  };

  const handleSaveEquipo = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!equipoForm.tipo.trim()) return;
    try {
      await equiposApi.create(clienteId, equipoForm);
      success('Equipo añadido');
      setIsEquipoModalOpen(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error registrando equipo');
    }
  };

  const handleSaveServicio = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!servicioForm.nombre.trim()) return;
    try {
      await serviciosApi.create(clienteId, servicioForm);
      success('Servicio asignado');
      setIsServicioModalOpen(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error guardando servicio');
    }
  };

  const handleSaveWeb = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!webForm.nombre.trim() || !webForm.url.trim()) return;
    try {
      await websApi.create(clienteId, webForm);
      success('Web registrada');
      setIsWebModalOpen(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error guardando web');
    }
  };

  const handleSaveEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!eventForm.titulo.trim() || !eventForm.fechaInicio) return;
    try {
      await eventosApi.create({ ...eventForm, clienteId });
      success('Evento agendado');
      setIsEventModalOpen(false);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error creando evento');
    }
  };

  const handleUploadDocument = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!uploadFile) {
      error('Selecciona un archivo para subir');
      return;
    }

    try {
      setIsUploading(true);
      setUploadProgress(0);
      await documentosApi.upload(clienteId, uploadFile, docCategoria, docDescripcion, (percent) => {
        setUploadProgress(percent);
      });
      success('Documento subido correctamente');
      setIsDocModalOpen(false);
      setUploadFile(null);
      setUploadProgress(null);
      loadCliente();
    } catch (err: any) {
      if (err.response?.status === 409 && err.response?.data?.data?.canCreateNewVersion) {
        const conflictData = err.response.data.data;
        setConflictDocInfo({
          id: conflictData.existingDocumentId,
          name: conflictData.existingDocumentName,
          version: conflictData.currentVersion,
        });
        setIsDocModalOpen(false);
        setIsDocVersionConflictModalOpen(true);
      } else {
        error(err.response?.data?.message || 'Error al subir el documento');
      }
    } finally {
      setIsUploading(false);
    }
  };

  const handleConfirmNewVersion = async () => {
    if (!conflictDocInfo || !uploadFile) return;
    try {
      setIsUploading(true);
      setUploadProgress(0);
      await documentosApi.uploadNewVersion(conflictDocInfo.id, uploadFile, (percent) => {
        setUploadProgress(percent);
      });
      success(`Nueva versión (v${conflictDocInfo.version + 1}) subida con éxito`);
      setIsDocVersionConflictModalOpen(false);
      setUploadFile(null);
      setConflictDocInfo(null);
      setUploadProgress(null);
      loadCliente();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error creando nueva versión');
    } finally {
      setIsUploading(false);
    }
  };

  const handleDownloadDoc = async (docId: number, version?: number, filename?: string) => {
    try {
      const blob = await documentosApi.download(docId, version);
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename || 'documento');
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
      success('Descarga iniciada');
    } catch (err) {
      error('Error al descargar archivo');
    }
  };

  const toggleDocExpand = (docId: number) => {
    setExpandedDocIds((prev) => ({
      ...prev,
      [docId]: !prev[docId],
    }));
  };

  const formatBytes = (bytes: number) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  if (loading || !cliente) {
    return <LoadingSpinner message="Cargando ficha técnica del cliente..." />;
  }

  const tabs = [
    { id: 'info', label: 'Información', icon: <Building className="w-4 h-4" /> },
    { id: 'contactos', label: `Contactos (${cliente.contactos?.length || 0})`, icon: <Users className="w-4 h-4" /> },
    { id: 'equipos', label: `Equipos (${cliente.equipos?.length || 0})`, icon: <HardDrive className="w-4 h-4" /> },
    { id: 'servicios', label: `Servicios (${cliente.servicios?.length || 0})`, icon: <Briefcase className="w-4 h-4" /> },
    { id: 'webs', label: `Sitios Web (${cliente.webs?.length || 0})`, icon: <Globe className="w-4 h-4" /> },
    { id: 'documentos', label: `Documentos (${cliente.documentos?.length || 0})`, icon: <FileText className="w-4 h-4" /> },
    { id: 'cronograma', label: `Cronograma (${cliente.eventos?.length || 0})`, icon: <Calendar className="w-4 h-4" /> },
  ];

  return (
    <div className="space-y-6">
      {/* Top Navigation Back & Actions Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <button
          onClick={() => navigate('/clientes')}
          className="inline-flex items-center gap-2 text-sm font-semibold text-slate-500 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Volver al listado de clientes</span>
        </button>

        <div className="flex items-center gap-2.5 flex-wrap">
          {hasPermission('CLIENTE_EDITAR') && !isEditingClient && (
            <>
              {cliente.estado === 'BAJA' || !cliente.activo ? (
                <Button
                  variant="primary"
                  size="md"
                  onClick={handleDarDeAlta}
                  leftIcon={<UserCheck className="w-4 h-4 text-white" />}
                  className="bg-emerald-600 hover:bg-emerald-700 font-bold shadow-xs text-white border-emerald-600"
                >
                  Dar de alta
                </Button>
              ) : (
                <Button
                  variant="outline"
                  size="md"
                  onClick={handleDarDeBaja}
                  leftIcon={<UserX className="w-4 h-4 text-rose-600" />}
                  className="font-bold text-rose-700 hover:bg-rose-50 hover:border-rose-300"
                >
                  Dar de baja
                </Button>
              )}

              <Button
                variant="outline"
                size="md"
                onClick={handleStartEditClient}
                leftIcon={<Edit2 className="w-4 h-4 text-brand-600" />}
                className="font-bold shadow-xs hover:border-brand-300"
              >
                Editar cliente
              </Button>
            </>
          )}

          {isEditingClient && (
            <>
              <Button
                variant="outline"
                size="md"
                onClick={handleCancelEditClient}
                leftIcon={<X className="w-4 h-4 text-slate-500" />}
                className="font-bold"
              >
                Cancelar
              </Button>
              <Button
                variant="primary"
                size="md"
                onClick={handleSaveClient}
                isLoading={savingClient}
                leftIcon={<Save className="w-4 h-4" />}
                className="font-bold shadow-xs"
              >
                Guardar cambios
              </Button>
            </>
          )}

          <Button
            variant="outline"
            size="md"
            onClick={handleDownloadPdf}
            leftIcon={<FileDown className="w-4 h-4 text-slate-700" />}
          >
            Descargar Ficha PDF
          </Button>
        </div>
      </div>

      {/* Client Header Card */}
      <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-start gap-4">
            <div className="w-14 h-14 rounded-2xl bg-brand-50 border border-brand-100 flex items-center justify-center font-black text-xl text-brand-700 shrink-0 shadow-xs">
              {cliente.nombre.substring(0, 2).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2.5 flex-wrap">
                <h1 className="text-2xl font-black text-slate-900 tracking-tight">{cliente.nombre}</h1>
                <span className="font-mono text-xs px-2.5 py-1 rounded-lg bg-slate-100 border border-slate-200 text-slate-700 font-bold">
                  {cliente.codigo}
                </span>
                <Badge variant={cliente.estado === 'ALTA' && cliente.activo ? 'emerald' : 'rose'} dot size="md">
                  {cliente.estado === 'ALTA' && cliente.activo ? 'ALTA' : 'BAJA'}
                </Badge>
                <Badge variant="slate" size="md">
                  Mantenimiento: {cliente.mantenimiento || 'ESTANDAR'}
                </Badge>
                {isEditingClient && (
                  <span className="inline-flex items-center gap-1 text-xs font-bold px-2 py-0.5 rounded-lg bg-amber-50 text-amber-800 border border-amber-200 animate-pulse">
                    Modo Edición Activo
                  </span>
                )}
              </div>
              <p className="text-xs font-medium text-slate-500 mt-1.5 flex items-center gap-3 flex-wrap">
                <span>NIF/CIF: <strong className="text-slate-800 font-semibold">{cliente.nifCif}</strong></span>
                <span className="text-slate-300">•</span>
                <span>Fecha Alta: <strong className="text-slate-800 font-semibold">{cliente.fechaAlta}</strong></span>
                {cliente.poblacion && (
                  <>
                    <span className="text-slate-300">•</span>
                    <span>Ubicación: <strong className="text-slate-800 font-semibold">{cliente.poblacion} ({cliente.provincia})</strong></span>
                  </>
                )}
              </p>
            </div>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="mt-6 flex items-center gap-1.5 border-b border-slate-200 overflow-x-auto pb-px">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-4 py-2.5 text-xs font-bold rounded-t-xl transition-all border-b-2 whitespace-nowrap ${
                activeTab === tab.id
                  ? 'border-brand-600 text-brand-700 bg-brand-50/70 shadow-xs'
                  : 'border-transparent text-slate-500 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Tab Contents */}
      <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
        {/* TAB 1: INFORMACION */}
        {activeTab === 'info' && (
          <div className="space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">
                {isEditingClient ? 'Editar Datos de la Empresa' : 'Información General de la Empresa'}
              </h3>
              {hasPermission('CLIENTE_EDITAR') && !isEditingClient && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleStartEditClient}
                  leftIcon={<Edit2 className="w-3.5 h-3.5" />}
                >
                  Editar
                </Button>
              )}
            </div>

            {isEditingClient ? (
              <form onSubmit={handleSaveClient} className="space-y-5">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  <Input
                    label="Razón Social *"
                    value={editClientForm.nombre}
                    onChange={(e) => setEditClientForm({ ...editClientForm, nombre: e.target.value })}
                    required
                  />

                  <Input
                    label="NIF / CIF *"
                    value={editClientForm.nifCif}
                    onChange={(e) => setEditClientForm({ ...editClientForm, nifCif: e.target.value })}
                    required
                  />

                  <div>
                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                      Estado del Cliente *
                    </label>
                    <select
                      value={editClientForm.estado}
                      onChange={(e) => setEditClientForm({ ...editClientForm, estado: e.target.value })}
                      className="w-full px-3.5 py-2 text-xs font-semibold rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 bg-white"
                    >
                      <option value="ALTA">Alta</option>
                      <option value="BAJA">Baja</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                      Modalidad de Mantenimiento
                    </label>
                    <select
                      value={editClientForm.mantenimiento}
                      onChange={(e) => setEditClientForm({ ...editClientForm, mantenimiento: e.target.value })}
                      className="w-full px-3.5 py-2 text-xs font-semibold rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 bg-white"
                    >
                      <option value="ESTANDAR">Estándar</option>
                      <option value="PREMIUM">Premium</option>
                      <option value="SIN_MANTENIMIENTO">Sin Mantenimiento</option>
                    </select>
                  </div>

                  <Input
                    label="Dirección"
                    value={editClientForm.direccion}
                    onChange={(e) => setEditClientForm({ ...editClientForm, direccion: e.target.value })}
                  />

                  <Input
                    label="Población"
                    value={editClientForm.poblacion}
                    onChange={(e) => setEditClientForm({ ...editClientForm, poblacion: e.target.value })}
                  />

                  <Input
                    label="Provincia"
                    value={editClientForm.provincia}
                    onChange={(e) => setEditClientForm({ ...editClientForm, provincia: e.target.value })}
                  />

                  <Input
                    label="Gerente / Contacto Principal"
                    value={editClientForm.gerente}
                    onChange={(e) => setEditClientForm({ ...editClientForm, gerente: e.target.value })}
                  />
                </div>

                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                  <Button
                    type="button"
                    variant="outline"
                    size="md"
                    onClick={handleCancelEditClient}
                  >
                    Cancelar
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    size="md"
                    isLoading={savingClient}
                    leftIcon={<Save className="w-4 h-4" />}
                  >
                    Guardar Cambios del Cliente
                  </Button>
                </div>
              </form>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Razón Social</span>
                  <p className="text-sm font-semibold text-slate-900">{cliente.nombre}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">NIF / CIF</span>
                  <p className="text-sm font-black text-brand-700 font-mono">{cliente.nifCif}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Código de Cliente</span>
                  <p className="text-sm font-black text-brand-700 font-mono">{cliente.codigo}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Dirección</span>
                  <p className="text-sm font-semibold text-slate-800">{cliente.direccion || 'No especificada'}</p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Población y Provincia</span>
                  <p className="text-sm font-semibold text-slate-800">
                    {cliente.poblacion || '-'} {cliente.provincia ? `(${cliente.provincia})` : ''}
                  </p>
                </div>
                <div className="p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-1">
                  <span className="text-xs font-bold uppercase tracking-wider text-slate-400">Gerente / Contacto</span>
                  <p className="text-sm font-semibold text-slate-800">{cliente.gerente || 'No especificado'}</p>
                </div>
              </div>
            )}
          </div>
        )}

        {/* TAB 2: CONTACTOS */}
        {activeTab === 'contactos' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Personas de Contacto</h3>
              {hasPermission('CONTACTO_GESTIONAR') && (
                <Button variant="primary" size="sm" onClick={() => setIsContactModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
                  Añadir Contacto
                </Button>
              )}
            </div>
            {cliente.contactos && cliente.contactos.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {cliente.contactos.map((ct) => (
                  <div key={ct.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2">
                    <div className="flex items-start justify-between">
                      <div>
                        <h4 className="font-bold text-slate-900 text-sm">{ct.nombre} {ct.apellidos}</h4>
                        {ct.cargo && <p className="text-xs text-brand-600 font-semibold">{ct.cargo}</p>}
                      </div>
                      <Badge variant="emerald" size="sm">Activo</Badge>
                    </div>
                    <div className="space-y-1 text-xs text-slate-600 pt-2 border-t border-slate-200">
                      {ct.email && (
                        <div className="flex items-center gap-2">
                          <Mail className="w-3.5 h-3.5 text-slate-400" />
                          <a href={`mailto:${ct.email}`} className="hover:text-brand-600 font-medium">{ct.email}</a>
                        </div>
                      )}
                      {ct.telefono && (
                        <div className="flex items-center gap-2">
                          <Phone className="w-3.5 h-3.5 text-slate-400" />
                          <a href={`tel:${ct.telefono}`} className="hover:text-brand-600 font-medium">{ct.telefono}</a>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay contactos"
                description="Registra los responsables de contacto de este cliente."
                actionText={hasPermission('CONTACTO_GESTIONAR') ? 'Añadir Contacto' : undefined}
                onAction={hasPermission('CONTACTO_GESTIONAR') ? () => setIsContactModalOpen(true) : undefined}
              />
            )}
          </div>
        )}

        {/* TAB 3: EQUIPOS */}
        {activeTab === 'equipos' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Parque Informático e Inventario</h3>
              {hasPermission('EQUIPO_CREAR') && (
                <Button variant="primary" size="sm" onClick={() => setIsEquipoModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
                  Añadir Equipo
                </Button>
              )}
            </div>
            {cliente.equipos && cliente.equipos.length > 0 ? (
              <div className="overflow-x-auto border border-slate-200 rounded-xl">
                <table className="w-full text-left text-sm text-slate-700">
                  <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold uppercase text-slate-600">
                    <tr>
                      <th className="px-4 py-3.5">Cód. Inventario</th>
                      <th className="px-4 py-3.5">Tipo</th>
                      <th className="px-4 py-3.5">Marca / Modelo</th>
                      <th className="px-4 py-3.5">Nº Serie</th>
                      <th className="px-4 py-3.5">Estado</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {cliente.equipos.map((eq) => (
                      <tr key={eq.id} className="hover:bg-slate-50">
                        <td className="px-4 py-3 font-mono font-bold text-brand-600 text-xs">{eq.codigoInventario}</td>
                        <td className="px-4 py-3 font-semibold text-slate-900">{eq.tipo}</td>
                        <td className="px-4 py-3">{eq.marca} {eq.modelo}</td>
                        <td className="px-4 py-3 font-mono text-xs text-slate-500">{eq.numeroSerie || '-'}</td>
                        <td className="px-4 py-3">
                          <Badge variant={eq.estado === 'OPERATIVO' ? 'emerald' : 'amber'} size="sm">
                            {eq.estado}
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <EmptyState
                title="No hay equipos"
                description="Registra equipos informáticos y dispositivos del cliente."
                actionText={hasPermission('EQUIPO_CREAR') ? 'Añadir Equipo' : undefined}
                onAction={hasPermission('EQUIPO_CREAR') ? () => setIsEquipoModalOpen(true) : undefined}
              />
            )}
          </div>
        )}

        {/* TAB 4: SERVICIOS */}
        {activeTab === 'servicios' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Servicios Contratados</h3>
              {hasPermission('SERVICIO_GESTIONAR') && (
                <Button variant="primary" size="sm" onClick={() => setIsServicioModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
                  Añadir Servicio
                </Button>
              )}
            </div>
            {cliente.servicios && cliente.servicios.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {cliente.servicios.map((srv) => (
                  <div key={srv.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2">
                    <div className="flex items-start justify-between">
                      <h4 className="font-bold text-slate-900 text-sm">{srv.nombre}</h4>
                      <Badge variant={srv.estado === 'ACTIVO' ? 'emerald' : 'slate'} size="sm">{srv.estado}</Badge>
                    </div>
                    {srv.descripcion && <p className="text-xs text-slate-500">{srv.descripcion}</p>}
                    <div className="pt-2 border-t border-slate-200 text-xs text-slate-400">
                      Vigencia: {srv.fechaInicio || 'Inicio n/d'} - {srv.fechaFin || 'Indefinido'}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay servicios"
                description="Asigna contratos o servicios técnicos para este cliente."
                actionText={hasPermission('SERVICIO_GESTIONAR') ? 'Añadir Servicio' : undefined}
                onAction={hasPermission('SERVICIO_GESTIONAR') ? () => setIsServicioModalOpen(true) : undefined}
              />
            )}
          </div>
        )}

        {/* TAB 5: WEBS */}
        {activeTab === 'webs' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Sitios Web Gestionados</h3>
              {hasPermission('WEB_GESTIONAR') && (
                <Button variant="primary" size="sm" onClick={() => setIsWebModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
                  Añadir Web
                </Button>
              )}
            </div>
            {cliente.webs && cliente.webs.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {cliente.webs.map((w) => (
                  <div key={w.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between">
                    <div>
                      <h4 className="font-bold text-slate-900 text-sm">{w.nombre}</h4>
                      <a
                        href={w.url}
                        target="_blank"
                        rel="noreferrer"
                        className="text-xs text-brand-600 hover:underline inline-flex items-center gap-1 mt-0.5 font-mono font-medium"
                      >
                        {w.url}
                        <ExternalLink className="w-3 h-3" />
                      </a>
                    </div>
                    <Badge variant={w.estado === 'ONLINE' ? 'emerald' : 'amber'} size="sm">{w.estado}</Badge>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay sitios web"
                description="Registra páginas web y dominios del cliente."
                actionText={hasPermission('WEB_GESTIONAR') ? 'Añadir Web' : undefined}
                onAction={hasPermission('WEB_GESTIONAR') ? () => setIsWebModalOpen(true) : undefined}
              />
            )}
          </div>
        )}

        {/* TAB 6: DOCUMENTOS CON ÁRBOL DE VERSIONES */}
        {activeTab === 'documentos' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-900">Gestor Documental y Versiones</h3>
                <p className="text-xs text-slate-500">
                  Formatos admitidos: DOCX, PDF, XLSX, PPTX, TXT, imágenes, ZIP
                </p>
              </div>
              {hasPermission('DOCUMENTO_SUBIR') && (
                <Button variant="primary" size="sm" onClick={() => setIsDocModalOpen(true)} leftIcon={<Upload className="w-4 h-4" />}>
                  Subir Documento
                </Button>
              )}
            </div>

            {cliente.documentos && cliente.documentos.length > 0 ? (
              <div className="space-y-3">
                {cliente.documentos.map((doc) => {
                  const isExpanded = expandedDocIds[doc.id] ?? false;
                  return (
                    <div
                      key={doc.id}
                      className="rounded-2xl border border-slate-200 bg-white overflow-hidden shadow-xs hover:border-slate-300 transition-all"
                    >
                      {/* Document Header Row */}
                      <div className="p-4.5 bg-slate-50/70 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                        <div className="flex items-start gap-3">
                          <div className="w-10 h-10 rounded-xl bg-white border border-slate-200 flex items-center justify-center text-brand-600 shadow-2xs shrink-0">
                            <FileCheck className="w-5 h-5" />
                          </div>
                          <div>
                            <div className="flex items-center gap-2 flex-wrap">
                              <h4 className="font-bold text-slate-900 text-sm">{doc.nombreOriginal}</h4>
                              <Badge variant="purple" size="sm">
                                v{doc.versionActual} Actual
                              </Badge>
                              <Badge variant="cyan" size="sm">
                                {doc.categoria}
                              </Badge>
                            </div>
                            {doc.descripcion && (
                              <p className="text-xs text-slate-500 mt-0.5">{doc.descripcion}</p>
                            )}
                            <p className="text-[11px] text-slate-400 mt-1">
                              Última modificación:{' '}
                              {doc.fechaModificacion
                                ? format(new Date(doc.fechaModificacion), 'dd/MM/yyyy HH:mm', { locale: es })
                                : '-'}
                            </p>
                          </div>
                        </div>

                        {/* Actions */}
                        <div className="flex items-center gap-2 self-end sm:self-center">
                          {doc.versiones && doc.versiones.length > 1 && (
                            <button
                              onClick={() => toggleDocExpand(doc.id)}
                              className="px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 bg-white hover:bg-slate-50 flex items-center gap-1.5 transition-colors"
                            >
                              <History className="w-3.5 h-3.5 text-slate-500" />
                              <span>{doc.versiones.length} versiones</span>
                              {isExpanded ? <ChevronDown className="w-3.5 h-3.5" /> : <ChevronRight className="w-3.5 h-3.5" />}
                            </button>
                          )}
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => handleDownloadDoc(doc.id, doc.versionActual, doc.nombreOriginal)}
                            leftIcon={<Download className="w-4 h-4 text-slate-700" />}
                          >
                            Descargar (v{doc.versionActual})
                          </Button>
                        </div>
                      </div>

                      {/* Versions Hierarchical Tree / Sub-rows */}
                      {doc.versiones && doc.versiones.length > 0 && (
                        <div className={`divide-y divide-slate-100 border-t border-slate-100 ${!isExpanded && doc.versiones.length > 1 ? 'hidden' : 'block'}`}>
                          {doc.versiones.map((ver) => (
                            <div
                              key={ver.id}
                              className="p-3 pl-8 sm:pl-12 flex items-center justify-between text-xs hover:bg-slate-50/50 transition-colors"
                            >
                              <div className="flex items-center gap-2.5">
                                <span className={`font-mono font-bold px-2 py-0.5 rounded text-[11px] ${
                                  ver.version === doc.versionActual
                                    ? 'bg-purple-100 text-purple-800'
                                    : 'bg-slate-100 text-slate-600'
                                }`}>
                                  v{ver.version} {ver.version === doc.versionActual ? '(Actual)' : ''}
                                </span>
                                <span className="font-semibold text-slate-800">{ver.nombreArchivo}</span>
                                <span className="text-slate-400 font-mono">({formatBytes(ver.tamano)})</span>
                                <span className="text-slate-400">
                                  por <strong className="text-slate-600">{ver.usuarioNombre || 'Sistema'}</strong> el{' '}
                                  {format(new Date(ver.fechaSubida), 'dd/MM/yyyy HH:mm', { locale: es })}
                                </span>
                              </div>

                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => handleDownloadDoc(doc.id, ver.version, ver.nombreArchivo)}
                                leftIcon={<Download className="w-3.5 h-3.5" />}
                              >
                                Descargar
                              </Button>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            ) : (
              <EmptyState
                title="No hay documentos"
                description="Sube contratos, manuales, actas o certificados técnicos."
                actionText={hasPermission('DOCUMENTO_SUBIR') ? 'Subir Documento' : undefined}
                onAction={hasPermission('DOCUMENTO_SUBIR') ? () => setIsDocModalOpen(true) : undefined}
              />
            )}
          </div>
        )}

        {/* TAB 7: CRONOGRAMA */}
        {activeTab === 'cronograma' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-bold text-slate-900">Intervenciones del Cliente</h3>
              {hasPermission('CRONOGRAMA_GESTIONAR') && (
                <Button variant="primary" size="sm" onClick={() => setIsEventModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
                  Agendar Evento
                </Button>
              )}
            </div>
            {cliente.eventos && cliente.eventos.length > 0 ? (
              <div className="space-y-3">
                {cliente.eventos.map((ev) => (
                  <div key={ev.id} className="p-4 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between">
                    <div>
                      <div className="flex items-center gap-2">
                        <h4 className="font-bold text-slate-900 text-sm">{ev.titulo}</h4>
                        <Badge variant={ev.prioridad === 'URGENTE' ? 'rose' : 'amber'} size="sm">{ev.prioridad}</Badge>
                        <Badge variant={ev.estado === 'COMPLETADO' ? 'emerald' : 'brand'} size="sm">{ev.estado}</Badge>
                      </div>
                      <p className="text-xs text-slate-500 mt-1 flex items-center gap-1.5">
                        <Clock className="w-3.5 h-3.5" />
                        <span>{format(new Date(ev.fechaInicio), "d 'de' MMMM yyyy, HH:mm", { locale: es })}</span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <EmptyState
                title="No hay eventos"
                description="Agrega revisiones o mantenimientos en el calendario."
                actionText={hasPermission('CRONOGRAMA_GESTIONAR') ? 'Agendar Evento' : undefined}
                onAction={hasPermission('CRONOGRAMA_GESTIONAR') ? () => setIsEventModalOpen(true) : undefined}
              />
            )}
          </div>
        )}
      </div>

      {/* Modal: Subir Documento con Barra de Progreso */}
      <Modal
        isOpen={isDocModalOpen}
        onClose={() => !isUploading && setIsDocModalOpen(false)}
        title="Subir Nuevo Documento"
        size="md"
      >
        <form onSubmit={handleUploadDocument} className="space-y-4">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
              Archivo a Subir * (DOCX, PDF, XLSX, PPTX, TXT, ZIP, Imágenes)
            </label>
            <input
              type="file"
              required
              disabled={isUploading}
              onChange={(e) => setUploadFile(e.target.files ? e.target.files[0] : null)}
              className="w-full text-xs text-slate-600 file:mr-4 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-xs file:font-bold file:bg-brand-600 file:text-white hover:file:bg-brand-700 cursor-pointer"
            />
          </div>

          <Select
            label="Categoría del Documento"
            value={docCategoria}
            disabled={isUploading}
            onChange={(e) => setDocCategoria(e.target.value)}
          >
            <option value="CONTRATOS">Contratos y Acuerdos</option>
            <option value="FACTURAS">Facturas y Presupuestos</option>
            <option value="INFORMES_TECNICOS">Informes Técnicos</option>
            <option value="CERTIFICADOS">Certificados y Garantías</option>
            <option value="GENERAL">General</option>
          </Select>

          <Input
            label="Descripción o Notas (Opcional)"
            placeholder="Detalles sobre el contenido del documento..."
            value={docDescripcion}
            disabled={isUploading}
            onChange={(e) => setDocDescripcion(e.target.value)}
          />

          {/* Real upload progress bar */}
          {isUploading && uploadProgress !== null && (
            <div className="space-y-1.5 pt-2">
              <div className="flex items-center justify-between text-xs font-bold text-slate-700">
                <span>Subiendo archivo al servidor...</span>
                <span className="font-mono text-brand-600">{uploadProgress}%</span>
              </div>
              <div className="w-full bg-slate-200 rounded-full h-2.5 overflow-hidden">
                <div
                  className="bg-brand-600 h-2.5 rounded-full transition-all duration-300"
                  style={{ width: `${uploadProgress}%` }}
                />
              </div>
            </div>
          )}

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              type="button"
              variant="outline"
              disabled={isUploading}
              onClick={() => setIsDocModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="primary"
              isLoading={isUploading}
            >
              {isUploading ? `Subiendo (${uploadProgress || 0}%)` : 'Subir Documento'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Conflicto de Documento & Prompt para Nueva Versión */}
      <Modal
        isOpen={isDocVersionConflictModalOpen}
        onClose={() => !isUploading && setIsDocVersionConflictModalOpen(false)}
        title="Documento existente detectado"
        size="md"
      >
        <div className="space-y-4 text-sm text-slate-700">
          <div className="flex items-start gap-3 p-4 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-xs">
            <AlertCircle className="w-5 h-5 shrink-0 text-amber-600 mt-0.5" />
            <div>
              Ya existe el documento <strong>{conflictDocInfo?.name}</strong> con versión actual <strong>v{conflictDocInfo?.version}</strong> para este cliente.
            </div>
          </div>
          <p className="text-xs text-slate-600">
            El sistema ABAXIAL mantiene un historial inmutable. ¿Deseas almacenar este archivo como la <strong>versión v{(conflictDocInfo?.version || 1) + 1}</strong>?
          </p>

          {isUploading && uploadProgress !== null && (
            <div className="space-y-1.5 pt-2">
              <div className="flex items-center justify-between text-xs font-bold text-slate-700">
                <span>Subiendo nueva versión...</span>
                <span className="font-mono text-brand-600">{uploadProgress}%</span>
              </div>
              <div className="w-full bg-slate-200 rounded-full h-2.5 overflow-hidden">
                <div
                  className="bg-brand-600 h-2.5 rounded-full transition-all duration-300"
                  style={{ width: `${uploadProgress}%` }}
                />
              </div>
            </div>
          )}

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              variant="outline"
              disabled={isUploading}
              onClick={() => setIsDocVersionConflictModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button
              variant="primary"
              isLoading={isUploading}
              onClick={handleConfirmNewVersion}
            >
              Subir como Versión v{(conflictDocInfo?.version || 1) + 1}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Modal: Añadir Contacto */}
      <Modal
        isOpen={isContactModalOpen}
        onClose={() => setIsContactModalOpen(false)}
        title="Añadir Persona de Contacto"
        size="md"
      >
        <form onSubmit={handleSaveContact} className="space-y-3">
          <Input label="Nombre *" required value={contactForm.nombre} onChange={(e) => setContactForm({ ...contactForm, nombre: e.target.value })} />
          <Input label="Apellidos" value={contactForm.apellidos || ''} onChange={(e) => setContactForm({ ...contactForm, apellidos: e.target.value })} />
          <Input label="Cargo o Puesto" value={contactForm.cargo || ''} onChange={(e) => setContactForm({ ...contactForm, cargo: e.target.value })} />
          <Input label="Email" type="email" value={contactForm.email || ''} onChange={(e) => setContactForm({ ...contactForm, email: e.target.value })} />
          <Input label="Teléfono Móvil" value={contactForm.telefono || ''} onChange={(e) => setContactForm({ ...contactForm, telefono: e.target.value })} />
          <Input label="Teléfono Fijo" value={contactForm.telefonoFijo || ''} onChange={(e) => setContactForm({ ...contactForm, telefonoFijo: e.target.value })} />
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsContactModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" type="submit">Guardar Contacto</Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Añadir Equipo */}
      <Modal
        isOpen={isEquipoModalOpen}
        onClose={() => setIsEquipoModalOpen(false)}
        title="Añadir Equipo al Inventario"
        size="md"
      >
        <form onSubmit={handleSaveEquipo} className="space-y-3">
          <Select label="Tipo de Dispositivo" value={equipoForm.tipo} onChange={(e) => setEquipoForm({ ...equipoForm, tipo: e.target.value })}>
            <option value="PC Sobremesa">PC Sobremesa</option>
            <option value="Portátil">Portátil</option>
            <option value="Servidor">Servidor</option>
            <option value="Router / Switch">Router / Switch</option>
            <option value="Impresora">Impresora</option>
            <option value="SAI / UPS">SAI / UPS</option>
            <option value="Otro">Otro</option>
          </Select>
          <Input label="Marca" value={equipoForm.marca || ''} onChange={(e) => setEquipoForm({ ...equipoForm, marca: e.target.value })} />
          <Input label="Modelo" value={equipoForm.modelo || ''} onChange={(e) => setEquipoForm({ ...equipoForm, modelo: e.target.value })} />
          <Input label="Número de Serie" value={equipoForm.numeroSerie || ''} onChange={(e) => setEquipoForm({ ...equipoForm, numeroSerie: e.target.value })} />
          <Select label="Estado Operativo" value={equipoForm.estado || 'OPERATIVO'} onChange={(e) => setEquipoForm({ ...equipoForm, estado: e.target.value })}>
            <option value="OPERATIVO">OPERATIVO</option>
            <option value="EN_REPARACION">EN REPARACIÓN</option>
            <option value="OBSOLETO">OBSOLETO</option>
            <option value="BAJA">BAJA</option>
          </Select>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsEquipoModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" type="submit">Guardar Equipo</Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Añadir Servicio */}
      <Modal
        isOpen={isServicioModalOpen}
        onClose={() => setIsServicioModalOpen(false)}
        title="Asignar Servicio Técnico"
        size="md"
      >
        <form onSubmit={handleSaveServicio} className="space-y-3">
          <Input label="Nombre del Servicio *" required placeholder="Ej. Copias de Seguridad Cloud" value={servicioForm.nombre} onChange={(e) => setServicioForm({ ...servicioForm, nombre: e.target.value })} />
          <Input label="Descripción" value={servicioForm.descripcion || ''} onChange={(e) => setServicioForm({ ...servicioForm, descripcion: e.target.value })} />
          <Select label="Estado" value={servicioForm.estado || 'ACTIVO'} onChange={(e) => setServicioForm({ ...servicioForm, estado: e.target.value })}>
            <option value="ACTIVO">ACTIVO</option>
            <option value="PAUSADO">PAUSADO</option>
            <option value="FINALIZADO">FINALIZADO</option>
          </Select>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsServicioModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" type="submit">Guardar Servicio</Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Añadir Web */}
      <Modal
        isOpen={isWebModalOpen}
        onClose={() => setIsWebModalOpen(false)}
        title="Registrar Sitio Web del Cliente"
        size="md"
      >
        <form onSubmit={handleSaveWeb} className="space-y-3">
          <Input label="Nombre o Proyecto *" required placeholder="Ej. Web Corporativa" value={webForm.nombre} onChange={(e) => setWebForm({ ...webForm, nombre: e.target.value })} />
          <Input label="URL Completa *" required placeholder="https://ejemplo.com" value={webForm.url} onChange={(e) => setWebForm({ ...webForm, url: e.target.value })} />
          <Select label="Estado" value={webForm.estado || 'ONLINE'} onChange={(e) => setWebForm({ ...webForm, estado: e.target.value })}>
            <option value="ONLINE">ONLINE</option>
            <option value="OFFLINE">OFFLINE</option>
            <option value="MANTENIMIENTO">MANTENIMIENTO</option>
          </Select>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsWebModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" type="submit">Guardar Web</Button>
          </div>
        </form>
      </Modal>

      {/* Modal: Añadir Evento */}
      <Modal
        isOpen={isEventModalOpen}
        onClose={() => setIsEventModalOpen(false)}
        title="Agendar Intervención"
        size="md"
      >
        <form onSubmit={handleSaveEvent} className="space-y-3">
          <Input label="Título del Evento *" required placeholder="Ej. Mantenimiento Preventivo Trimestral" value={eventForm.titulo} onChange={(e) => setEventForm({ ...eventForm, titulo: e.target.value })} />
          <Input label="Fecha y Hora de Inicio *" type="datetime-local" required value={eventForm.fechaInicio} onChange={(e) => setEventForm({ ...eventForm, fechaInicio: e.target.value })} />
          <Select label="Prioridad" value={eventForm.prioridad || 'MEDIA'} onChange={(e) => setEventForm({ ...eventForm, prioridad: e.target.value })}>
            <option value="BAJA">BAJA</option>
            <option value="MEDIA">MEDIA</option>
            <option value="ALTA">ALTA</option>
            <option value="URGENTE">URGENTE</option>
          </Select>
          <Input label="Descripción y Notas" value={eventForm.descripcion || ''} onChange={(e) => setEventForm({ ...eventForm, descripcion: e.target.value })} />
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" type="button" onClick={() => setIsEventModalOpen(false)}>Cancelar</Button>
            <Button variant="primary" type="submit">Guardar Evento</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
