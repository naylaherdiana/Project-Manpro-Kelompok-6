// Versi minimalis tanpa loading indicator
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.getElementById('tambah-vendor');
const btnCloseTambah = document.getElementById('btnCloseTambah');
const popupEdit = document.getElementById('edit-vendor');
const btnCloseEdit = document.getElementById('btnCloseEdit');
const popupHapus = document.getElementById('hapus-vendor');
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
        
        // Ambil row yang diklik
        const row = e.target.closest('tr');
        const vendorId = e.target.getAttribute('data-id');
        
        // Ambil data dari cell
        const cells = row.querySelectorAll('td');
        
        // Ambil data dari row - ambil data dari data attributes atau cells
        const idjenisvendor = row.getAttribute('data-jenis-id') || 
                              row.querySelector('[data-jenis-id]')?.getAttribute('data-jenis-id') || 
                              '';
        
        // Isi form edit
        document.getElementById('editId').value = vendorId;
        document.getElementById('editNamapemilik').value = cells[1].textContent;
        document.getElementById('editNamavendor').value = cells[2].textContent;
        document.getElementById('editAlamatvendor').value = cells[3].textContent;
        document.getElementById('editKontakvendor').value = cells[4].textContent;
        
        // Set dropdown jenis vendor dengan ID yang benar
        if (idjenisvendor) {
            document.getElementById('editIdjenisvendor').value = idjenisvendor;
        } else {
            // Jika tidak ada data attribute, coba ambil dari text dan mapping
            const namaJenis = cells[5].textContent.trim();
            // Cari option yang textnya sama dengan nama jenis
            const select = document.getElementById('editIdjenisvendor');
            for (let i = 0; i < select.options.length; i++) {
                if (select.options[i].text === namaJenis) {
                    select.value = select.options[i].value;
                    break;
                }
            }
        }
        
        popupEdit.style.display = 'flex';
    }
    
    else if (e.target.classList.contains('hapus')) {
        closeAllPopups();
        document.getElementById('hapusId').value = e.target.getAttribute('data-id');
        popupHapus.style.display = 'flex';
    }
});