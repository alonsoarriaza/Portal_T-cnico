import React, { useEffect, useState, useCallback, useMemo } from 'react';
import {
  Calendar as CalendarIcon,
  ChevronLeft,
  ChevronRight,
  Plus,
  Clock,
  User,
  Building,
  CheckCircle2,
  AlertCircle,
  Clock3,
  Edit2,
  Trash2,
  Search,
  List,
  CalendarDays,
  CalendarRange,
  Lock,
  Globe,
  Repeat,
  Layers,
  FileCheck,
  X,
  AlertTriangle,
} from 'lucide-react';
import {
  format,
  addMonths,
  subMonths,
  startOfWeek,
  endOfWeek,
  startOfMonth,
  endOfMonth,
  eachDayOfInterval,
  isSameMonth,
  isSameDay,
  isToday,
  addDays,
  subDays,
  addWeeks,
  subWeeks,
  parseISO,
} from 'date-fns';
import { es } from 'date-fns/locale';
import { eventosApi, clientesApi } from '../api/services';
import { Evento, EventoRequest, ClienteList } from '../types';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Input } from '../components/common/Input';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

type ViewMode = 'month' | 'week' | 'day' | 'agenda';
type TipoFilter = '' | 'RECURRENTE' | 'PUNTUAL';

const ESTADOS = [
  { value: '', label: 'Todos los estados' },
  { value: 'PENDIENTE', label: 'Pendiente' },
  { value: 'EN_PROCESO', label: 'En Proceso' },
  { value: 'COMPLETADO', label: 'Completado' },
  { value: 'CANCELADO', label: 'Cancelado' },
];

const PRIORIDADES = [
  { value: '', label: 'Todas las prioridades' },
  { value: 'BAJA', label: 'Baja' },
  { value: 'MEDIA', label: 'Media' },
  { value: 'ALTA', label: 'Alta' },
  { value: 'URGENTE', label: 'Urgente' },
];

const DIAS_SEMANA_OPCIONES = [
  { num: 1, label: 'Lunes', short: 'L' },
  { num: 2, label: 'Martes', short: 'M' },
  { num: 3, label: 'Miércoles', short: 'X' },
  { num: 4, label: 'Jueves', short: 'J' },
  { num: 5, label: 'Viernes', short: 'V' },
  { num: 6, label: 'Sábado', short: 'S' },
  { num: 7, label: 'Domingo', short: 'D' },
];

