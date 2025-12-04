// Versi minimalis tanpa loading indicator
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.querySelector('.tambah-asisten');
const btnCloseTambah = document.getElementById('btnCloseTambah');
const popupEdit = document.querySelector('.edit-asisten');
const btnCloseEdit = document.getElementById('btnCloseEdit');
const popupHapus = document.querySelector('.hapus-asisten');
const btnCloseHapus = document.getElementById('btnCloseHapus');

function closeAllPopups() {
    popupTambah.style.display = 'none';
    popupEdit.style.display = 'none';
    popupHapus.style.display = 'none';
}

// Buka popup tambah
btnTambah.addEventListener('click', (e) => {
    e.preventDefault();
    closeAllPopups();
    popupTambah.style.display = 'flex';
});

// Tutup popup
btnCloseTambah.addEventListener('click', closeAllPopups);
btnCloseEdit.addEventListener('click', closeAllPopups);
btnCloseHapus.addEventListener('click', closeAllPopups);

// Handle edit & hapus button
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('edit')) {
        closeAllPopups();
        
        // Ambil data dari data-* attributes
        const id = e.target.getAttribute('data-id');
        const nama = e.target.getAttribute('data-nama');
        const alamat = e.target.getAttribute('data-alamat');
        const kontak = e.target.getAttribute('data-kontak');
        const email = e.target.getAttribute('data-email');
        
        // Isi form edit
        document.getElementById('editId').value = id;
        document.getElementById('editNama').value = nama;
        document.getElementById('editAlamat').value = alamat;
        document.getElementById('editKontak').value = kontak;
        document.getElementById('editEmail').value = email;
        
        popupEdit.style.display = 'flex';
    }
    
    else if (e.target.classList.contains('hapus')) {
        closeAllPopups();
        
        // Ambil ID dari data-id attribute
        const id = e.target.getAttribute('data-id');
        document.getElementById('hapusId').value = id;
        
        popupHapus.style.display = 'flex';
    }
});

// Tutup popup ketika klik di luar konten
window.addEventListener('click', (e) => {
    if (e.target === popupTambah || e.target === popupEdit || e.target === popupHapus) {
        closeAllPopups();
    }
});