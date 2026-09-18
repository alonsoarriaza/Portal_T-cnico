import React, { useState } from 'react';
import {
  Upload,
  FileText,
  CheckCircle2,
  AlertTriangle,
  RefreshCw,
  X,
  Users,
  HardDrive,
  ShieldCheck,
  ArrowRight,
  Info,
  AlertCircle
} from 'lucide-react';
import { Modal } from '../common/Modal';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { LoadingSpinner } from '../common/LoadingSpinner';
import { clientesApi } from '../../api/services';
import { ImportPlan, ImportSummary } from '../../types';
import { useToast } from '../../contexts/ToastContext';

interface ImportClientsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const ImportClientsModal: React.FC<ImportClientsModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [file, setFile] = useState<File | null>(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [importing, setImporting] = useState(false);
  const [plan, setPlan] = useState<ImportPlan | null>(null);
  const [summary, setSummary] = useState<ImportSummary | null>(null);
  const [dragOver, setDragOver] = useState(false);

  const { success, error } = useToast();

  const handleReset = () => {
    setFile(null);
    setAnalyzing(false);
    setImporting(false);
    setPlan(null);
    setSummary(null);
    setDragOver(false);
  };

  const handleClose = () => {
    handleReset();
    onClose();
  };

  const handleFileChange = async (selectedFile: File) => {
    if (!selectedFile) return;

    const lower = selectedFile.name.toLowerCase();
    if (!lower.endsWith('.txt') && !lower.endsWith('.html') && !lower.endsWith('.htm') && !lower.endsWith('.csv')) {
      error('Formato no soportado. Se admiten archivos .txt, .html, .htm o .csv');
      return;
    }

    setFile(selectedFile);
    setPlan(null);
    setSummary(null);
    setAnalyzing(true);

    try {
      const resultPlan = await clientesApi.analizarImportacion(selectedFile);
      setPlan(resultPlan);
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al analizar el documento de importación');
      setFile(null);
    } finally {
      setAnalyzing(false);
    }
  };

  const handleExecuteImport = async () => {
    if (!file) return;

    setImporting(true);
    try {
      const resultSummary = await clientesApi.ejecutarImportacion(file);
      setSummary(resultSummary);
      success('Importación completada exitosamente sin duplicados');
      onSuccess();
    } catch (err: any) {
      error(err.response?.data?.message || 'Error al ejecutar la importación');
    } finally {
      setImporting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title="Sincronización e Importación Inteligente de Clientes y Equipos"
      subtitle="Extracción semántica, detección de duplicados y protección de datos existentes"
      size="xl"
    >
      <div className="space-y-6">
        {/* Step 1: Upload Dropzone (if not analyzed yet) */}
        {!plan && !summary && (
          <div className="space-y-4">
            <div
              onDragOver={(e) => {
                e.preventDefault();
                setDragOver(true);
              }}
              onDragLeave={() => setDragOver(false)}
              onDrop={(e) => {
                e.preventDefault();
                setDragOver(false);
                if (e.dataTransfer.files && e.dataTransfer.files[0]) {
                  handleFileChange(e.dataTransfer.files[0]);
                }
              }}
              className={`border-2 border-dashed rounded-2xl p-8 text-center transition-all ${
                dragOver
                  ? 'border-brand-500 bg-brand-50/50 scale-[1.01]'
                  : 'border-slate-300 bg-slate-50/50 hover:bg-slate-50 hover:border-slate-400'
              }`}
            >
              <div className="w-14 h-14 mx-auto rounded-2xl bg-brand-50 border border-brand-100 flex items-center justify-center text-brand-600 shadow-2xs mb-4">
                <Upload className="w-7 h-7" />
              </div>
              <h4 className="text-base font-bold text-slate-900 mb-1">
                Arrastra tu documento o selecciónalo
              </h4>
              <p className="text-xs text-slate-500 mb-4 max-w-md mx-auto">
                Formatos compatibles: <strong className="text-slate-700">TXT (.txt), HTML (.html, .htm)</strong>. El sistema procesará clientes y sus equipos inventariados.
              </p>

              <label className="inline-flex">
                <input
                  type="file"
                  accept=".txt,.html,.htm,.csv"
                  className="hidden"
                  onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                      handleFileChange(e.target.files[0]);
                    }
                  }}
                />
                <Button variant="primary" size="md" leftIcon={<FileText className="w-4 h-4" />}>
                  Examinar Archivo
                </Button>
              </label>
            </div>

            {analyzing && (
              <div className="py-6">
                <LoadingSpinner message="Analizando documento, extrayendo entidades y calculando coincidencias con la base de datos..." />
              </div>
            )}
          </div>
        )}

        {/* Step 2: Analysis / Import Plan Preview */}
        {plan && !summary && (
          <div className="space-y-5">
            {/* Header info bar */}
            <div className="flex items-center justify-between p-3.5 bg-slate-100 rounded-xl text-xs font-semibold text-slate-700">
              <div className="flex items-center gap-2">
                <FileText className="w-4 h-4 text-brand-600" />
                <span>Archivo analizado: <strong>{file?.name}</strong></span>
              </div>
              <button
                onClick={handleReset}
                className="text-brand-600 hover:text-brand-800 underline font-bold cursor-pointer"
              >
                Cambiar archivo
              </button>
            </div>

            {/* Metrics cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {/* Clients Summary */}
              <div className="p-4 rounded-2xl border border-slate-200 bg-white shadow-2xs space-y-2.5">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 text-sm font-bold text-slate-900">
                    <Users className="w-4 h-4 text-sky-600" />
                    <span>Clientes ({plan.clientsFound})</span>
                  </div>
                  <Badge variant="blue" size="sm">Total: {plan.clientsFound}</Badge>
                </div>
                <div className="space-y-1.5 text-xs text-slate-600">
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Nuevos (a registrar):</span>
                    <strong className="text-emerald-600 font-mono">+{plan.clientsToCreateCount}</strong>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Existentes:</span>
                    <strong className="text-slate-800 font-mono">{plan.clientsToUpdateCount + plan.clientsToSkipCount}</strong>
                  </div>
                  <div className="flex justify-between py-1">
                    <span className="text-slate-400">Duplicados a crear:</span>
                    <strong className="text-emerald-700 font-mono font-bold">0 (Protegido)</strong>
                  </div>
                </div>
              </div>

              {/* Contacts Summary */}
              <div className="p-4 rounded-2xl border border-slate-200 bg-white shadow-2xs space-y-2.5">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 text-sm font-bold text-slate-900">
                    <Users className="w-4 h-4 text-emerald-600" />
                    <span>Contactos ({plan.contactsFound || 0})</span>
                  </div>
                  <Badge variant="emerald" size="sm">Total: {plan.contactsFound || 0}</Badge>
                </div>
                <div className="space-y-1.5 text-xs text-slate-600">
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Nuevos (a asociar):</span>
                    <strong className="text-emerald-600 font-mono">+{plan.contactsToCreateCount || 0}</strong>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Existentes (reutilizados):</span>
                    <strong className="text-slate-800 font-mono">{plan.contactsToSkipCount || 0}</strong>
                  </div>
                  <div className="flex justify-between py-1">
                    <span className="text-slate-400">Duplicados a crear:</span>
                    <strong className="text-emerald-700 font-mono font-bold">0 (Protegido)</strong>
                  </div>
                </div>
              </div>

              {/* Equipments Summary */}
              <div className="p-4 rounded-2xl border border-slate-200 bg-white shadow-2xs space-y-2.5">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2 text-sm font-bold text-slate-900">
                    <HardDrive className="w-4 h-4 text-purple-600" />
                    <span>Equipos ({plan.equipmentsFound})</span>
                  </div>
                  <Badge variant="purple" size="sm">Total: {plan.equipmentsFound}</Badge>
                </div>
                <div className="space-y-1.5 text-xs text-slate-600">
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Nuevos (a asociar):</span>
                    <strong className="text-emerald-600 font-mono">+{plan.equipmentsToCreateCount}</strong>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span>Existentes (reutilizados):</span>
                    <strong className="text-slate-800 font-mono">{plan.equipmentsToSkipCount}</strong>
                  </div>
                  <div className="flex justify-between py-1">
                    <span className="text-slate-400">Duplicados a crear:</span>
                    <strong className="text-emerald-700 font-mono font-bold">0 (Protegido)</strong>
                  </div>
                </div>
              </div>
            </div>

            {/* Protection Guarantee Notice */}
            <div className="p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 flex items-start gap-2.5 text-emerald-900 text-xs">
              <ShieldCheck className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
              <div>
                <p className="font-bold">Protección de Datos Existentes Activa</p>
                <p className="text-emerald-700 mt-0.5">
                  Los registros de la base de datos no serán sobrescritos si existe discrepancia. Únicamente se completarán campos vacíos y se crearán los equipos y clientes no existentes.
                </p>
              </div>
            </div>

            {/* Conflicts Warning (if any) */}
            {(plan.conflicts.length > 0 || plan.associationConflicts.length > 0) && (
              <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-xs space-y-2">
                <div className="flex items-center gap-2 font-bold text-amber-800">
                  <AlertTriangle className="w-4 h-4 text-amber-600" />
                  <span>Discrepancias detectadas ({plan.conflicts.length + plan.associationConflicts.length})</span>
                </div>
                <p className="text-amber-700">
                  Se encontraron datos en el documento diferentes a los guardados en el portal. Conforme a las reglas de seguridad, <strong>se preservan los datos actuales de la base de datos</strong>.
                </p>
                <div className="max-h-36 overflow-y-auto space-y-1.5 pr-1">
                  {plan.conflicts.map((c, idx) => (
                    <div key={idx} className="p-2 bg-white/80 rounded-lg border border-amber-200/60 flex items-center justify-between text-[11px]">
                      <span><strong>{c.entityIdentifier}</strong>: campo <em>{c.fieldName}</em></span>
                      <span className="font-mono text-slate-500">BBDD: "{c.existingValue}" vs Doc: "{c.importedValue}"</span>
                    </div>
                  ))}
                  {plan.associationConflicts.map((ac, idx) => (
                    <div key={'ac-' + idx} className="p-2 bg-white/80 rounded-lg border border-amber-200/60 flex items-center justify-between text-[11px]">
                      <span><strong>{ac.equipmentReference}</strong></span>
                      <span className="text-rose-700 font-semibold">{ac.message}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
              <Button variant="outline" onClick={handleClose} disabled={importing}>
                Cancelar
              </Button>
              <Button
                variant="primary"
                onClick={handleExecuteImport}
                isLoading={importing}
                leftIcon={<CheckCircle2 className="w-4 h-4" />}
              >
                {importing ? 'Sincronizando...' : 'Confirmar e Importar'}
              </Button>
            </div>
          </div>
        )}

        {/* Step 3: Success Summary Result */}
        {summary && (
          <div className="space-y-5">
            <div className="p-5 rounded-2xl bg-emerald-50/80 border border-emerald-200 text-center space-y-2">
              <div className="w-12 h-12 mx-auto rounded-xl bg-emerald-100 flex items-center justify-center text-emerald-700 shadow-2xs">
                <CheckCircle2 className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-emerald-950">Importación Completada con Éxito</h3>
              <p className="text-xs text-emerald-800 max-w-md mx-auto">
                La base de datos se ha sincronizado correctamente. La integridad de clientes y equipos está garantizada.
              </p>
            </div>

            {/* Statistics grid */}
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 text-center">
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Clientes Nuevos</span>
                <span className="text-xl font-mono font-bold text-emerald-600">+{summary.clientsCreated}</span>
              </div>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Clientes Reutiliz.</span>
                <span className="text-xl font-mono font-bold text-slate-800">{summary.clientsMatched}</span>
              </div>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Contactos Nuevos</span>
                <span className="text-xl font-mono font-bold text-emerald-600">+{summary.contactsCreated || 0}</span>
              </div>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Contactos Reutiliz.</span>
                <span className="text-xl font-mono font-bold text-slate-800">{summary.contactsMatched || 0}</span>
              </div>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Equipos Nuevos</span>
                <span className="text-xl font-mono font-bold text-emerald-600">+{summary.equipmentsCreated}</span>
              </div>
              <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-[11px] font-semibold text-slate-500 uppercase tracking-wider block">Equipos Reutiliz.</span>
                <span className="text-xl font-mono font-bold text-slate-800">{summary.equipmentsMatched}</span>
              </div>
            </div>

            {/* Duplicates indicator */}
            <div className="flex items-center justify-between p-3.5 bg-slate-50 rounded-xl border border-slate-200 text-xs">
              <span className="font-semibold text-slate-700 flex items-center gap-1.5">
                <ShieldCheck className="w-4 h-4 text-emerald-600" />
                Duplicados generados:
              </span>
              <strong className="text-emerald-700 font-mono font-bold">0 duplicados</strong>
            </div>

            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
              <Button variant="primary" onClick={handleClose}>
                Aceptar y Finalizar
              </Button>
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};
