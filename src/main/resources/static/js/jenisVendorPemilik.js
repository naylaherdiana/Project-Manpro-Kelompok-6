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

// Fungsi untuk format angka ke Rupiah
function formatRupiah(angka) {
    return 'Rp ' + angka.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// Fungsi untuk parse Rupiah ke angka
function parseRupiah(rupiahStr) {
    const cleaned = rupiahStr.replace(/[^0-9]/g, '');
    return cleaned ? parseInt(cleaned, 10) : 0;
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
        const namaJenis = cells[3].textContent.trim(); // Ambil dari kolom ke-4 (indeks 3)
        
        // Parse dari format Rupiah ke angka
        const hargaMinAngka = parseRupiah(hargaMinText);
        const hargaMaxAngka = parseRupiah(hargaMaxText);
        
        console.log("Data yang diambil:");
        console.log("- ID:", id);
        console.log("- Harga Min (parsed):", hargaMinAngka);
        console.log("- Harga Max (parsed):", hargaMaxAngka);
        console.log("- Nama Jenis:", namaJenis);
        
        // Isi form edit
        document.getElementById('editId').value = id;
        document.getElementById('editKisaranhargamin').value = hargaMinAngka;
        document.getElementById('editKisaranhargamax').value = hargaMaxAngka;
        document.getElementById('editnamajenisvendor').value = namaJenis; // Isi input text nama
        
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

// --- TAMBAHAN: Validasi format input harga ---
document.addEventListener('DOMContentLoaded', function() {
    // Format input harga saat user mengetik
    const hargaInputs = document.querySelectorAll('input[name="kisaranhargamin"], input[name="kisaranhargamax"], #editKisaranhargamin, #editKisaranhargamax');
    
    hargaInputs.forEach(input => {
        input.addEventListener('input', function(e) {
            // Hapus semua karakter non-angka
            let value = e.target.value.replace(/[^0-9]/g, '');
            
            // Tambahkan titik sebagai pemisah ribuan
            if (value.length > 3) {
                value = value.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
            }
            
            // Tambahkan "Rp " di depan jika ada nilai
            if (value) {
                e.target.value = 'Rp ' + value;
            } else {
                e.target.value = '';
            }
        });
    });
});