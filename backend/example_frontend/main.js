document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loginBtn').onclick = () => {
      window.location.href = 'http://localhost:3000/auth/login';
    };
    
    console.log('hello');
    const urlParams = new URLSearchParams(window.location.search);
    console.log('urlParams', urlParams);
  
    const code = urlParams.get('code');

  });