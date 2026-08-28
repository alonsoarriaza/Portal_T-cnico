import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Globe, Search, ExternalLink, Building } from 'lucide-react';
import { websApi } from '../api/services';
import { WebItem } from '../types';
import { useToast } from '../contexts/ToastContext';
import { Badge } from '../components/common/Badge';
import { Input } from '../components/common/Input';
import { Select } from '../components/common/Select';
import { Pagination } from '../components/common/Pagination';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { EmptyState } from '../components/common/EmptyState';

export const WebsPage: React.FC = () => {
  const [webs, setWebs] = useState<WebItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const [search, setSearch] = useState('');
  const [estado, setEstado] = useState('');

  const { error } = useToast();
  const navigate = useNavigate();

  const loadWebs = useCallback(async () => {
    try {
      setLoading(true);
      const data = await websApi.list({
        search: search || undefined,
        estado: estado || undefined,
        page,
        size,
      });
      setWebs(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
    } catch (err) {
      error('Error al cargar sitios web');
    } finally {
      setLoading(false);
    }
  }, [search, estado, page, size, error]);

  useEffect(() => {
    loadWebs();
  }, [loadWebs]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-bold text-slate-900 flex items-center gap-2">
          <Globe className="w-5 h-5 text-sky-600" />
          <span>Sitios Web y Dominios</span>
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">
          Control de dominios y páginas web administradas para clientes.
        </p>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm grid grid-cols-1 sm:grid-cols-2 gap-3">
        <Input
          placeholder="Buscar por nombre, URL o cliente..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          leftIcon={<Search className="w-4 h-4" />}
        />
        <Select value={estado} onChange={(e) => { setEstado(e.target.value); setPage(0); }}>
          <option value="">Todos los Estados</option>
          <option value="ONLINE">Online</option>
          <option value="OFFLINE">Offline</option>
          <option value="MANTENIMIENTO">Mantenimiento</option>
        </Select>
      </div>

      <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
        {loading ? (
          <LoadingSpinner message="Cargando sitios web..." />
        ) : webs.length === 0 ? (
          <EmptyState title="No hay páginas web" description="No se han encontrado registros." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-600">
                <tr>
                  <th className="px-4 py-3">Nombre</th>
                  <th className="px-4 py-3">URL / Enlace</th>
                  <th className="px-4 py-3">Cliente</th>
                  <th className="px-4 py-3">Fecha Registro</th>
                  <th className="px-4 py-3">Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {webs.map((w) => (
                  <tr
                    key={w.id}
                    onClick={() => navigate(`/clientes/${w.clienteId}`)}
                    className="hover:bg-slate-50 transition-colors cursor-pointer"
                  >
                    <td className="px-4 py-3 font-bold text-slate-900">{w.nombre}</td>
                    <td className="px-4 py-3">
                      <a
                        href={w.url}
                        target="_blank"
                        rel="noreferrer"
                        onClick={(e) => e.stopPropagation()}
                        className="text-xs text-brand-600 hover:underline inline-flex items-center gap-1 font-mono"
                      >
                        {w.url}
                        <ExternalLink className="w-3 h-3" />
                      </a>
                    </td>
                    <td className="px-4 py-3 font-medium text-slate-700 flex items-center gap-1.5">
                      <Building className="w-3.5 h-3.5 text-slate-400" />
                      <span>{w.clienteNombre}</span>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-500">{w.fechaRegistro || '-'}</td>
                    <td className="px-4 py-3">
                      <Badge variant={w.estado === 'ONLINE' ? 'emerald' : 'amber'} size="sm">{w.estado}</Badge>
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
