import { Link, useLocation } from 'react-router-dom';

const NavBar = () => {
  const location = useLocation();
  
  const navItems = [
    { path: '/admin', label: '📊 Dashboard', exact: true },
    { path: '/admin/ingredients', label: '🥦 Ingrédients' },
    { path: '/admin/recipes', label: '🍔 Recettes' },
    { path: '/admin/orders', label: '📦 Commandes' },
  ];

  return (
    <nav style={{
      background: '#4f46e5',
      padding: '0.75rem 2rem',
      boxShadow: '0 4px 10px rgba(0,0,0,0.12)',
      position: 'sticky',
      top: 0,
      zIndex: 50
    }}>
      <div style={{
        maxWidth: '1400px',
        margin: '0 auto',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{
            fontSize: '1.5rem',
            background: 'white',
            width: '44px',
            height: '44px',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            color: '#4f46e5'
          }}>
            🍽️
          </div>
          <h1 style={{
            color: 'white',
            fontSize: '1.25rem',
            fontWeight: '600',
          }}>
            Restaurant SAF - Admin
          </h1>
        </div>

        {/* Navigation */}
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {navItems.map(item => {
            const isActive = item.exact 
              ? location.pathname === item.path
              : location.pathname.startsWith(item.path);

            return (
              <Link
                key={item.path}
                to={item.path}
                style={{
                  padding: '0.5rem 1rem',
                  borderRadius: '8px',
                  textDecoration: 'none',
                  fontSize: '0.9rem',
                  fontWeight: 500,
                  color: isActive ? '#4f46e5' : 'white',
                  background: isActive ? 'white' : 'transparent',
                  boxShadow: isActive ? '0 2px 8px rgba(0,0,0,0.12)' : 'none',
                  transition: 'all 0.2s',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem'
                }}
              >
                {item.label}
              </Link>
            );
          })}
        </div>

        {/* Profil */}
        <div style={{
          color: 'white',
          fontSize: '0.875rem',
          opacity: 0.85,
          fontWeight: 500
        }}>
          Admin
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
