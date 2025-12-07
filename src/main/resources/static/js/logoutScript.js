// ambil semua tag yg dibutuhkan
userButton = document.querySelector('#user-btn');
logoutButton = document.querySelector('#logout-btn');
logoutPopup = document.querySelector('#popup-logout');

// tampilkan popup logout saat di klik
userButton.addEventListener('click', () => {
    // periksa apakah popup sudah tampil
    if (logoutPopup.style.display === 'none') {
        logoutPopup.style.display = 'flex';
    } else {
        logoutPopup.style.display = 'none';
    }

    logoutButton.addEventListener('click', () => {
        window.location.href = '/';
    });
});