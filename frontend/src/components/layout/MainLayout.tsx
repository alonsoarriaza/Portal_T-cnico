import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';
import { Footer } from './Footer';

export const MainLayout: React.FC = () => {
  const [mobileOpen, setMobileOpen] = React.useState(false);

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      {/* Sidebar Navigation */}
      <Sidebar mobileOpen={mobileOpen} onCloseMobile={() => setMobileOpen(false)} />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Navbar onOpenMobile={() => setMobileOpen(true)} />
        <main className="flex-1 overflow-y-auto flex flex-col justify-between p-4 sm:p-6 lg:p-8 bg-slate-50">
          <div className="max-w-7xl w-full mx-auto flex-1">
            <Outlet />
          </div>
          <div className="max-w-7xl w-full mx-auto">
            <Footer />
          </div>
        </main>
      </div>
    </div>
  );
};
