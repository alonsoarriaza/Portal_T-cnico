import React from 'react';

export const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="w-full mt-12 pt-6 pb-2 border-t border-slate-200/80 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500 select-none">
      {/* Left: Corporate Info */}
      <div className="flex items-center gap-2.5">
        <img
          src="/logo-coanda.png"
          alt="Logo Coanda"
          className="w-5 h-5 object-contain rounded"
        />
        <span>
          © {currentYear}{' '}
          <strong className="text-slate-800 font-semibold">Coanda</strong>{' '}
          · Portal Técnico Empresarial
        </span>
        <span className="hidden md:inline text-slate-300">|</span>
        <span className="hidden md:inline text-[11px] text-slate-400">
          Soporte, Mantenimiento & Auditoría TI
        </span>
      </div>

      {/* Right: Author / Management Badge */}
      <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-white border border-slate-200 shadow-xs hover:border-brand-200 transition-colors">
        <div className="w-5 h-5 rounded-full bg-gradient-to-tr from-brand-600 to-emerald-500 flex items-center justify-center text-white font-black text-[9px] shadow-xs">
          AF
        </div>
        <span className="text-[11px] text-slate-600">
          Web creada y gestionada por{' '}
          <span className="font-semibold text-slate-900">Alonso Feria Arriaza</span>
        </span>
      </div>
    </footer>
  );
};
