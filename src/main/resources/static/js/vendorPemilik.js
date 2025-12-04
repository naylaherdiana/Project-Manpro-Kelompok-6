// Versi minimalis tanpa loading indicator
const btnTambah = document.getElementById('btnTambah');
const popupTambah = document.querySelector('.tambah-vendor');
const btnCloseTambah = document.getElementById('btnCloseTambah');
const popupEdit = document.querySelector('.edit-vendor');
const btnCloseEdit = document.getElementById('btnCloseEdit');
const popupHapus = document.querySelector('.hapus-vendor');
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
        const cells = e.target.closest('tr').querySelectorAll('td');
        document.getElementById('editId').value = e.target.getAttribute('data-id');
        document.getElementById('editNamapemilik').value = cells[1].textContent;
        document.getElementById('editNamavendor').value = cells[2].textContent;
        document.getElementById('editAlamatvendor').value = cells[3].textContent;
        document.getElementById('editKontakvendor').value = cells[4].textContent;
        document.getElementById('editIdjenisvendor').value = cells[5].textContent;
        popupEdit.style.display = 'flex';
    }
    
    else if (e.target.classList.contains('hapus')) {
        closeAllPopups();
        document.getElementById('hapusId').value = e.target.getAttribute('data-id');
        popupHapus.style.display = 'flex';
    }
});