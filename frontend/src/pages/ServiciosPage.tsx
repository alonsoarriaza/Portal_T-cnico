import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Briefcase, Search, Building } from 'lucide-react';
import { serviciosApi } from '../api/services';
import { Servicio } from '../types';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';

export const ServiciosPage: React.FC = () => {
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const [search, setSearch] = useState('');
  const [estado, setEstado] = useState('');

  const { error } = useToast();
  const navigate = useNavigate();

  const loadServicios = useCallback(async () => {
    try {
      setLoading(true);
      const data = await serviciosApi.list({
        search: search || undefined,
        estado: estado || undefined,
        page,
        size,
      });
      setServicios(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar servicios');
    } finally {
      setLoading(false);
    }
  }, [search, estado, page, size, error]);

  useEffect(() => {
    loadServicios();
  }, [loadServicios]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
          <Briefcase className="w-5 h-5 text-indigo-600" />
          <span>Servicios y Contratos</span>
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">
          Supervisión de servicios activos, licencias y mantenimientos en clientes.
        </p>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm grid grid-cols-1 sm:grid-cols-2 gap-3">
        <Input
          placeholder="Buscar por servicio, descripción o cliente..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          leftIcon={<Search className="w-4 h-4" />}
        />
        <Select value={estado} onChange={(e) => { setEstado(e.target.value); setPage(0); }}>
          <option value="">Todos los Estados</option>
          <option value="ACTIVO">Activo</option>
          <option value="PAUSADO">Pausado</option>
          <option value="FINALIZADO">Finalizado</option>
        </Select>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando servicios..." />
        ) : servicios.length === 0 ? (
          <EmptyState title="No hay servicios" description="No se han encontrado servicios registrados." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Servicio</th>
                  <th className="px-4 py-3">Cliente</th>
                  <th className="px-4 py-3">Descripción</th>
                  <th className="px-4 py-3">Vigencia</th>
                  <th className="px-4 py-3">Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {servicios.map((srv) => (
                  <tr
                    key={srv.id}
                    onClick={() => navigate(`/clientes/${srv.clienteId}`)}
                    className="hover:bg-slate-50 transition-colors cursor-pointer"
                  >
                    <td className="px-4 py-3 font-bold text-slate-900">{srv.nombre}</td>
                    <td className="px-4 py-3 font-medium text-slate-700 flex items-center gap-1.5">
                      <Building className="w-3.5 h-3.5 text-slate-400" />
                      <span>{srv.clienteNombre}</span>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-500 max-w-xs truncate">{srv.descripcion || '-'}</td>
                    <td className="px-4 py-3 text-xs text-slate-500">{srv.fechaInicio || 'n/d'} - {srv.fechaFin || 'Indefinido'}</td>
                    <td className="px-4 py-3">
                      <Badge variant={srv.estado === 'ACTIVO' ? 'emerald' : 'slate'} size="sm">{srv.estado}</Badge>
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
    </div>
  );
};
