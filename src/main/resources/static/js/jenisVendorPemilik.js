// --- DOM Elements ---
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.getElementById('tambah-jenis-vendor');
const btnCloseTambah = document.getElementById('btnCloseTambah');

const popupEdit = document.getElementById('edit-jenis-vendor');
const btnCloseEdit = document.getElementById('btnCloseEdit');

const popupHapus = document.getElementById('hapus-jenis-vendor');
const btnCloseHapus = document.getElementById('btnCloseHapus');

// --- Helper: Tutup Semua Popup ---
function closeAllPopups() {
    popupTambah.style.display = 'none';
    popupEdit.style.display = 'none';
    popupHapus.style.display = 'none';
}

// --- Event: Buka Popup Tambah ---
btnTambah.addEventListener('click', (e) => {
    e.preventDefault();
    closeAllPopups();
    popupTambah.style.display = 'flex';
});

// --- Event: Tutup Popup ---
btnCloseTambah.addEventListener('click', closeAllPopups);
btnCloseEdit.addEventListener('click', closeAllPopups);
btnCloseHapus.addEventListener('click', closeAllPopups);

function formatRupiah(angka) {
    return 'Rp ' + angka.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function parseRupiah(rupiahStr) {
    return parseInt(rupiahStr.replace(/[^0-9]/g, ''));
}

// --- Event Delegation: Handle Edit & Hapus ---
document.addEventListener('click', (e) => {
    // Handle Edit Button
    if (e.target.classList.contains('edit')) {
        closeAllPopups();
        
        const row = e.target.closest('tr');
        const cells = row.querySelectorAll('td');
        
        const id = e.target.getAttribute('data-id');
        const hargaMinText = cells[1].textContent.trim();
        const hargaMaxText = cells[2].textContent.trim();
        const namaJenis = cells[3].textContent.trim();
        
        // Parse dari format Rupiah ke angka
        const hargaMinAngka = parseRupiah(hargaMinText);
        const hargaMaxAngka = parseRupiah(hargaMaxText);
        
        console.log("Harga Min (parsed):", hargaMinAngka);
        console.log("Harga Max (parsed):", hargaMaxAngka);
        
        document.getElementById('editId').value = id;
        document.getElementById('editKisaranhargamin').value = hargaMinAngka;
        document.getElementById('editKisaranhargamax').value = hargaMaxAngka;
        document.getElementById('editNamajenisvendor').value = namaJenis;
        
        popupEdit.style.display = 'flex';
    }

    // Handle Hapus Button
    else if (e.target.classList.contains('hapus')) {
        closeAllPopups();
        
        const id = e.target.getAttribute('data-id');
        document.getElementById('hapusId').value = id;
        
        popupHapus.style.display = 'flex';
    }
});

// --- TAMBAHAN: Reset form ketika popup ditutup ---
btnCloseTambah.addEventListener('click', function() {
    document.getElementById('formTambah').reset();
});

btnCloseEdit.addEventListener('click', function() {
    document.getElementById('formEdit').reset();
});