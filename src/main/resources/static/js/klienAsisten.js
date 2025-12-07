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

    // hitung id baru
    const newId = tbody.children.length + 1;

    // tambahkan ke sel ke baris baru
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <td>${newId}</td>
            <td>${namaKlien}</td>
            <td>${alamatKlien}</td>
            <td>${kontakKlien}</td>
            <td><button class="edit">Edit</td>
            <td><button class="hapus">Hapus</td>
    `
    // tambahkan ke body tabel
    tbody.appendChild(newTr);

    popup.style.display = 'none';
    formTambahKlien.reset();

    attachEditEvents();
    attachHapusEvents();
});

// fitur edit untuk data yang baru ditambahkan
function attachEditEvents(){
    document.querySelectorAll('.edit').forEach((button) => {
        button.addEventListener('click', (e) => {
            rowToEdit = e.target.closest('tr'); // simpan baris terdekat

            // ambil isi kolom (td)
            const cells = rowToEdit.querySelectorAll('td');

            document.getElementById('editNama').value = cells[1].textContent;
            document.getElementById('editAlamat').value = cells[2].textContent;
            document.getElementById('editKontak').value = cells[3].textContent;

            popupEdit.style.display = 'flex'; // tampilkan popup
        });
    });
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
};

btnClose.onclick = () => {
  popup.style.display = 'none';
};


/* Edit Asisten */
const btnEdit = document.querySelectorAll('.edit');
const popupEdit = document.querySelector('.edit-klien');
const btnCloseEdit = document.querySelector('#btnCloseEdit');
const btnSimpanEdit = document.querySelector('#btnSimpanEdit');

let rowToEdit = null; // simpan baris yang akan diedit

// Pop Up Edit
btnEdit.forEach(button => {
    button.addEventListener('click', (e) => {
        rowToEdit = e.target.closest('tr'); // simpan baris terdekat

        // ambil isi kolom (td)
        const cells = rowToEdit.querySelectorAll('td');

        document.getElementById('editNama').value = cells[1].textContent;
        document.getElementById('editAlamat').value = cells[2].textContent;
        document.getElementById('editKontak').value = cells[3].textContent;

        popupEdit.style.display = 'flex'; // tampilkan popup
    });
});

// simpan hasil edit terbaru
const formEdit = document.getElementById('formEditKlien');
formEditKlien.addEventListener('submit', (e) => {
    e.preventDefault();

    if(rowToEdit){
        const cells = rowToEdit.querySelectorAll('td');

        cells[1].textContent = document.getElementById('editNama').value;
        cells[2].textContent = document.getElementById('editAlamat').value;
        cells[3].textContent = document.getElementById('editKontak').value;
    }

    popupEdit.style.display = 'none'; // tutup kembali popup
});

btnCloseEdit.onclick = () => {
  popupEdit.style.display = 'none';
};


/* Hapus Asisten */
const btnHapus = document.querySelectorAll('.hapus')
const popupHapus = document.querySelector('.hapus-klien')
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