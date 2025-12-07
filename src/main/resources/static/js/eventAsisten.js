// DOM Elements
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.getElementById('tambah-event');
const popupEdit = document.getElementById('edit-event');
const popupHapus = document.getElementById('hapus-event');
const btnCloseTambah = document.getElementById('btnCloseTambah');
const btnCloseEdit = document.getElementById('btnCloseEdit');
const btnCloseHapus = document.getElementById('btnCloseHapus');

// Format tanggal untuk input date (YYYY-MM-DD)
function formatDateForInput(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Popup Tambah Event
btnTambah.onclick = () => {
    popupTambah.style.display = 'flex';
    // Reset form
    document.getElementById('formTambahEvent').reset();
};

btnCloseTambah.onclick = () => {
    popupTambah.style.display = 'none';
};

// Setup event listeners untuk tombol edit
document.addEventListener('DOMContentLoaded', function() {
    // Edit buttons
    document.querySelectorAll('.edit').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nama = this.getAttribute('data-nama');
            const jenis = this.getAttribute('data-jenis');
            const tanggal = this.getAttribute('data-tanggal');
            const undangan = this.getAttribute('data-undangan');
            const status = this.getAttribute('data-status');
            const idklien = this.getAttribute('data-idklien');
            
            // Isi form edit
            document.getElementById('editId').value = id;
            document.getElementById('editNama').value = nama;
            document.getElementById('editJenis').value = jenis;
            document.getElementById('editTanggal').value = formatDateForInput(tanggal);
            document.getElementById('editUndangan').value = undangan;
            document.getElementById('editStatus').value = status;
            document.getElementById('editKlien').value = idklien;
            
            // Tampilkan popup edit
            popupEdit.style.display = 'flex';
        });
    });
    
    // Hapus buttons
    document.querySelectorAll('.hapus').forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const nama = this.getAttribute('data-nama');
            
            document.getElementById('deleteId').value = id;
            document.getElementById('deleteEventName').textContent = nama;
            
            // Tampilkan popup hapus
            popupHapus.style.display = 'flex';
        });
    });
});

// Close popup edit
btnCloseEdit.onclick = () => {
    popupEdit.style.display = 'none';
};

// Close popup hapus
btnCloseHapus.onclick = () => {
    popupHapus.style.display = 'none';
};

// Tutup popup saat klik di luar konten
window.onclick = function(event) {
    if (event.target.classList.contains('tambah-event')) {
        popupTambah.style.display = 'none';
    }
    if (event.target.classList.contains('edit-event')) {
        popupEdit.style.display = 'none';
    }
    if (event.target.classList.contains('hapus-event')) {
        popupHapus.style.display = 'none';
    }
}

// Validasi form sebelum submit (optional)
document.getElementById('formTambahEvent').addEventListener('submit', function(e) {
    const tanggal = this.querySelector('input[name="tanggal"]').value;
    const today = new Date().toISOString().split('T')[0];
    
    // Validasi: tanggal tidak boleh di masa lalu untuk event baru
    if (tanggal < today) {
        e.preventDefault();
        alert('Tanggal event tidak boleh di masa lalu');
    }
});
/* Tambah Event */
const btnTambah = document.getElementById('btnTambah');
const popup = document.querySelector('.tambah-event');
const btnClose = document.getElementById('btnCloseTambah');

const formTambahEvent = document.getElementById('formTambahEvent');
const btnSimpanTambah = document.getElementById('btnSimpanTambah');
const tbody = document.querySelector('.table-container:nth-of-type(2) tbody');

btnTambah.onclick = () => {
  popup.style.display = 'flex';
};

// tambah klien baru
formTambahEvent.addEventListener('submit', (e) => {
    e.preventDefault();

    // ambil semua input dalam form
    const formData = new FormData(formTambahEvent);
    const namaEvent = formData.get('namaEvent');
    const jenisEvent = formData.get('jenisEvent');
    const tanggalEvent = formData.get('tanggalEvent');
    const jumlahUndangan = formData.get('jumlahUndangan');
    const statusEvent = formData.get('statusEvent');
    const klien = formData.get('klien');

    // hitung id baru
    const newId = tbody.children.length + 1;

    // tambahkan ke sel ke baris baru
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <td>${newId}</td>
            <td>${namaEvent}</td>
            <td>${jenisEvent}</td>
            <td>${tanggalEvent}</td>
            <td>${jumlahUndangan}</td>
            <td>${statusEvent}</td>
            <td>${klien}</td>
    `
    // tambahkan ke body tabel
    tbody.appendChild(newTr);

    popup.style.display = 'none';
    formTambahEvent.reset();
});


btnClose.onclick = () => {
  popup.style.display = 'none';
};


