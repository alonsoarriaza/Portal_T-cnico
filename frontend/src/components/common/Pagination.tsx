import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  size: number | 'ALL';
  onPageChange: (newPage: number) => void;
  onSizeChange?: (newSize: any) => void;
  sizeOptions?: (number | 'ALL')[];
}

export const Pagination: React.FC<PaginationProps> = ({
  page,
  totalPages,
  totalElements,
  size,
  onPageChange,
  onSizeChange,
  sizeOptions = [10, 25, 50, 100, 'ALL'],
}) => {
  const isAll = size === 'ALL' || (typeof size === 'number' && size <= 0);
  const numSize = typeof size === 'number' && size > 0 ? size : totalElements;
  const startItem = totalElements === 0 ? 0 : isAll ? 1 : page * numSize + 1;
  const endItem = isAll ? totalElements : Math.min((page + 1) * numSize, totalElements);
  const effectiveTotalPages = isAll ? (totalElements === 0 ? 0 : 1) : totalPages;
  const effectivePage = isAll ? 0 : page;

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-3 text-xs text-slate-500">
      <div className="flex items-center gap-2">
        <span>
          Mostrando <strong className="text-slate-800">{startItem}</strong> a{' '}
          <strong className="text-slate-800">{endItem}</strong> de{' '}
          <strong className="text-slate-800">{totalElements}</strong> registros
        </span>
        {onSizeChange && (
          <div className="flex items-center gap-1.5 ml-4">
            <span>Por pág:</span>
            <select
              value={size}
              onChange={(e) => {
                const val = e.target.value;
                onSizeChange(val === 'ALL' ? 'ALL' : Number(val));
              }}
              className="rounded bg-white border border-slate-300 text-slate-800 px-2 py-1 text-xs focus:ring-brand-500"
            >
              {sizeOptions.map((opt) => (
                <option key={String(opt)} value={opt}>
                  {opt === 'ALL' ? 'Todos' : opt}
                </option>
              ))}
            </select>
          </div>
        )}
      </div>

      <div className="flex items-center gap-1.5">
        <button
          onClick={() => onPageChange(Math.max(0, effectivePage - 1))}
          disabled={isAll || effectivePage === 0}
          className="p-1.5 rounded-lg border border-slate-300 bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronLeft className="w-4 h-4" />
        </button>
        <span className="px-2 font-medium text-slate-700">
          Página {effectiveTotalPages === 0 ? 0 : effectivePage + 1} de {effectiveTotalPages}
        </span>
        <button
          onClick={() => onPageChange(Math.min(effectiveTotalPages - 1, effectivePage + 1))}
          disabled={isAll || effectivePage >= effectiveTotalPages - 1 || effectiveTotalPages === 0}
          className="p-1.5 rounded-lg border border-slate-300 bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
