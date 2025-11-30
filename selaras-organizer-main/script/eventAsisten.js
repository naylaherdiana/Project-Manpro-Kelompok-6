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