export const CronogramaPage: React.FC = () => {
  const [currentDate, setCurrentDate] = useState<Date>(new Date());
  const [viewMode, setViewMode] = useState<ViewMode>('month');
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [clientes, setClientes] = useState<ClienteList[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [filtroTipo, setFiltroTipo] = useState<TipoFilter>('');
  const [filtroEstado, setFiltroEstado] = useState<string>('');
  const [filtroPrioridad, setFiltroPrioridad] = useState<string>('');
  const [filtroCliente, setFiltroCliente] = useState<string>('');
  const [filtroVisibilidad, setFiltroVisibilidad] = useState<string>('');
  const [busqueda, setBusqueda] = useState<string>('');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isScopeChoiceModalOpen, setIsScopeChoiceModalOpen] = useState(false);
  const [scopeActionType, setScopeActionType] = useState<'EDIT' | 'DELETE'>('EDIT');
  const [selectedEvento, setSelectedEvento] = useState<Evento | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

  // Recurrence Form State
  const [selectedDiasSemana, setSelectedDiasSemana] = useState<number[]>([1]);
  const [tieneFechaFinRecurrencia, setTieneFechaFinRecurrencia] = useState(false);
  const [editScope, setEditScope] = useState<'SERIE' | 'INSTANCIA'>('SERIE');

  // Form State
  const [formData, setFormData] = useState<EventoRequest>({
    titulo: '',
    descripcion: '',
    fechaInicio: '',
    fechaFin: '',
    tipo: 'PUNTUAL',
    recurrencia: 'SEMANAL',
    fechaFinRecurrencia: '',
    diasSemana: '1',
    diaMes: 1,
    prioridad: 'MEDIA',
    estado: 'PENDIENTE',
    visibilidad: 'COMPARTIDO',
    clienteId: undefined,
  });

  const { hasPermission } = useAuth();
  const { success, error } = useToast();

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      // Calcular ventana de carga (3 meses hacia atrás y 12 meses hacia adelante)
      const desdeIso = format(subMonths(currentDate, 3), "yyyy-MM-dd'T'00:00:00");
      const hastaIso = format(addMonths(currentDate, 12), "yyyy-MM-dd'T'23:59:59");

      const [eventsData, clientsData] = await Promise.all([
        eventosApi.list({ desde: desdeIso, hasta: hastaIso }),
        clientesApi.list({ size: 100 }),
      ]);
      setEventos(eventsData || []);
      setClientes(clientsData.content || []);
    } catch (err) {
      error('Error al cargar eventos del cronograma');
    } finally {
      setLoading(false);
    }
  }, [currentDate, error]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Navigation handlers
  const handlePrevious = () => {
    if (viewMode === 'month') setCurrentDate((prev) => subMonths(prev, 1));
    else if (viewMode === 'week') setCurrentDate((prev) => subWeeks(prev, 1));
    else if (viewMode === 'day') setCurrentDate((prev) => subDays(prev, 1));
  };

  const handleNext = () => {
    if (viewMode === 'month') setCurrentDate((prev) => addMonths(prev, 1));
    else if (viewMode === 'week') setCurrentDate((prev) => addWeeks(prev, 1));
    else if (viewMode === 'day') setCurrentDate((prev) => addDays(prev, 1));
  };

  const handleToday = () => {
    setCurrentDate(new Date());
  };

  // Filtered events
  const filteredEventos = useMemo(() => {
    return eventos.filter((ev) => {
      if (filtroTipo) {
        const evTipo = ev.tipo?.toUpperCase() || 'PUNTUAL';
        if (evTipo !== filtroTipo) return false;
      }
      if (filtroEstado && ev.estado !== filtroEstado) return false;
      if (filtroPrioridad && ev.prioridad !== filtroPrioridad) return false;
      if (filtroCliente && ev.clienteId?.toString() !== filtroCliente) return false;
      if (filtroVisibilidad && ev.visibilidad !== filtroVisibilidad) return false;
      if (busqueda) {
        const query = busqueda.toLowerCase();
        const matchesTitulo = ev.titulo.toLowerCase().includes(query);
        const matchesDesc = ev.descripcion?.toLowerCase().includes(query);
        const matchesCliente = ev.clienteNombre?.toLowerCase().includes(query);
        if (!matchesTitulo && !matchesDesc && !matchesCliente) return false;
      }
      return true;
    });
  }, [eventos, filtroTipo, filtroEstado, filtroPrioridad, filtroCliente, filtroVisibilidad, busqueda]);

  // Helpers for calendar grid
  const monthStart = startOfMonth(currentDate);
  const monthEnd = endOfMonth(monthStart);
  const startDate = startOfWeek(monthStart, { weekStartsOn: 1 });
  const endDate = endOfWeek(monthEnd, { weekStartsOn: 1 });
  const monthDays = eachDayOfInterval({ start: startDate, end: endDate });

  const weekStart = startOfWeek(currentDate, { weekStartsOn: 1 });
  const weekDays = eachDayOfInterval({
    start: weekStart,
    end: endOfWeek(currentDate, { weekStartsOn: 1 }),
  });

  const getEventsForDay = (day: Date) => {
    return filteredEventos.filter((ev) => {
      try {
        const evDate = parseISO(ev.fechaInicio);
        return isSameDay(evDate, day);
      } catch {
        return false;
      }
    });
  };

  const openCreateModal = (date?: Date) => {
    const initialDate = date || new Date();
    const defaultStart = format(initialDate, "yyyy-MM-dd'T'09:00");
    const defaultEnd = format(initialDate, "yyyy-MM-dd'T'10:00");
    const dayOfWeek = initialDate.getDay() === 0 ? 7 : initialDate.getDay();

    setSelectedEvento(null);
    setSelectedDiasSemana([dayOfWeek]);
    setTieneFechaFinRecurrencia(false);
    setEditScope('SERIE');

    setFormData({
      titulo: '',
      descripcion: '',
      fechaInicio: defaultStart,
      fechaFin: defaultEnd,
      tipo: 'PUNTUAL',
      recurrencia: 'SEMANAL',
      fechaFinRecurrencia: '',
      diasSemana: String(dayOfWeek),
      diaMes: initialDate.getDate(),
      prioridad: 'MEDIA',
      estado: 'PENDIENTE',
      visibilidad: 'COMPARTIDO',
      clienteId: undefined,
    });
    setIsModalOpen(true);
  };

  const handleRequestEdit = (evento: Evento) => {
    setSelectedEvento(evento);
    if (evento.tipo === 'RECURRENTE' || evento.esRecurrente || evento.eventoPadreId) {
      setScopeActionType('EDIT');
      setIsDetailOpen(false);
      setIsScopeChoiceModalOpen(true);
    } else {
      openEditModal(evento, 'SERIE');
    }
  };

  const handleRequestDelete = (evento: Evento) => {
    setSelectedEvento(evento);
    if (evento.tipo === 'RECURRENTE' || evento.esRecurrente || evento.eventoPadreId) {
      setScopeActionType('DELETE');
      setIsDetailOpen(false);
      setIsScopeChoiceModalOpen(true);
    } else {
      executeDelete(evento.id);
    }
  };

  const openEditModal = (evento: Evento, scope: 'SERIE' | 'INSTANCIA') => {
    setSelectedEvento(evento);
    setEditScope(scope);
    setIsScopeChoiceModalOpen(false);
    setIsDetailOpen(false);

    const initialDate = evento.fechaInicio ? parseISO(evento.fechaInicio) : new Date();
    const dayOfWeek = initialDate.getDay() === 0 ? 7 : initialDate.getDay();

    // Parse días semana si existe
    if (evento.diasSemana) {
      const parsed = evento.diasSemana.split(',').map((s) => Number(s.trim())).filter((n) => !isNaN(n));
      setSelectedDiasSemana(parsed.length > 0 ? parsed : [dayOfWeek]);
    } else {
      setSelectedDiasSemana([dayOfWeek]);
    }

    setTieneFechaFinRecurrencia(Boolean(evento.fechaFinRecurrencia));

    setFormData({
      titulo: evento.titulo,
      descripcion: evento.descripcion || '',
      fechaInicio: evento.fechaInicio ? evento.fechaInicio.slice(0, 16) : '',
      fechaFin: evento.fechaFin ? evento.fechaFin.slice(0, 16) : '',
      tipo: scope === 'INSTANCIA' ? 'PUNTUAL' : evento.tipo || 'PUNTUAL',
      recurrencia: evento.recurrencia || 'SEMANAL',
      fechaFinRecurrencia: evento.fechaFinRecurrencia ? evento.fechaFinRecurrencia.slice(0, 16) : '',
      diasSemana: evento.diasSemana || String(dayOfWeek),
      diaMes: evento.diaMes || initialDate.getDate(),
      prioridad: evento.prioridad || 'MEDIA',
      estado: evento.estado || 'PENDIENTE',
      visibilidad: evento.visibilidad || 'COMPARTIDO',
      clienteId: evento.clienteId,
      fechaOriginalOcurrencia: evento.fechaOriginalOcurrencia || evento.fechaInicio,
    });

    setIsModalOpen(true);
  };

  const openDetailModal = (evento: Evento) => {
    setSelectedEvento(evento);
    setIsDetailOpen(true);
  };

  const handleToggleDiaSemana = (diaNum: number) => {
    setSelectedDiasSemana((prev) => {
      if (prev.includes(diaNum)) {
        if (prev.length === 1) return prev; // Mantener al menos uno
        return prev.filter((d) => d !== diaNum);
      } else {
        return [...prev, diaNum].sort((a, b) => a - b);
      }
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.titulo.trim()) {
      error('El título es obligatorio');
      return;
    }
    if (!formData.fechaInicio) {
      error('La fecha de inicio es obligatoria');
      return;
    }

    try {
      setModalLoading(true);
      const payload: EventoRequest = {
        ...formData,
        diasSemana: selectedDiasSemana.join(','),
        fechaFinRecurrencia: tieneFechaFinRecurrencia && formData.fechaFinRecurrencia ? formData.fechaFinRecurrencia : undefined,
      };

      if (selectedEvento) {
        const parentId = selectedEvento.eventoPadreId || selectedEvento.id;

        if (editScope === 'INSTANCIA') {
          // Modificar solo esta ocurrencia
          payload.fechaOriginalOcurrencia = selectedEvento.fechaOriginalOcurrencia || selectedEvento.fechaInicio;
          await eventosApi.modificarExcepcion(parentId, payload);
          success('Instancia del evento modificada correctamente');
        } else {
          // Modificar toda la serie
          await eventosApi.update(parentId, payload);
          success('Serie de eventos actualizada correctamente');
        }
      } else {
        await eventosApi.create(payload);
        success('Evento programado exitosamente');
      }

      setIsModalOpen(false);
      loadData();
    } catch (err) {
      error('Error al guardar el evento');
    } finally {
      setModalLoading(false);
    }
  };

  const executeDelete = async (id: number) => {
    if (!window.confirm('¿Seguro que deseas eliminar este evento del cronograma?')) return;
    try {
      await eventosApi.deactivate(id);
      success('Evento eliminado correctamente');
      setIsDetailOpen(false);
      setIsModalOpen(false);
      setIsScopeChoiceModalOpen(false);
      loadData();
    } catch (err) {
      error('Error al eliminar el evento');
    }
  };

  const handleDeleteOccurrenceOnly = async () => {
    if (!selectedEvento) return;
    try {
      const parentId = selectedEvento.eventoPadreId || selectedEvento.id;
      const fechaOriginal = selectedEvento.fechaOriginalOcurrencia || selectedEvento.fechaInicio;
      await eventosApi.excluirOcurrencia(parentId, fechaOriginal);
      success('Ocurrencia eliminada de la serie correctamente');
      setIsScopeChoiceModalOpen(false);
      setIsDetailOpen(false);
      loadData();
    } catch (err) {
      error('Error al excluir la ocurrencia de la serie');
    }
  };

  const handleDeleteWholeSeries = async () => {
    if (!selectedEvento) return;
    const parentId = selectedEvento.eventoPadreId || selectedEvento.id;
    executeDelete(parentId);
  };

  const handleToggleEstado = async (evento: Evento) => {
    try {
      const nuevoEstado = evento.estado === 'COMPLETADO' ? 'PENDIENTE' : 'COMPLETADO';
      if (evento.esRecurrente && !evento.esExcepcion) {
        // Guardar excepción de estado para esta fecha
        const parentId = evento.eventoPadreId || evento.id;
        await eventosApi.modificarExcepcion(parentId, {
          ...evento,
          estado: nuevoEstado,
          fechaOriginalOcurrencia: evento.fechaOriginalOcurrencia || evento.fechaInicio,
        });
      } else {
        await eventosApi.update(evento.id, {
          ...evento,
          estado: nuevoEstado,
        });
      }
      success(`Evento marcado como ${nuevoEstado.toLowerCase()}`);
      loadData();
    } catch (err) {
      error('Error al actualizar el estado');
    }
  };

  // Color identificativo corporativo sin emojis:
  // RECURRENTE / FIJO -> Morado corporativo
  // ESPORÁDICO / RECORDATORIO -> Verde claro corporativo
  const getEventChipStyles = (tipo?: string, estado?: string) => {
    const isCompleted = estado === 'COMPLETADO';
    if (tipo === 'RECURRENTE') {
      return isCompleted
        ? 'bg-purple-50/50 border-purple-200 text-purple-700/60 line-through'
        : 'bg-purple-50 border-purple-200 text-purple-900 hover:bg-purple-100/90 shadow-2xs';
    }
    return isCompleted
      ? 'bg-emerald-50/50 border-emerald-200 text-emerald-700/60 line-through'
      : 'bg-emerald-50 border-emerald-200 text-emerald-900 hover:bg-emerald-100/90 shadow-2xs';
  };

  const getStatusBadgeVariant = (estado: string) => {
    switch (estado) {
      case 'COMPLETADO':
        return 'emerald';
      case 'EN_PROCESO':
        return 'brand';
      case 'CANCELADO':
        return 'rose';
      default:
        return 'amber';
    }
  };

  const getPriorityBadgeVariant = (prioridad: string) => {
    switch (prioridad) {
      case 'URGENTE':
        return 'rose';
      case 'ALTA':
        return 'amber';
      case 'MEDIA':
        return 'brand';
      default:
        return 'slate';
    }
  };

  const getRecurrenciaText = (ev: Evento) => {
    const freq = ev.recurrencia?.toUpperCase() || 'SEMANAL';
    switch (freq) {
      case 'DIARIO':
      case 'DIARIA':
        return 'Diario (todos los días)';
      case 'QUINCENAL':
        return 'Quincenal (cada 14 días exactos)';
      case 'MENSUAL':
        return `Mensual (día ${ev.diaMes || 1} de cada mes)`;
      case 'ANUAL':
        return 'Anual (en la misma fecha)';
      case 'SEMANAL':
      default:
        if (ev.diasSemana) {
          const names = ev.diasSemana
            .split(',')
            .map((n) => DIAS_SEMANA_OPCIONES.find((d) => d.num === Number(n))?.label)
            .filter(Boolean)
            .join(', ');
          return `Semanal (${names || 'Lunes'})`;
        }
        return 'Semanal';
    }
  };

  const getHeaderTitle = () => {
    if (viewMode === 'month') {
      return format(currentDate, 'MMMM yyyy', { locale: es });
    }
    if (viewMode === 'week') {
      const start = format(weekDays[0], "d 'de' MMMM", { locale: es });
      const end = format(weekDays[6], "d 'de' MMMM yyyy", { locale: es });
      return `${start} - ${end}`;
    }
    if (viewMode === 'day') {
      return format(currentDate, "EEEE, d 'de' MMMM yyyy", { locale: es });
    }
    return 'Agenda de Intervenciones';
  };

  return (
    <div className="space-y-6">
      {/* Header & Controls Bar */}
      <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          {/* Navigation and Title */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-brand-50 border border-brand-100 flex items-center justify-center text-brand-600">
              <CalendarIcon className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-black text-slate-900 capitalize tracking-tight">
                {getHeaderTitle()}
              </h1>
              <p className="text-xs font-medium text-slate-500">
                Planificación de intervenciones técnicas, recurrencias periódicas y recordatorios
              </p>
            </div>
          </div>

          {/* Action and View Switcher */}
          <div className="flex flex-wrap items-center gap-3">
            {/* View switcher buttons */}
            <div className="inline-flex rounded-xl bg-slate-100 p-1 border border-slate-200">
              <button
                onClick={() => setViewMode('month')}
                className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-colors flex items-center gap-1.5 ${
                  viewMode === 'month'
                    ? 'bg-white text-brand-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <CalendarDays className="w-3.5 h-3.5" />
                <span>Mes</span>
              </button>
              <button
                onClick={() => setViewMode('week')}
                className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-colors flex items-center gap-1.5 ${
                  viewMode === 'week'
                    ? 'bg-white text-brand-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <CalendarRange className="w-3.5 h-3.5" />
                <span>Semana</span>
              </button>
              <button
                onClick={() => setViewMode('day')}
                className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-colors flex items-center gap-1.5 ${
                  viewMode === 'day'
                    ? 'bg-white text-brand-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Clock3 className="w-3.5 h-3.5" />
                <span>Día</span>
              </button>
              <button
                onClick={() => setViewMode('agenda')}
                className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-colors flex items-center gap-1.5 ${
                  viewMode === 'agenda'
                    ? 'bg-white text-brand-700 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <List className="w-3.5 h-3.5" />
                <span>Agenda</span>
              </button>
            </div>

            {/* Navigation arrows & Today */}
            <div className="flex items-center gap-1.5">
              <Button
                variant="outline"
                size="sm"
                onClick={handlePrevious}
                title="Anterior"
              >
                <ChevronLeft className="w-4 h-4" />
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={handleToday}
                className="font-bold text-xs"
              >
                Hoy
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={handleNext}
                title="Siguiente"
              >
                <ChevronRight className="w-4 h-4" />
              </Button>
            </div>

            {hasPermission('CRONOGRAMA_GESTIONAR') && (
              <Button
                variant="primary"
                size="md"
                onClick={() => openCreateModal()}
                leftIcon={<Plus className="w-4 h-4" />}
                className="shadow-sm font-bold"
              >
                Nuevo Evento
              </Button>
            )}
          </div>
        </div>

        {/* QUICK TYPE SELECTOR TABS */}
        <div className="flex flex-wrap items-center justify-between gap-3 pt-3 border-t border-slate-100">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs font-bold text-slate-500 mr-1">Filtrar Tipo:</span>
            <button
              onClick={() => setFiltroTipo('')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all ${
                filtroTipo === ''
                  ? 'bg-slate-900 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200/70 border border-slate-200'
              }`}
            >
              Todos los Eventos ({eventos.length})
            </button>
            <button
              onClick={() => setFiltroTipo('RECURRENTE')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-2 ${
                filtroTipo === 'RECURRENTE'
                  ? 'bg-purple-700 text-white shadow-xs'
                  : 'bg-purple-50 text-purple-900 hover:bg-purple-100 border border-purple-200'
              }`}
            >
              <span className="w-2 h-2 rounded-full bg-purple-500"></span>
              <span>Fijos / Recurrentes ({eventos.filter((e) => e.tipo === 'RECURRENTE').length})</span>
            </button>
            <button
              onClick={() => setFiltroTipo('PUNTUAL')}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-2 ${
                filtroTipo === 'PUNTUAL'
                  ? 'bg-emerald-700 text-white shadow-xs'
                  : 'bg-emerald-50 text-emerald-900 hover:bg-emerald-100 border border-emerald-200'
              }`}
            >
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span>Esporádicos / Recordatorios ({eventos.filter((e) => e.tipo !== 'RECURRENTE').length})</span>
            </button>
          </div>
        </div>

        {/* Secondary Filter Bar */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 pt-3 border-t border-slate-100">
          <div className="relative lg:col-span-2">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Buscar por título, nota o cliente..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="w-full pl-9 pr-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white placeholder:text-slate-400 transition-all"
            />
          </div>

          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all cursor-pointer"
          >
            {ESTADOS.map((st) => (
              <option key={st.value} value={st.value} className="bg-white text-slate-900">
                {st.label}
              </option>
            ))}
          </select>

          <select
            value={filtroPrioridad}
            onChange={(e) => setFiltroPrioridad(e.target.value)}
            className="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all cursor-pointer"
          >
            {PRIORIDADES.map((p) => (
              <option key={p.value} value={p.value} className="bg-white text-slate-900">
                {p.label}
              </option>
            ))}
          </select>

          <select
            value={filtroVisibilidad}
            onChange={(e) => setFiltroVisibilidad(e.target.value)}
            className="w-full px-3 py-2 text-xs font-semibold text-slate-800 bg-slate-50/80 hover:bg-slate-100/70 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 focus:bg-white transition-all cursor-pointer"
          >
            <option value="" className="bg-white text-slate-900">Todas las visibilidades</option>
            <option value="COMPARTIDO" className="bg-white text-slate-900">Compartido</option>
            <option value="PRIVADO" className="bg-white text-slate-900">Solo para mí (Privado)</option>
          </select>
        </div>
      </div>

      {loading ? (
        <LoadingSpinner message="Cargando calendario interactivo..." />
      ) : (
        <>
          {/* ======================================================== */}
          {/* VIEW: MONTH (VISTA MENSUAL GRANDE E INTERACTIVA)       */}
          {/* ======================================================== */}
          {viewMode === 'month' && (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden select-none">
              {/* Day names header */}
              <div className="grid grid-cols-7 border-b border-slate-200 bg-slate-50 text-center text-xs font-bold uppercase tracking-wider text-slate-500 py-3">
                <span>Lun</span>
                <span>Mar</span>
                <span>Mié</span>
                <span>Jue</span>
                <span>Vie</span>
                <span className="text-brand-600">Sáb</span>
                <span className="text-brand-600">Dom</span>
              </div>

              {/* Grid 7 Columns */}
              <div className="grid grid-cols-7 auto-rows-fr divide-x divide-y divide-slate-100 min-h-[640px]">
                {monthDays.map((day, i) => {
                  const dayEvents = getEventsForDay(day);
                  const isCurrentMonth = isSameMonth(day, monthStart);
                  const isDayToday = isToday(day);

                  return (
                    <div
                      key={i}
                      onClick={() => openCreateModal(day)}
                      className={`min-h-[120px] p-2 transition-colors flex flex-col justify-between group cursor-pointer ${
                        !isCurrentMonth
                          ? 'bg-slate-50/50 text-slate-300'
                          : 'bg-white hover:bg-slate-50/70 text-slate-700'
                      }`}
                    >
                      {/* Day Number Header */}
                      <div className="flex items-center justify-between">
                        <span
                          className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${
                            isDayToday
                              ? 'bg-brand-600 text-white shadow-sm'
                              : isCurrentMonth
                              ? 'text-slate-800 group-hover:bg-slate-200/60'
                              : 'text-slate-300'
                          }`}
                        >
                          {format(day, 'd')}
                        </span>
                        {dayEvents.length > 0 && (
                          <span className="text-[10px] font-bold text-slate-400">
                            {dayEvents.length} {dayEvents.length === 1 ? 'evento' : 'eventos'}
                          </span>
                        )}
                      </div>

                      {/* Event Chips List */}
                      <div className="space-y-1 mt-1.5 flex-1 overflow-y-auto max-h-24">
                        {dayEvents.slice(0, 3).map((ev, idx) => (
                          <div
                            key={ev.recurrenciaId || `${ev.id}-${idx}`}
                            onClick={(e) => {
                              e.stopPropagation();
                              openDetailModal(ev);
                            }}
                            className={`px-2 py-1 rounded-lg border text-[11px] font-bold truncate transition-all flex items-center justify-between gap-1 ${getEventChipStyles(
                              ev.tipo,
                              ev.estado
                            )}`}
                            title={`${ev.titulo} (${ev.tipo === 'RECURRENTE' ? 'Fijo / Recurrente' : 'Esporádico'}) - ${ev.clienteNombre || 'Sin cliente'}`}
                          >
                            <span className="truncate flex items-center gap-1">
                              {ev.tipo === 'RECURRENTE' ? (
                                <Repeat className="w-3 h-3 text-purple-700 shrink-0" />
                              ) : (
                                <span className="w-1.5 h-1.5 rounded-full bg-emerald-600 shrink-0"></span>
                              )}
                              {ev.visibilidad === 'PRIVADO' && (
                                <Lock className="w-3 h-3 text-amber-600 shrink-0" />
                              )}
                              <span>{ev.titulo}</span>
                            </span>
                            <span className="text-[9px] opacity-75 shrink-0 font-mono">
                              {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'HH:mm') : ''}
                            </span>
                          </div>
                        ))}
                        {dayEvents.length > 3 && (
                          <div className="text-[10px] font-bold text-brand-600 pl-1">
                            +{dayEvents.length - 3} más...
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW: WEEK (VISTA SEMANAL)                                */}
          {/* ======================================================== */}
          {viewMode === 'week' && (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-7 divide-x divide-slate-200 min-h-[500px]">
                {weekDays.map((day, i) => {
                  const dayEvents = getEventsForDay(day);
                  const isDayToday = isToday(day);

                  return (
                    <div key={i} className="flex flex-col">
                      {/* Day Column Header */}
                      <div
                        className={`p-3 text-center border-b border-slate-200 ${
                          isDayToday ? 'bg-brand-50/80 text-brand-800' : 'bg-slate-50 text-slate-700'
                        }`}
                      >
                        <div className="text-xs font-bold uppercase tracking-wider">
                          {format(day, 'EEE', { locale: es })}
                        </div>
                        <div
                          className={`text-lg font-black mt-0.5 inline-block w-8 h-8 rounded-full leading-8 ${
                            isDayToday ? 'bg-brand-600 text-white shadow-sm' : ''
                          }`}
                        >
                          {format(day, 'd')}
                        </div>
                      </div>

                      {/* Day events column */}
                      <div
                        onClick={() => openCreateModal(day)}
                        className="flex-1 p-2 space-y-2 hover:bg-slate-50/50 transition-colors cursor-pointer"
                      >
                        {dayEvents.map((ev, idx) => (
                          <div
                            key={ev.recurrenciaId || `${ev.id}-${idx}`}
                            onClick={(e) => {
                              e.stopPropagation();
                              openDetailModal(ev);
                            }}
                            className={`p-2.5 rounded-xl border text-xs font-semibold shadow-xs space-y-1.5 transition-all ${getEventChipStyles(
                              ev.tipo,
                              ev.estado
                            )}`}
                          >
                            <div className="flex items-center justify-between text-[10px] opacity-80 font-mono">
                              <span className="flex items-center gap-1 font-bold">
                                {ev.tipo === 'RECURRENTE' ? 'Fijo' : 'Esporádico'}
                              </span>
                              <span className="flex items-center gap-1">
                                {ev.visibilidad === 'PRIVADO' && (
                                  <Lock className="w-3 h-3 text-amber-600" />
                                )}
                                {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'HH:mm') : ''}
                              </span>
                            </div>
                            <h4 className="font-bold text-slate-900 line-clamp-2">{ev.titulo}</h4>
                            {ev.clienteNombre && (
                              <p className="text-[10px] text-slate-600 truncate flex items-center gap-1">
                                <Building className="w-3 h-3 text-slate-400" />
                                {ev.clienteNombre}
                              </p>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW: DAY (VISTA DIARIA)                                  */}
          {/* ======================================================== */}
          {viewMode === 'day' && (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-6">
              <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                <div>
                  <h2 className="text-xl font-bold text-slate-900 capitalize">
                    {format(currentDate, "EEEE, d 'de' MMMM yyyy", { locale: es })}
                  </h2>
                  <p className="text-xs text-slate-500">
                    {getEventsForDay(currentDate).length} intervenciones programadas
                  </p>
                </div>
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => openCreateModal(currentDate)}
                  leftIcon={<Plus className="w-4 h-4" />}
                >
                  Agregar evento para hoy
                </Button>
              </div>

              {getEventsForDay(currentDate).length === 0 ? (
                <div className="py-16 text-center space-y-3">
                  <CalendarIcon className="w-12 h-12 text-slate-300 mx-auto" />
                  <p className="text-sm font-medium text-slate-500">
                    No hay intervenciones programadas para este día.
                  </p>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => openCreateModal(currentDate)}
                  >
                    Crear primera tarea
                  </Button>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {getEventsForDay(currentDate).map((ev, idx) => (
                    <div
                      key={ev.recurrenciaId || `${ev.id}-${idx}`}
                      onClick={() => openDetailModal(ev)}
                      className={`p-5 rounded-2xl border transition-all cursor-pointer space-y-3 ${
                        ev.tipo === 'RECURRENTE'
                          ? 'border-purple-200 bg-purple-50/40 hover:border-purple-300 hover:shadow-md'
                          : 'border-emerald-200 bg-emerald-50/40 hover:border-emerald-300 hover:shadow-md'
                      }`}
                    >
                      <div className="flex items-center justify-between flex-wrap gap-2">
                        <div className="flex items-center gap-2">
                          <span
                            className={`text-xs font-bold px-2.5 py-1 rounded-lg border ${
                              ev.tipo === 'RECURRENTE'
                                ? 'bg-purple-100 text-purple-800 border-purple-200'
                                : 'bg-emerald-100 text-emerald-800 border-emerald-200'
                            }`}
                          >
                            {ev.tipo === 'RECURRENTE' ? 'Fijo / Recurrente' : 'Esporádico'}
                          </span>
                          <Badge variant={getStatusBadgeVariant(ev.estado)} size="md">
                            {ev.estado}
                          </Badge>
                          {ev.visibilidad === 'PRIVADO' && (
                            <span className="inline-flex items-center gap-1 text-[11px] font-bold px-2 py-0.5 rounded-lg bg-amber-50 text-amber-800 border border-amber-200">
                              <Lock className="w-3 h-3" /> Solo para mí
                            </span>
                          )}
                        </div>
                        <span className="text-xs font-mono font-bold text-slate-600 flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5 text-slate-400" />
                          {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'HH:mm') : ''}
                          {ev.fechaFin ? ` - ${format(parseISO(ev.fechaFin), 'HH:mm')}` : ''}
                        </span>
                      </div>

                      <h3 className="text-base font-bold text-slate-900">{ev.titulo}</h3>

                      {ev.descripcion && (
                        <p className="text-xs text-slate-600 line-clamp-2">{ev.descripcion}</p>
                      )}

                      <div className="pt-3 border-t border-slate-200/60 flex items-center justify-between text-xs text-slate-500">
                        {ev.clienteNombre ? (
                          <span className="font-semibold text-slate-800 flex items-center gap-1.5">
                            <Building className="w-4 h-4 text-slate-400" />
                            {ev.clienteNombre}
                          </span>
                        ) : (
                          <span className="italic text-slate-400">Sin cliente asignado</span>
                        )}
                        {ev.usuarioNombre && (
                          <span className="flex items-center gap-1">
                            <User className="w-3.5 h-3.5 text-slate-400" />
                            {ev.usuarioNombre}
                          </span>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* ======================================================== */}
          {/* VIEW: AGENDA / LISTA                                      */}
          {/* ======================================================== */}
          {viewMode === 'agenda' && (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              {filteredEventos.length === 0 ? (
                <div className="py-16 text-center space-y-3">
                  <AlertCircle className="w-12 h-12 text-slate-300 mx-auto" />
                  <p className="text-sm font-medium text-slate-500">
                    No se encontraron eventos con los filtros seleccionados.
                  </p>
                </div>
              ) : (
                <div className="divide-y divide-slate-100">
                  {filteredEventos.map((ev, idx) => (
                    <div
                      key={ev.recurrenciaId || `${ev.id}-${idx}`}
                      onClick={() => openDetailModal(ev)}
                      className="p-4 sm:p-5 hover:bg-slate-50/80 transition-colors cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                    >
                      <div className="flex items-start gap-4">
                        <div
                          className={`w-12 h-12 rounded-xl border flex flex-col items-center justify-center shrink-0 ${
                            ev.tipo === 'RECURRENTE'
                              ? 'bg-purple-50 border-purple-200 text-purple-900'
                              : 'bg-emerald-50 border-emerald-200 text-emerald-900'
                          }`}
                        >
                          <span className="text-[10px] uppercase font-bold opacity-75">
                            {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'MMM', { locale: es }) : ''}
                          </span>
                          <span className="text-base font-black leading-none">
                            {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'd') : ''}
                          </span>
                        </div>

                        <div className="space-y-1">
                          <div className="flex items-center gap-2 flex-wrap">
                            <h3 className="text-sm font-bold text-slate-900">{ev.titulo}</h3>
                            <span
                              className={`text-[11px] font-bold px-2 py-0.5 rounded-lg border ${
                                ev.tipo === 'RECURRENTE'
                                  ? 'bg-purple-100 text-purple-800 border-purple-200'
                                  : 'bg-emerald-100 text-emerald-800 border-emerald-200'
                              }`}
                            >
                              {ev.tipo === 'RECURRENTE' ? 'Fijo / Recurrente' : 'Esporádico'}
                            </span>
                            <Badge variant={getStatusBadgeVariant(ev.estado)} size="sm">
                              {ev.estado}
                            </Badge>
                            {ev.visibilidad === 'PRIVADO' ? (
                              <span className="inline-flex items-center gap-1 text-[11px] font-bold px-2 py-0.5 rounded-lg bg-amber-50 text-amber-800 border border-amber-200">
                                <Lock className="w-3 h-3" /> Solo para mí
                              </span>
                            ) : (
                              <span className="inline-flex items-center gap-1 text-[11px] font-bold px-2 py-0.5 rounded-lg bg-slate-100 text-slate-600">
                                <Globe className="w-3 h-3" /> Compartido
                              </span>
                            )}
                          </div>
                          {ev.descripcion && (
                            <p className="text-xs text-slate-500 line-clamp-1">{ev.descripcion}</p>
                          )}
                          <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500">
                            <span className="flex items-center gap-1 font-mono">
                              <Clock className="w-3.5 h-3.5 text-slate-400" />
                              {ev.fechaInicio ? format(parseISO(ev.fechaInicio), 'dd/MM/yyyy HH:mm') : ''}
                            </span>
                            {ev.clienteNombre && (
                              <span className="flex items-center gap-1 font-medium text-slate-700">
                                <Building className="w-3.5 h-3.5 text-slate-400" />
                                {ev.clienteNombre}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center gap-2 self-end sm:self-center">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleToggleEstado(ev);
                          }}
                          leftIcon={<CheckCircle2 className="w-4 h-4" />}
                        >
                          {ev.estado === 'COMPLETADO' ? 'Reabrir' : 'Completar'}
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </>
      )}

      {/* ======================================================== */}
      {/* MODAL: SELECCIÓN DE ÁMBITO (ESTA INSTANCIA VS TODA LA SERIE) */}
      {/* ======================================================== */}
      <Modal
        isOpen={isScopeChoiceModalOpen}
        onClose={() => setIsScopeChoiceModalOpen(false)}
        title={scopeActionType === 'EDIT' ? 'Editar Evento Recurrente' : 'Eliminar Evento Recurrente'}
        size="md"
      >
        <div className="space-y-4">
          <div className="p-4 rounded-xl bg-purple-50 border border-purple-200 text-xs text-purple-900 space-y-1">
            <p className="font-bold flex items-center gap-1.5">
              <Repeat className="w-4 h-4 text-purple-700" />
              <span>Este evento forma parte de una serie recurrente:</span>
            </p>
            <p className="font-semibold text-slate-900 text-sm">{selectedEvento?.titulo}</p>
            <p className="text-purple-800 text-[11px]">
              Ocurrencia: {selectedEvento?.fechaInicio ? format(parseISO(selectedEvento.fechaInicio), "d 'de' MMMM yyyy 'a las' HH:mm", { locale: es }) : ''}
            </p>
          </div>

          <p className="text-xs font-semibold text-slate-600">
            ¿Cómo deseas aplicar esta {scopeActionType === 'EDIT' ? 'modificación' : 'eliminación'}?
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div
              onClick={() => {
                if (scopeActionType === 'EDIT' && selectedEvento) {
                  openEditModal(selectedEvento, 'INSTANCIA');
                } else {
                  handleDeleteOccurrenceOnly();
                }
              }}
              className="p-4 rounded-xl border-2 border-slate-200 hover:border-brand-500 hover:bg-brand-50/50 cursor-pointer transition-all space-y-1.5 group"
            >
              <div className="flex items-center gap-2 font-bold text-slate-900 text-xs group-hover:text-brand-700">
                <Clock className="w-4 h-4 text-brand-600" />
                <span>Solo este evento</span>
              </div>
              <p className="text-[11px] text-slate-500 leading-tight">
                {scopeActionType === 'EDIT'
                  ? 'Aplica los cambios únicamente a la fecha seleccionada sin modificar las demás.'
                  : 'Elimina exclusivamente la ocurrencia de esta fecha manteniendo el resto de la serie.'}
              </p>
            </div>

            <div
              onClick={() => {
                if (scopeActionType === 'EDIT' && selectedEvento) {
                  openEditModal(selectedEvento, 'SERIE');
                } else {
                  handleDeleteWholeSeries();
                }
              }}
              className="p-4 rounded-xl border-2 border-slate-200 hover:border-purple-500 hover:bg-purple-50/50 cursor-pointer transition-all space-y-1.5 group"
            >
              <div className="flex items-center gap-2 font-bold text-slate-900 text-xs group-hover:text-purple-700">
                <Repeat className="w-4 h-4 text-purple-600" />
                <span>Toda la serie</span>
              </div>
              <p className="text-[11px] text-slate-500 leading-tight">
                {scopeActionType === 'EDIT'
                  ? 'Actualiza las reglas de recurrencia y todos los eventos periódicos asociados.'
                  : 'Elimina permanentemente la serie y todas sus apariciones futuras.'}
              </p>
            </div>
          </div>

          <div className="flex justify-end pt-3 border-t border-slate-100">
            <Button variant="outline" size="sm" onClick={() => setIsScopeChoiceModalOpen(false)}>
              Cancelar
            </Button>
          </div>
        </div>
      </Modal>

      {/* ======================================================== */}
      {/* MODAL: CREAR / EDITAR EVENTO                             */}
      {/* ======================================================== */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={
          selectedEvento
            ? editScope === 'INSTANCIA'
              ? 'Modificar solo esta ocurrencia'
              : 'Modificar toda la serie recurrente'
            : 'Programar Nueva Intervención'
        }
        size="lg"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Título de la intervención *"
            placeholder="Ej: Visita de mantenimiento semanal a servidores"
            value={formData.titulo}
            onChange={(e) => setFormData({ ...formData, titulo: e.target.value })}
            required
          />

          {/* TIPO DE EVENTO: PUNTUAL VS FIJO/RECURRENTE (Si estamos editando toda la serie o creando nuevo) */}
          {editScope === 'SERIE' && (
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2">
                Tipo de Planificación *
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div
                  onClick={() => setFormData({ ...formData, tipo: 'PUNTUAL' })}
                  className={`p-3.5 rounded-xl border-2 cursor-pointer transition-all flex items-start gap-3 ${
                    formData.tipo === 'PUNTUAL' || !formData.tipo
                      ? 'border-emerald-600 bg-emerald-50/60 shadow-xs'
                      : 'border-slate-200 bg-white hover:border-slate-300'
                  }`}
                >
                  <div className="w-8 h-8 rounded-lg bg-emerald-100 flex items-center justify-center text-emerald-700 shrink-0 mt-0.5">
                    <Clock className="w-4 h-4" />
                  </div>
                  <div>
                    <span className="text-xs font-bold text-slate-900 block">
                      Evento Puntual / Esporádico
                    </span>
                    <span className="text-[11px] text-slate-500 block leading-tight mt-0.5">
                      Incidencia puntual, tarea única o visita puntual.
                    </span>
                  </div>
                </div>

                <div
                  onClick={() => setFormData({ ...formData, tipo: 'RECURRENTE' })}
                  className={`p-3.5 rounded-xl border-2 cursor-pointer transition-all flex items-start gap-3 ${
                    formData.tipo === 'RECURRENTE'
                      ? 'border-purple-600 bg-purple-50/60 shadow-xs'
                      : 'border-slate-200 bg-white hover:border-slate-300'
                  }`}
                >
                  <div className="w-8 h-8 rounded-lg bg-purple-100 flex items-center justify-center text-purple-700 shrink-0 mt-0.5">
                    <Repeat className="w-4 h-4" />
                  </div>
                  <div>
                    <span className="text-xs font-bold text-slate-900 block">
                      Evento Fijo / Recurrente
                    </span>
                    <span className="text-[11px] text-slate-500 block leading-tight mt-0.5">
                      Actividad periódica de calendario (diaria, semanal, quincenal, mensual, anual).
                    </span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* CONFIGURACIÓN DE RECURRENCIA REAL */}
          {formData.tipo === 'RECURRENTE' && editScope === 'SERIE' && (
            <div className="p-4 rounded-xl bg-purple-50/50 border border-purple-200 space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-purple-900 flex items-center gap-1.5">
                  <Repeat className="w-4 h-4 text-purple-700" />
                  <span>Configuración de Periodicidad</span>
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-purple-900 mb-1">
                    Frecuencia de Repetición
                  </label>
                  <select
                    value={formData.recurrencia || 'SEMANAL'}
                    onChange={(e) => setFormData({ ...formData, recurrencia: e.target.value })}
                    className="w-full px-3 py-2 text-xs font-semibold rounded-xl border border-purple-300 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-500 bg-white text-purple-950"
                  >
                    <option value="DIARIO">Diario (todos los días)</option>
                    <option value="SEMANAL">Semanal (días específicos)</option>
                    <option value="QUINCENAL">Quincenal (cada 14 días exactos)</option>
                    <option value="MENSUAL">Mensual (día específico del mes)</option>
                    <option value="ANUAL">Anual (en la misma fecha cada año)</option>
                  </select>
                </div>

                {/* Día del mes si es mensual */}
                {formData.recurrencia === 'MENSUAL' && (
                  <div>
                    <label className="block text-xs font-bold text-purple-900 mb-1">
                      Día del Mes
                    </label>
                    <select
                      value={formData.diaMes || 1}
                      onChange={(e) => setFormData({ ...formData, diaMes: Number(e.target.value) })}
                      className="w-full px-3 py-2 text-xs font-semibold rounded-xl border border-purple-300 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-500 bg-white text-purple-950"
                    >
                      {Array.from({ length: 31 }, (_, idx) => idx + 1).map((d) => (
                        <option key={d} value={d}>
                          Día {d} de cada mes
                        </option>
                      ))}
                    </select>
                  </div>
                )}
              </div>

              {/* Selector de días de la semana si es semanal */}
              {(formData.recurrencia === 'SEMANAL' || !formData.recurrencia) && (
                <div className="space-y-1.5">
                  <label className="block text-xs font-bold text-purple-900">
                    Días de la semana en que se repite:
                  </label>
                  <div className="flex flex-wrap items-center gap-1.5">
                    {DIAS_SEMANA_OPCIONES.map((d) => {
                      const isSelected = selectedDiasSemana.includes(d.num);
                      return (
                        <button
                          key={d.num}
                          type="button"
                          onClick={() => handleToggleDiaSemana(d.num)}
                          className={`w-9 h-9 rounded-xl text-xs font-bold transition-all border ${
                            isSelected
                              ? 'bg-purple-700 text-white border-purple-700 shadow-xs'
                              : 'bg-white text-purple-900 border-purple-200 hover:bg-purple-100/60'
                          }`}
                          title={d.label}
                        >
                          {d.short}
                        </button>
                      );
                    })}
                  </div>
                  <p className="text-[11px] text-purple-700">
                    Se generará automáticamente para:{' '}
                    <strong>
                      {selectedDiasSemana
                        .map((n) => DIAS_SEMANA_OPCIONES.find((d) => d.num === n)?.label)
                        .join(', ')}
                    </strong>
                  </p>
                </div>
              )}

              {/* Finalización de la recurrencia */}
              <div className="pt-3 border-t border-purple-200/60 space-y-2">
                <label className="block text-xs font-bold text-purple-900">
                  Límite de la Recurrencia
                </label>
                <div className="flex items-center gap-4">
                  <label className="inline-flex items-center gap-2 text-xs font-semibold text-slate-700 cursor-pointer">
                    <input
                      type="radio"
                      name="recurrenciaFinRadio"
                      checked={!tieneFechaFinRecurrencia}
                      onChange={() => setTieneFechaFinRecurrencia(false)}
                      className="text-purple-600 focus:ring-purple-500"
                    />
                    <span>Sin fecha de finalización (indefinido)</span>
                  </label>

                  <label className="inline-flex items-center gap-2 text-xs font-semibold text-slate-700 cursor-pointer">
                    <input
                      type="radio"
                      name="recurrenciaFinRadio"
                      checked={tieneFechaFinRecurrencia}
                      onChange={() => setTieneFechaFinRecurrencia(true)}
                      className="text-purple-600 focus:ring-purple-500"
                    />
                    <span>Hasta una fecha límite</span>
                  </label>
                </div>

                {tieneFechaFinRecurrencia && (
                  <div className="pt-1">
                    <Input
                      label="Fecha de Finalización de la Serie"
                      type="datetime-local"
                      value={formData.fechaFinRecurrencia || ''}
                      onChange={(e) => setFormData({ ...formData, fechaFinRecurrencia: e.target.value })}
                      required={tieneFechaFinRecurrencia}
                    />
                  </div>
                )}
              </div>
            </div>
          )}

          {/* VISIBILIDAD / PRIVACIDAD */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2">
              Visibilidad y Privacidad *
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div
                onClick={() => setFormData({ ...formData, visibilidad: 'COMPARTIDO' })}
                className={`p-3.5 rounded-xl border-2 cursor-pointer transition-all flex items-start gap-3 ${
                  formData.visibilidad === 'COMPARTIDO'
                    ? 'border-brand-600 bg-brand-50/60 shadow-xs'
                    : 'border-slate-200 bg-white hover:border-slate-300'
                }`}
              >
                <div className="w-8 h-8 rounded-lg bg-brand-100 flex items-center justify-center text-brand-700 shrink-0 mt-0.5">
                  <Globe className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-xs font-bold text-slate-900 block">
                    Compartido
                  </span>
                  <span className="text-[11px] text-slate-500 block leading-tight mt-0.5">
                    Visible para el equipo técnico según sus permisos.
                  </span>
                </div>
              </div>

              <div
                onClick={() => setFormData({ ...formData, visibilidad: 'PRIVADO' })}
                className={`p-3.5 rounded-xl border-2 cursor-pointer transition-all flex items-start gap-3 ${
                  formData.visibilidad === 'PRIVADO'
                    ? 'border-amber-600 bg-amber-50/60 shadow-xs'
                    : 'border-slate-200 bg-white hover:border-slate-300'
                }`}
              >
                <div className="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center text-amber-700 shrink-0 mt-0.5">
                  <Lock className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-xs font-bold text-slate-900 block">
                    Solo para mí (Privado)
                  </span>
                  <span className="text-[11px] text-slate-500 block leading-tight mt-0.5">
                    Visible únicamente por ti. Ni siquiera el Super Admin puede verlo.
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                Cliente Asociado
              </label>
              <select
                value={formData.clienteId || ''}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    clienteId: e.target.value ? Number(e.target.value) : undefined,
                  })
                }
                className="w-full px-3 py-2 text-sm font-semibold rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 bg-white text-slate-900"
              >
                <option value="">-- Sin cliente asignado --</option>
                {clientes.map((c) => (
                  <option key={c.id} value={c.id} className="text-slate-900">
                    {c.nombre} ({c.codigo})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
                Nivel de Prioridad
              </label>
              <select
                value={formData.prioridad}
                onChange={(e) => setFormData({ ...formData, prioridad: e.target.value })}
                className="w-full px-3 py-2 text-sm font-semibold rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 bg-white text-slate-900"
              >
                <option value="BAJA" className="text-slate-900">Baja</option>
                <option value="MEDIA" className="text-slate-900">Media</option>
                <option value="ALTA" className="text-slate-900">Alta</option>
                <option value="URGENTE" className="text-slate-900">Urgente</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Fecha y Hora de Inicio *"
              type="datetime-local"
              value={formData.fechaInicio}
              onChange={(e) => setFormData({ ...formData, fechaInicio: e.target.value })}
              required
            />

            <Input
              label="Fecha y Hora Estimada de Fin"
              type="datetime-local"
              value={formData.fechaFin}
              onChange={(e) => setFormData({ ...formData, fechaFin: e.target.value })}
            />
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
              Estado de la Tarea
            </label>
            <select
              value={formData.estado}
              onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
              className="w-full px-3 py-2 text-sm font-semibold rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 bg-white text-slate-900"
            >
              <option value="PENDIENTE" className="text-slate-900">Pendiente</option>
              <option value="EN_PROCESO" className="text-slate-900">En Proceso</option>
              <option value="COMPLETADO" className="text-slate-900">Completado</option>
              <option value="CANCELADO" className="text-slate-900">Cancelado</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1">
              Descripción y Notas Técnicas
            </label>
            <textarea
              rows={3}
              placeholder="Detalles sobre las tareas a realizar, herramientas o requerimientos..."
              value={formData.descripcion}
              onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
              className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="primary"
              isLoading={modalLoading}
              className="font-bold shadow-sm"
            >
              {selectedEvento ? 'Guardar Cambios' : 'Crear Evento'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* ======================================================== */}
      {/* MODAL: DETALLE DEL EVENTO                                */}
      {/* ======================================================== */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        title="Detalles de la Intervención"
        size="md"
      >
        {selectedEvento && (
          <div className="space-y-5">
            <div className="flex items-center justify-between gap-3 flex-wrap">
              <div className="flex items-center gap-2 flex-wrap">
                <span
                  className={`text-xs font-bold px-2.5 py-1 rounded-lg border ${
                    selectedEvento.tipo === 'RECURRENTE'
                      ? 'bg-purple-100 text-purple-800 border-purple-200'
                      : 'bg-emerald-100 text-emerald-800 border-emerald-200'
                  }`}
                >
                  {selectedEvento.tipo === 'RECURRENTE' ? 'Fijo / Recurrente' : 'Esporádico'}
                </span>
                <Badge variant={getPriorityBadgeVariant(selectedEvento.prioridad)} size="md">
                  {selectedEvento.prioridad}
                </Badge>
                <Badge variant={getStatusBadgeVariant(selectedEvento.estado)} size="md">
                  {selectedEvento.estado}
                </Badge>
                {selectedEvento.visibilidad === 'PRIVADO' ? (
                  <span className="inline-flex items-center gap-1 text-xs font-bold px-2.5 py-1 rounded-lg bg-amber-50 text-amber-800 border border-amber-200">
                    <Lock className="w-3.5 h-3.5" /> Solo para mí (Privado)
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1 text-xs font-bold px-2.5 py-1 rounded-lg bg-slate-100 text-slate-700">
                    <Globe className="w-3.5 h-3.5" /> Compartido
                  </span>
                )}
              </div>
              <span className="text-xs text-slate-400 font-mono">
                ID #{selectedEvento.id}
              </span>
            </div>

            <div>
              <h2 className="text-lg font-bold text-slate-900">{selectedEvento.titulo}</h2>
              {selectedEvento.descripcion && (
                <p className="text-sm text-slate-600 mt-2 bg-slate-50 p-3 rounded-xl border border-slate-100 whitespace-pre-wrap">
                  {selectedEvento.descripcion}
                </p>
              )}
            </div>

            <div className="space-y-2.5 text-xs text-slate-600 bg-slate-50 p-4 rounded-xl border border-slate-100">
              {selectedEvento.tipo === 'RECURRENTE' && (
                <div className="flex items-center justify-between text-purple-900 font-semibold pb-2 border-b border-purple-100">
                  <span className="flex items-center gap-1.5">
                    <Repeat className="w-3.5 h-3.5 text-purple-700" /> Periodicidad:
                  </span>
                  <span className="font-bold text-purple-950">
                    {getRecurrenciaText(selectedEvento)}
                  </span>
                </div>
              )}

              <div className="flex items-center justify-between">
                <span className="text-slate-500 font-medium flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-slate-400" /> Fecha y Hora:
                </span>
                <span className="font-bold text-slate-800 font-mono">
                  {selectedEvento.fechaInicio ? format(parseISO(selectedEvento.fechaInicio), "d 'de' MMMM yyyy, HH:mm", { locale: es }) : '-'}
                  {selectedEvento.fechaFin ? ` - ${format(parseISO(selectedEvento.fechaFin), 'HH:mm')}` : ''}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-slate-500 font-medium flex items-center gap-1.5">
                  <Building className="w-3.5 h-3.5 text-slate-400" /> Cliente:
                </span>
                <span className="font-bold text-slate-800">
                  {selectedEvento.clienteNombre || 'Sin cliente asignado'}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-slate-500 font-medium flex items-center gap-1.5">
                  <User className="w-3.5 h-3.5 text-slate-400" /> Asignado por:
                </span>
                <span className="font-bold text-slate-800">
                  {selectedEvento.usuarioNombre || 'Sistema'}
                </span>
              </div>

              {selectedEvento.visibilidad === 'PRIVADO' && (
                <div className="pt-2 border-t border-slate-200 text-amber-800 text-[11px] flex items-center gap-1.5">
                  <Lock className="w-3.5 h-3.5 shrink-0" />
                  <span>Este evento es privado y solo es visible para ti.</span>
                </div>
              )}
            </div>

            <div className="flex flex-wrap items-center justify-between gap-3 pt-4 border-t border-slate-100">
              <div className="flex items-center gap-2">
                {hasPermission('CRONOGRAMA_GESTIONAR') && (
                  <>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleRequestEdit(selectedEvento)}
                      leftIcon={<Edit2 className="w-3.5 h-3.5" />}
                    >
                      Editar
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => handleRequestDelete(selectedEvento)}
                      leftIcon={<Trash2 className="w-3.5 h-3.5" />}
                    >
                      Eliminar
                    </Button>
                  </>
                )}
              </div>

              <Button
                variant={selectedEvento.estado === 'COMPLETADO' ? 'outline' : 'primary'}
                size="sm"
                onClick={() => handleToggleEstado(selectedEvento)}
                leftIcon={<CheckCircle2 className="w-4 h-4" />}
              >
                {selectedEvento.estado === 'COMPLETADO' ? 'Reabrir Tarea' : 'Marcar Completada'}
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
