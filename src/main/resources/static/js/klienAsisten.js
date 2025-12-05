/* Tambah Klien */
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.getElementById('tambah-klien');
const btnCloseTambah = document.getElementById('btnCloseTambah');

btnTambah.onclick = () => {
  popupTambah.style.display = 'flex';
};

btnCloseTambah.onclick = () => {
  popupTambah.style.display = 'none';
};

/* Edit Klien */
const popupEdit = document.getElementById('edit-klien');
const btnCloseEdit = document.getElementById('btnCloseEdit');

// Fungsi untuk menampilkan popup edit
function showEditPopup(id, nama, alamat, kontak) {
    document.getElementById('editId').value = id;
    document.getElementById('editNama').value = nama;
    document.getElementById('editAlamat').value = alamat || '';
    document.getElementById('editKontak').value = kontak;
    
    popupEdit.style.display = 'flex';
}

// Event listener untuk tombol edit
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.edit').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nama = this.getAttribute('data-nama');
            const alamat = this.getAttribute('data-alamat');
            const kontak = this.getAttribute('data-kontak');
            
            showEditPopup(id, nama, alamat, kontak);
        });
    });
});

btnCloseEdit.onclick = () => {
  popupEdit.style.display = 'none';
};

/* Hapus Klien */
const popupHapus = document.getElementById('hapus-klien');
const btnCloseHapus = document.getElementById('btnCloseHapus');

// Fungsi untuk menampilkan popup hapus
function showDeletePopup(id, nama) {
    document.getElementById('deleteId').value = id;
    document.getElementById('deleteKlienName').textContent = nama;
    popupHapus.style.display = 'flex';
}

// Event listener untuk tombol hapus
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.hapus').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nama = this.getAttribute('data-nama');
            
            showDeletePopup(id, nama);
        });
    });
});

btnCloseHapus.onclick = () => {
    popupHapus.style.display = 'none';
};

// Tutup popup saat klik di luar konten
window.onclick = function(event) {
    if (event.target.classList.contains('tambah-klien')) {
        popupTambah.style.display = 'none';
    }
    if (event.target.classList.contains('edit-klien')) {
        popupEdit.style.display = 'none';
    }
    if (event.target.classList.contains('hapus-klien')) {
        popupHapus.style.display = 'none';
    }
}