/* Tambah Vendor */
const btnTambah = document.getElementById('btnTambah');
const popup = document.querySelector('.tambah-vendor');
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
    const namaPemilik = formData.get('nama-pemilik');
    const namaVendor = formData.get('nama-vendor');
    const alamat = formData.get('alamat');
    const kontak = formData.get('kontak');
    const jenis = formData.get('jenis-vendor');

    // hitung id baru
    const newId = tbody.children.length + 1;

    // tambahkan ke sel ke baris baru
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <td>${newId}</td>
            <td>${namaPemilik}</td>
            <td>${namaVendor}</td>
            <td>${alamat}</td>
            <td>${kontak}</td>
            <td>${jenis}</td>
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

            document.getElementById('namaPemilik').value = cells[1].textContent;
            document.getElementById('namaVendor').value = cells[2].textContent;
            document.getElementById('alamat').value = cells[3].textContent;
            document.getElementById('kontak').value = cells[4].textContent;
            document.getElementById('jenisVendor').value = cells[5].textContent;

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


/* Edit Vendor */
const btnEdit = document.querySelectorAll('.edit');
const popupEdit = document.querySelector('.edit-vendor');
const btnCloseEdit = document.querySelector('#btnCloseEdit');
const btnSimpanEdit = document.querySelector('#btnSimpanEdit');

let rowToEdit = null; // simpan baris yang akan diedit

// Pop Up Edit
btnEdit.forEach(button => {
    button.addEventListener('click', (e) => {
        rowToEdit = e.target.closest('tr'); // simpan baris terdekat

        // ambil isi kolom (td)
        const cells = rowToEdit.querySelectorAll('td');

        document.getElementById('namaPemilik').value = cells[1].textContent;
        document.getElementById('namaVendor').value = cells[2].textContent;
        document.getElementById('alamat').value = cells[3].textContent;
        document.getElementById('kontak').value = cells[4].textContent;
        document.getElementById('jenisVendor').value = cells[5].textContent;

        popupEdit.style.display = 'flex'; // tampilkan popup
    });
});

// simpan hasil edit terbaru
const formEditVendor = document.getElementById('formEditVendor');
formEditVendor.addEventListener('submit', (e) => {
    e.preventDefault();

    if(rowToEdit){
        const cells = rowToEdit.querySelectorAll('td');

        cells[1].textContent = document.getElementById('namaPemilik').value;
        cells[2].textContent = document.getElementById('namaVendor').value;
        cells[3].textContent = document.getElementById('alamat').value;
        cells[4].textContent = document.getElementById('kontak').value;
        cells[5].textContent = document.getElementById('jenisVendor').value;
    }

    popupEdit.style.display = 'none'; // tutup kembali popup
});

btnCloseEdit.onclick = () => {
  popupEdit.style.display = 'none';
};


/* Hapus Vendor */
const btnHapus = document.querySelectorAll('.hapus')
const popupHapus = document.querySelector('.hapus-vendor')
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

