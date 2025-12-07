/* Tambah Vendor */
const btnTambah = document.getElementById('btnTambah');
const popup = document.querySelector('.tambah-jenis-vendor');
const btnClose = document.getElementById('btnCloseTambah');

const formTambahJenisVendor = document.getElementById('formTambahJenisVendor');
const btnSimpanTambah = document.getElementById('btnSimpanTambah');
const tbody = document.querySelector('tbody');

btnTambah.onclick = () => {
  popup.style.display = 'flex';
};

function formatRupiah(angka) {
    return "Rp " + Number(angka).toLocaleString('id-ID');
}

// tambah jenis vendor baru
formTambahJenisVendor.addEventListener('submit', (e) => {
    e.preventDefault();

    // ambil semua input dalam form
    const formData = new FormData(formTambahJenisVendor);
    const kisaranMinimal = formData.get('kisaran-minimal');
    const kisaranMaksimal = formData.get('kisaran-maksimal');
    const jenisVendor = formData.get('jenis-vendor');

    // hitung id baru
    const newId = tbody.children.length + 1;

    // tambahkan ke sel ke baris baru
    const newTr = document.createElement('tr');
    newTr.innerHTML = `
            <td>${newId}</td>
            <td>${formatRupiah(kisaranMinimal)}</td>
            <td>${formatRupiah(kisaranMaksimal)}</td>
            <td>${jenisVendor}</td>
            <td><button class="edit">Edit</td>
            <td><button class="hapus">Hapus</td>
    `
    // tambahkan ke body tabel
    tbody.appendChild(newTr);

    popup.style.display = 'none';
    formTambahJenisVendor.reset();

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
            
            document.getElementById('kisaranMinimal').value = cells[1].textContent.replace(/\D/g, '');;
            document.getElementById('kisaranMaksimal').value = cells[2].textContent.replace(/\D/g, '');;
            document.getElementById('jenisVendor').value = cells[3].textContent;

            popupEdit.style.display = 'flex'; // tampilkan popup
        })
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


/* Edit Jenis Vendor */
const btnEdit = document.querySelectorAll('.edit');
const popupEdit = document.querySelector('.edit-jenis-vendor');
const btnCloseEdit = document.querySelector('#btnCloseEdit');
const btnSimpanEdit = document.querySelector('#btnSimpanEdit');
let rowToEdit = null; // simpan baris yang akan diedit

// Pop Up Edit
btnEdit.forEach(button => {
    button.addEventListener('click', (e) => {
        rowToEdit = e.target.closest('tr'); // simpan baris terdekat

        // ambil isi kolom (td)
        const cells = rowToEdit.querySelectorAll('td');
        
        document.getElementById('kisaranMinimal').value = cells[1].textContent.replace(/\D/g, '');;
        document.getElementById('kisaranMaksimal').value = cells[2].textContent.replace(/\D/g, '');;
        document.getElementById('jenisVendor').value = cells[3].textContent;

        popupEdit.style.display = 'flex'; // tampilkan popup
    });
});

// simpan hasil edit terbaru
const formEditJenisVendor = document.getElementById('formEditJenisVendor');
formEditJenisVendor.addEventListener('submit', (e) => {
    e.preventDefault();

    if(rowToEdit){
        const cells = rowToEdit.querySelectorAll('td');

        cells[1].textContent = formatRupiah(document.getElementById('kisaranMinimal').value);
        cells[2].textContent = formatRupiah(document.getElementById('kisaranMaksimal').value);
        cells[3].textContent = document.getElementById('jenisVendor').value;
    }

    popupEdit.style.display = 'none'; // tutup kembali popup
});

btnCloseEdit.onclick = () => {
  popupEdit.style.display = 'none';
};


/* Hapus Jenis Vendor */
const btnHapus = document.querySelectorAll('.hapus')
const popupHapus = document.querySelector('.hapus-jenis-vendor')
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

