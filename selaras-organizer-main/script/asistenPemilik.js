/* Tambah Asisten */
const btnTambah = document.getElementById('btnTambah');
const popup = document.querySelector('.tambah-asisten');
const btnClose = document.getElementById('btnCloseTambah');

const formTambah = document.getElementById('formTambah');
const btnSimpanTambah = document.getElementById('btnSimpanTambah');
const tbody = document.querySelector('tbody');

btnTambah.onclick = () => {
  popup.style.display = 'flex';
};

// tambah asisten baru
formTambah.addEventListener('submit', (e) => {
    e.preventDefault();

    // ambil semua input dalam form
    const formData = new FormData(formTambah);
    const nama = formData.get('nama-asisten');
    const alamat = formData.get('alamat');
    const kontak = formData.get('kontak');
    const email = formData.get('email');

    // hitung id baru
    const newId = tbody.children.length + 1;

    // tambahkan ke sel ke baris baru
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <td>${newId}</td>
            <td>${nama}</td>
            <td>${alamat}</td>
            <td>${kontak}</td>
            <td>${email}</td>
            <td><button class="edit">Edit</td>
            <td><button class="hapus">Hapus</td>
    `
    // tambahkan ke body tabel
    tbody.appendChild(newTr);

    popup.style.display = 'none';
    formTambah.reset();

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
            document.getElementById('editEmail').value = cells[4].textContent;

            popupEdit.style.display = 'flex'; // tampilkan popup
        });
    });
}

// fitur hapus untuk data yang baru ditambahkan
function attachHapusEvents(){
    document.querySelectorAll('.hapus').forEach((button) => {
        button.addEventListener('click', (e) => {
            popupHapus.style.display = 'flex';

            rowToDelete = e.target.closest('tr');
            btnConfirmHapus.addEventListener('click', () => {
                if(rowToDelete){
                    rowToDelete.remove();  // hapus baris dari tabel
                }
                popupHapus.style.display = 'none';
            });
        });
    });
};

btnClose.onclick = () => {
  popup.style.display = 'none';
};


/* Edit Asisten */
const btnEdit = document.querySelectorAll('.edit');
const popupEdit = document.querySelector('.edit-asisten');
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
        document.getElementById('editEmail').value = cells[4].textContent;

        popupEdit.style.display = 'flex'; // tampilkan popup
    });
});

// simpan hasil edit terbaru
const formEdit = document.getElementById('formEdit');
formEdit.addEventListener('submit', (e) => {
    e.preventDefault();

    if(rowToEdit){
        const cells = rowToEdit.querySelectorAll('td');

        cells[1].textContent = document.getElementById('editNama').value;
        cells[2].textContent = document.getElementById('editAlamat').value;
        cells[3].textContent = document.getElementById('editKontak').value;
        cells[4].textContent = document.getElementById('editEmail').value;
    }

    popupEdit.style.display = 'none'; // tutup kembali popup
});

btnCloseEdit.onclick = () => {
  popupEdit.style.display = 'none';
};


/* Hapus Asisten */
const btnHapus = document.querySelectorAll('.hapus')
const popupHapus = document.querySelector('.hapus-asisten')
const btnCloseHapus = document.getElementById('btnCloseHapus');
const btnConfirmHapus = document.getElementById('btnConfirmHapus')
let rowToDelete = null;

// fungsi untuk menyesuaikan id saat baris data tertentu dihapus
function updateTableIds() {
    const rows = document.querySelectorAll('tbody tr');
    rows.forEach((row, index) => {
        row.querySelector('td').textContent = index + 1;
    });
}

// Pop up Hapus
btnHapus.forEach(button => {
    button.onclick = (e) => {
        popupHapus.style.display = 'flex';

        rowToDelete = e.target.closest('tr');
        btnConfirmHapus.addEventListener('click', () => {
            if(rowToDelete){
                rowToDelete.remove();  // hapus baris dari tabel
                updateTableIds();
            }
            popupHapus.style.display = 'none';
        });
    };
})

btnCloseHapus.onclick = () => {
    popupHapus.style.display = 'none';
};

