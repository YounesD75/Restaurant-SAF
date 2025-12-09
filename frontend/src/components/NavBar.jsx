import { Link, useLocation } from 'react-router-dom';
//import '../styles/global.css';

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
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '1rem 1.5rem',
      boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{
            fontSize: '1.5rem',
            background: 'white',
            width: '40px',
            height: '40px',
            borderRadius: '8px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            color: '#667eea'
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
        
        <div style={{ display: 'flex', gap: '0.25rem' }}>
          {navItems.map((item) => {
            const isActive = item.exact 
              ? location.pathname === item.path
              : location.pathname.startsWith(item.path);
            
            return (
              <Link
                key={item.path}
                to={item.path}
                style={{
                  padding: '0.5rem 1rem',
                  borderRadius: '6px',
                  textDecoration: 'none',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  color: isActive ? '#667eea' : 'white',
                  background: isActive ? 'white' : 'transparent',
                  transition: 'all 0.2s',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                }}
              >
                {item.label}
              </Link>
            );
          })}
        </div>
        
        <div style={{
          color: 'white',
          fontSize: '0.875rem',
          opacity: 0.8,
        }}>
          Admin
        </div>
      </div>
    </nav>
  );
};

export default NavBar;