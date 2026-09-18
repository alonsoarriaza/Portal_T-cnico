import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Shield, Lock, User, ArrowRight, Eye, EyeOff, Sparkles } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';

export const LoginPage: React.FC = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const { error, success } = useToast();
  const navigate = useNavigate();

  const handleLogin = async (userVal: string, passVal: string) => {
    if (!userVal.trim() || !passVal.trim()) {
      error('Por favor, introduce usuario y contraseña');
      return;
    }

    try {
      setIsLoading(true);
      await login(userVal, passVal);
      success('Sesión iniciada correctamente');
      navigate('/dashboard');
    } catch (err: any) {
      const msg =
        err.response?.data?.message ||
        'Credenciales no válidas o error de conexión';
      error(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleLogin(usernameOrEmail, password);
  };

  const handleAutoFillAndLogin = () => {
    setUsernameOrEmail('admin');
    setPassword('AdminCoanda2026!');
    handleLogin('admin', 'AdminCoanda2026!');
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md bg-white border border-slate-200 rounded-2xl shadow-xl p-6 sm:p-8 space-y-6">
        {/* Header Branding */}
        <div className="text-center space-y-3">
          <div className="w-20 h-20 rounded-2xl bg-white border border-slate-200/80 flex items-center justify-center p-2 mx-auto shadow-md">
            <img
              src="/logo-coanda.png"
              alt="Logo Coanda"
              className="w-full h-full object-contain"
            />
          </div>
          <div>
            <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
              Portal Coanda
            </h1>
            <p className="text-xs text-slate-500 mt-1">
              Acceso exclusivo para soporte y gestión técnica
            </p>
          </div>
        </div>

        {/* Quick Fill Button */}
        <button
          type="button"
          onClick={handleAutoFillAndLogin}
          disabled={isLoading}
          className="w-full py-2.5 px-4 bg-brand-50 hover:bg-brand-100 border border-brand-200 text-brand-700 rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all shadow-sm active:scale-[0.99]"
        >
          <Sparkles className="w-4 h-4 text-brand-600" />
          <span>Acceso Rápido Administrador (1 clic)</span>
        </button>

        <div className="relative flex py-1 items-center">
          <div className="flex-grow border-t border-slate-200"></div>
          <span className="flex-shrink mx-3 text-slate-400 text-xs uppercase font-semibold">o con tus credenciales</span>
          <div className="flex-grow border-t border-slate-200"></div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Usuario o Correo Electrónico"
            type="text"
            required
            placeholder="admin"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
            leftIcon={<User className="w-4 h-4" />}
            autoFocus
          />

          <div className="relative">
            <Input
              label="Contraseña"
              type={showPassword ? 'text' : 'password'}
              required
              placeholder="••••••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              leftIcon={<Lock className="w-4 h-4" />}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-[34px] text-slate-400 hover:text-slate-600 focus:outline-none"
              tabIndex={-1}
            >
              {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>

          <Button
            type="submit"
            variant="primary"
            className="w-full mt-2"
            isLoading={isLoading}
            rightIcon={<ArrowRight className="w-4 h-4" />}
          >
            Iniciar Sesión
          </Button>
        </form>

        <div className="bg-slate-50 border border-slate-200 rounded-xl p-3 text-center text-xs text-slate-600 space-y-1">
          <div className="font-semibold text-slate-800">Credenciales por defecto:</div>
          <div>Usuario: <code className="font-mono bg-white px-1.5 py-0.5 rounded border border-slate-200 text-slate-900 font-bold">admin</code></div>
          <div>Contraseña: <code className="font-mono bg-white px-1.5 py-0.5 rounded border border-slate-200 text-slate-900 font-bold">AdminCoanda2026!</code> o <code className="font-mono bg-white px-1.5 py-0.5 rounded border border-slate-200 text-slate-900 font-bold">admin</code></div>
        </div>
      </div>

      {/* Footer Branding for Login */}
      <div className="text-center space-y-2 select-none">
        <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/90 backdrop-blur-xs border border-slate-200 shadow-xs text-xs text-slate-600">
          <div className="w-5 h-5 rounded-full bg-gradient-to-tr from-brand-600 to-emerald-500 flex items-center justify-center text-white font-black text-[9px] shadow-xs">
            AF
          </div>
          <span>
            Web creada y gestionada por <strong className="text-slate-900 font-semibold">Alonso Feria Arriaza</strong>
          </span>
        </div>
        <p className="text-[11px] text-slate-400">
          © {new Date().getFullYear()} Coanda · Portal Técnico Empresarial · Todos los derechos reservados
        </p>
      </div>
    </div>
  );
};
