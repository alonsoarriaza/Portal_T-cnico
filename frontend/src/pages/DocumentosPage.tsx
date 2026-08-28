import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { FileText, Search, Download, History, Building, FileCheck } from 'lucide-react';
import { documentosApi } from '../api/services';
import { Documento } from '../types';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Modal } from '../components/common/Modal';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export const DocumentosPage: React.FC = () => {
  const [documentos, setDocumentos] = useState<Documento[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const [search, setSearch] = useState('');
  const [categoria, setCategoria] = useState('');
  const [selectedDocForHistory, setSelectedDocForHistory] = useState<Documento | null>(null);

  const { success, error } = useToast();
  const navigate = useNavigate();

  const loadDocumentos = useCallback(async () => {
    try {
      setLoading(true);
      const data = await documentosApi.list({
        search: search || undefined,
        categoria: categoria || undefined,
        page,
        size,
      });
      setDocumentos(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar los documentos');
    } finally {
      setLoading(false);
    }
  }, [search, categoria, page, size, error]);

  useEffect(() => {
    loadDocumentos();
  }, [loadDocumentos]);

  const handleDownload = async (e: React.MouseEvent, docId: number, version?: number, filename?: string) => {
    e.stopPropagation();
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
      error('Error descargando documento');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
          <FileText className="w-5 h-5 text-emerald-600" />
          <span>Gestor Documental</span>
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">
          Archivos técnicos, contratos y certificados con versionado inmutable.
        </p>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm grid grid-cols-1 sm:grid-cols-2 gap-3">
        <Input
          placeholder="Buscar por archivo, descripción o cliente..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          leftIcon={<Search className="w-4 h-4" />}
        />
        <Select value={categoria} onChange={(e) => { setCategoria(e.target.value); setPage(0); }}>
          <option value="">Todas las Categorías</option>
          <option value="CONTRATOS">Contratos y Acuerdos</option>
          <option value="FACTURAS">Facturas y Presupuestos</option>
          <option value="INFORMES_TECNICOS">Informes Técnicos</option>
          <option value="CERTIFICADOS">Certificados y Garantías</option>
          <option value="GENERAL">General</option>
        </Select>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando documentos..." />
        ) : documentos.length === 0 ? (
          <EmptyState title="No hay documentos" description="No se han encontrado archivos registrados." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Documento</th>
                  <th className="px-4 py-3">Cliente</th>
                  <th className="px-4 py-3">Categoría</th>
                  <th className="px-4 py-3 text-center">Versión Actual</th>
                  <th className="px-4 py-3">Última Modificación</th>
                  <th className="px-4 py-3 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {documentos.map((doc) => (
                  <tr
                    key={doc.id}
                    onClick={() => navigate(`/clientes/${doc.clienteId}`)}
                    className="hover:bg-slate-50 transition-colors cursor-pointer"
                  >
                    <td className="px-4 py-3 font-bold text-slate-900 flex items-center gap-2">
                      <FileCheck className="w-4 h-4 text-emerald-600 shrink-0" />
                      <div>
                        <div>{doc.nombreOriginal}</div>
                        {doc.descripcion && <div className="text-xs text-slate-400 font-normal">{doc.descripcion}</div>}
                      </div>
                    </td>
                    <td className="px-4 py-3 font-medium text-slate-700">
                      <div className="flex items-center gap-1.5">
                        <Building className="w-3.5 h-3.5 text-slate-400" />
                        <span>{doc.clienteNombre}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <Badge variant="cyan" size="sm">{doc.categoria}</Badge>
                    </td>
                    <td className="px-4 py-3 text-center">
                      <Badge variant="purple" size="sm">v{doc.versionActual}</Badge>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-500">
                      {doc.fechaModificacion ? format(new Date(doc.fechaModificacion), 'dd/MM/yyyy HH:mm') : '-'}
                    </td>
                    <td className="px-4 py-3 text-right" onClick={(e) => e.stopPropagation()}>
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => setSelectedDocForHistory(doc)}
                          className="p-1 rounded text-slate-400 hover:text-slate-700 hover:bg-slate-100"
                          title="Historial de versiones"
                        >
                          <History className="w-4 h-4" />
                        </button>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={(e) => handleDownload(e, doc.id, doc.versionActual, doc.nombreOriginal)}
                          leftIcon={<Download className="w-3.5 h-3.5 text-slate-600" />}
                        >
                          Descargar
                        </Button>
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
            onSizeChange={setSize}
          />
        </div>
      </div>

      {/* Modal: Historial de Versiones */}
      <Modal
        isOpen={!!selectedDocForHistory}
        onClose={() => setSelectedDocForHistory(null)}
        title={`Historial de Versiones — ${selectedDocForHistory?.nombreOriginal}`}
        subtitle="Registro de versiones previas"
        maxWidth="lg"
      >
        <div className="space-y-2.5">
          {selectedDocForHistory?.versiones?.map((ver) => (
            <div key={ver.id} className="p-3 rounded-lg bg-slate-50 border border-slate-200 flex items-center justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <Badge variant="purple" size="sm">v{ver.version}</Badge>
                  <span className="text-xs font-semibold text-slate-800">{ver.nombreArchivo}</span>
                </div>
                <p className="text-[11px] text-slate-400 mt-0.5">
                  Subido por {ver.usuarioNombre} el {format(new Date(ver.fechaSubida), "d/MM/yyyy HH:mm", { locale: es })}
                </p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={(e) => handleDownload(e, selectedDocForHistory.id, ver.version, ver.nombreArchivo)}
                leftIcon={<Download className="w-3.5 h-3.5" />}
              >
                Descargar
              </Button>
            </div>
          ))}
        </div>
      </Modal>
    </div>
  );
};
