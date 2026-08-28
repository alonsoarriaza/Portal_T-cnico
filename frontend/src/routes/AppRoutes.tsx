import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { MainLayout } from '../components/layout/MainLayout';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { ClientesPage } from '../pages/ClientesPage';
import { ClienteDetailPage } from '../pages/ClienteDetailPage';
import { EquiposPage } from '../pages/EquiposPage';
import { ServiciosPage } from '../pages/ServiciosPage';
import { WebsPage } from '../pages/WebsPage';
import { DocumentosPage } from '../pages/DocumentosPage';
import { CronogramaPage } from '../pages/CronogramaPage';
import { AuditoriaPage } from '../pages/AuditoriaPage';
import { UsuariosPage } from '../pages/UsuariosPage';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

const ProtectedRoute: React.FC<{ children: React.ReactNode; permission?: string; role?: string }> = ({
  children,
  permission,
  role,
}) => {
  const { isAuthenticated, isLoading, hasPermission, hasRole } = useAuth();

  if (isLoading) {
    return (
      <div className="h-screen bg-slate-950 flex items-center justify-center">
        <LoadingSpinner message="Verificando sesión segura..." />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (permission && !hasPermission(permission)) {
    return <Navigate to="/dashboard" replace />;
  }

  if (role && !hasRole(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

export const AppRoutes: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="h-screen bg-slate-950 flex items-center justify-center">
        <LoadingSpinner message="Iniciando Portal ABAXIAL..." />
      </div>
    );
  }

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
      />

      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="clientes" element={<ClientesPage />} />
        <Route path="clientes/:id" element={<ClienteDetailPage />} />
        <Route path="equipos" element={<EquiposPage />} />
        <Route path="servicios" element={<ServiciosPage />} />
        <Route path="webs" element={<WebsPage />} />
        <Route path="documentos" element={<DocumentosPage />} />
        <Route path="cronograma" element={<CronogramaPage />} />
        <Route
          path="auditoria"
          element={
            <ProtectedRoute permission="HISTORIAL_VER">
              <AuditoriaPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="usuarios"
          element={
            <ProtectedRoute permission="USUARIO_VER">
              <UsuariosPage />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
