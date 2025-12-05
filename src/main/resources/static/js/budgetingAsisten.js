// budgetingAsisten.js - Versi dengan data real dari backend
document.addEventListener('DOMContentLoaded', function() {
    
    // ========== VARIABLES ==========
    const modalVendor = document.getElementById('modalVendor');
    const closeModal = document.querySelector('.close-modal');
    const btnResetFilter = document.getElementById('btnResetFilter');
    const modalVendorBody = document.getElementById('modalVendorBody');
    const modalTitle = document.getElementById('modalTitle');
    const formEditHarga = document.querySelector('.form-edit-harga');
    const btnCancelEdit = document.querySelector('.btn-cancel');
    const selectEvent = document.getElementById('selectEvent');
    const selectVendor = document.getElementById('selectVendor');
    const hargaDealingInput = document.getElementById('hargaDealing');
    
    // CSRF Token (jika menggunakan Spring Security)
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || '';
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
    
    // ========== EVENT LISTENERS ==========
    
    // Reset filter button
    if (btnResetFilter) {
        btnResetFilter.addEventListener('click', function() {
            document.getElementById('hargaMin').value = '';
            document.getElementById('hargaMax').value = '';
            document.querySelector('.filter-form').submit();
        });
    }
    
    // Setup tombol "Lihat Vendor" berdasarkan jenis
    setupLihatVendorButtons();
    
    // Tombol close modal
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            modalVendor.style.display = 'none';
        });
    }
    
    // Close modal saat klik di luar
    window.addEventListener('click', function(event) {
        if (event.target === modalVendor) {
            modalVendor.style.display = 'none';
        }
    });
    
    // Cancel edit button
    if (btnCancelEdit) {
        btnCancelEdit.addEventListener('click', function() {
            formEditHarga.style.display = 'none';
        });
    }
    
    // Event change untuk select event (load vendor berdasarkan event)
    if (selectEvent) {
        selectEvent.addEventListener('change', function() {
            const eventId = this.value;
            const eventText = this.options[this.selectedIndex]?.text || '';
            
            if (eventId) {
                // Tampilkan loading
                showLoadingVendorEventTable();
                // Load vendor untuk event ini dari backend
                loadVendorsForEventFromBackend(eventId, eventText);
            } else {
                clearVendorEventTable();
            }
        });
    }
    
    // Event change untuk select vendor (auto-fill harga)
    if (selectVendor && hargaDealingInput) {
        selectVendor.addEventListener('change', function() {
            const vendorId = this.value;
            if (vendorId) {
                // Ambil harga default dari vendor
                getVendorDefaultPrice(vendorId);
            } else {
                hargaDealingInput.value = '';
            }
        });
    }
    
    // Setup edit/hapus buttons untuk vendor event
    setupVendorEventButtons();
    
    // Validasi form tambah vendor
    const formTambahVendor = document.querySelector('.form-tambah-vendor');
    if (formTambahVendor) {
        formTambahVendor.addEventListener('submit', validateTambahVendorForm);
    }
    
    // Validasi form edit harga
    const formEdit = document.querySelector('.form-edit-harga form');
    if (formEdit) {
        formEdit.addEventListener('submit', validateEditHargaForm);
    }
    
    // ========== FUNCTIONS ==========
    
    // Setup tombol "Lihat Vendor"
    function setupLihatVendorButtons() {
        document.querySelectorAll('.btn-lihat-vendor').forEach(button => {
            button.addEventListener('click', function() {
                const jenisId = this.getAttribute('data-id');
                const jenisNama = this.getAttribute('data-nama');
                
                modalTitle.textContent = 'Daftar Vendor - ' + jenisNama;
                showVendorByJenisFromBackend(jenisId, jenisNama);
                modalVendor.style.display = 'block';
            });
        });
    }
    
    // Tampilkan vendor berdasarkan jenis (dari backend)
    function showVendorByJenisFromBackend(jenisId, jenisNama) {
        // Tampilkan loading
        modalVendorBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center;">
                    <div class="loading-spinner">Memuat data vendor...</div>
                </td>
            </tr>
        `;
        
        // Fetch data dari backend
        fetch(`/budgeting-asisten/get-vendors-by-jenis?jenisId=${jenisId}`)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(vendors => {
                if (vendors && vendors.length > 0) {
                    renderVendorModal(vendors);
                } else {
                    modalVendorBody.innerHTML = `
                        <tr>
                            <td colspan="7" style="text-align: center;">
                                Tidak ada vendor untuk jenis ${jenisNama}
                            </td>
                        </tr>
                    `;
                }
            })
            .catch(error => {
                console.error('Error fetching vendors:', error);
                modalVendorBody.innerHTML = `
                    <tr>
                        <td colspan="7" style="text-align: center; color: #dc3545;">
                            Gagal memuat data vendor: ${error.message}
                        </td>
                    </tr>
                `;
            });
    }
    
    // Render vendor di modal
    function renderVendorModal(vendors) {
        modalVendorBody.innerHTML = '';
        
        vendors.forEach(vendor => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${vendor.idvendor || 'N/A'}</td>
                <td>${vendor.namavendor || 'N/A'}</td>
                <td>${vendor.namapemilik || 'N/A'}</td>
                <td>${vendor.kontakvendor || 'N/A'}</td>
                <td>Rp ${vendor.kisaranhargamin ? formatRupiah(vendor.kisaranhargamin) : '0'}</td>
                <td>Rp ${vendor.kisaranhargamax ? formatRupiah(vendor.kisaranhargamax) : '0'}</td>
                <td>
                    <button class="btn-pilih-vendor" 
                            data-id="${vendor.idvendor}" 
                            data-nama="${vendor.namavendor}"
                            data-hargamin="${vendor.kisaranhargamin || 0}"
                            data-hargamax="${vendor.kisaranhargamax || 0}">
                        Pilih
                    </button>
                </td>
            `;
            modalVendorBody.appendChild(row);
        });
        
        // Setup tombol pilih vendor
        setupPilihVendorButtons();
    }
    
    // Setup tombol "Pilih" di modal vendor
    function setupPilihVendorButtons() {
        document.querySelectorAll('.btn-pilih-vendor').forEach(button => {
            button.addEventListener('click', function() {
                const vendorId = this.getAttribute('data-id');
                const vendorNama = this.getAttribute('data-nama');
                const hargaMin = this.getAttribute('data-hargamin');
                const hargaMax = this.getAttribute('data-hargamax');
                
                // Set value di select vendor
                if (selectVendor) {
                    selectVendor.value = vendorId;
                    
                    // Auto-fill harga dengan harga rata-rata
                    if (hargaDealingInput) {
                        const avgPrice = Math.round((parseFloat(hargaMin) + parseFloat(hargaMax)) / 2);
                        hargaDealingInput.value = avgPrice;
                    }
                    
                    // Scroll ke form tambah vendor
                    document.getElementById('tambah-vendor').scrollIntoView({ 
                        behavior: 'smooth' 
                    });
                    
                    // Tampilkan pesan
                    showNotification(`Vendor "${vendorNama}" telah dipilih`, 'success');
                }
                
                // Tutup modal
                modalVendor.style.display = 'none';
            });
        });
    }
    
    // Load vendor untuk event tertentu dari backend
    function loadVendorsForEventFromBackend(eventId, eventText) {
        fetch(`/budgeting-asisten/get-event-vendors?eventId=${eventId}`)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(vendorEvents => {
                renderVendorEventTable(vendorEvents, eventText);
            })
            .catch(error => {
                console.error('Error fetching event vendors:', error);
                showNotification(`Gagal memuat vendor untuk event: ${error.message}`, 'error');
                clearVendorEventTable();
            });
    }
    
    // Render tabel vendor event
    function renderVendorEventTable(vendorEvents, eventText) {
        const tabelBody = document.querySelector('#tabel-vendor-event tbody');
        
        if (!vendorEvents || vendorEvents.length === 0) {
            tabelBody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align: center;">
                        Belum ada vendor yang ditambahkan ke event "${eventText}"
                    </td>
                </tr>
            `;
            return;
        }
        
        tabelBody.innerHTML = '';
        
        vendorEvents.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${item.idevent || 'N/A'}</td>
                <td>${item.namaevent || 'N/A'}</td>
                <td>${item.namavendor || 'N/A'}</td>
                <td>${item.namajenisvendor || 'Tidak ada data'}</td>
                <td>Rp ${item.hargadealing ? formatRupiah(item.hargadealing) : '0'}</td>
                <td>
                    <span class="status-dealing ${item.statusdealing ? item.statusdealing.toLowerCase() : ''}">
                        ${item.statusdealing || 'N/A'}
                    </span>
                </td>
                <td>
                    <button class="btn-edit" 
                            data-idevent="${item.idevent}" 
                            data-idvendor="${item.idvendor}" 
                            data-harga="${item.hargadealing}" 
                            data-status="${item.statusdealing}"
                            data-jenis="${item.namajenisvendor || ''}">
                        Edit
                    </button>
                    <button class="btn-hapus" 
                            data-idevent="${item.idevent}" 
                            data-idvendor="${item.idvendor}" 
                            data-nama="${item.namavendor}"
                            data-jenis="${item.namajenisvendor || ''}">
                        Hapus
                    </button>
                </td>
            `;
            tabelBody.appendChild(row);
        });
        
        // Setup ulang tombol edit/hapus
        setupVendorEventButtons();
    }
    
    // Tampilkan loading di tabel vendor event
    function showLoadingVendorEventTable() {
        const tabelBody = document.querySelector('#tabel-vendor-event tbody');
        tabelBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center;">
                    <div class="loading-spinner">Memuat data vendor...</div>
                </td>
            </tr>
        `;
    }
    
    // Setup tombol edit/hapus untuk vendor event
    function setupVendorEventButtons() {
        // Tombol edit
        document.querySelectorAll('.btn-edit').forEach(button => {
            button.addEventListener('click', function() {
                const idevent = this.getAttribute('data-idevent');
                const idvendor = this.getAttribute('data-idvendor');
                const harga = this.getAttribute('data-harga');
                const status = this.getAttribute('data-status');
                
                // Isi form edit
                document.getElementById('editIdevent').value = idevent;
                document.getElementById('editIdvendor').value = idvendor;
                document.getElementById('editHarga').value = harga;
                document.getElementById('editStatus').value = status;
                
                // Tampilkan form edit
                formEditHarga.style.display = 'block';
                formEditHarga.scrollIntoView({ behavior: 'smooth' });
            });
        });
        
        // Tombol hapus
        document.querySelectorAll('.btn-hapus').forEach(button => {
            button.addEventListener('click', function() {
                const idevent = this.getAttribute('data-idevent');
                const idvendor = this.getAttribute('data-idvendor');
                const namaVendor = this.getAttribute('data-nama');
                const eventSelect = document.getElementById('selectEvent');
                const eventText = eventSelect.options[eventSelect.selectedIndex]?.text || '';
                
                if (confirm(`Apakah Anda yakin ingin menghapus vendor "${namaVendor}" dari event "${eventText}"?`)) {
                    // Kirim request hapus ke backend
                    deleteVendorFromEventBackend(idevent, idvendor, namaVendor);
                }
            });
        });
    }
    
    // Hapus vendor dari event (backend)
    function deleteVendorFromEventBackend(idevent, idvendor, namaVendor) {
        showNotification(`Menghapus vendor "${namaVendor}"...`, 'info');
        
        // Kirim request DELETE ke backend
        fetch(`/budgeting-asisten/hapus-vendor?idevent=${idevent}&idvendor=${idvendor}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [csrfHeader]: csrfToken
            }
        })
        .then(response => {
            if (!response.ok) throw new Error('Gagal menghapus vendor');
            return response.text();
        })
        .then(() => {
            // Reload data vendor untuk event
            loadVendorsForEventFromBackend(idevent, '');
            showNotification(`Vendor "${namaVendor}" berhasil dihapus`, 'success');
        })
        .catch(error => {
            console.error('Error deleting vendor:', error);
            showNotification(`Gagal menghapus vendor: ${error.message}`, 'error');
        });
    }
    
    // Ambil harga default dari vendor
    function getVendorDefaultPrice(vendorId) {
        fetch(`/budgeting-asisten/get-vendor-detail?vendorId=${vendorId}`)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(vendor => {
                if (vendor && vendor.kisaranhargamin && vendor.kisaranhargamax) {
                    // Set harga dealing dengan harga rata-rata
                    const avgPrice = Math.round((vendor.kisaranhargamin + vendor.kisaranhargamax) / 2);
                    if (hargaDealingInput) {
                        hargaDealingInput.value = avgPrice;
                        showNotification(`Harga dealing diisi dengan rata-rata: Rp ${formatRupiah(avgPrice)}`, 'info');
                    }
                }
            })
            .catch(error => {
                console.error('Error fetching vendor detail:', error);
                // Tetap lanjutkan meski gagal ambil harga default
            });
    }
    
    // Clear vendor event table
    function clearVendorEventTable() {
        const tabelBody = document.querySelector('#tabel-vendor-event tbody');
        tabelBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center;">Pilih event terlebih dahulu untuk melihat vendor</td>
            </tr>
        `;
    }
    
    // Validasi form tambah vendor
    function validateTambahVendorForm(e) {
        const hargaDealing = document.getElementById('hargaDealing').value;
        const selectEvent = document.getElementById('selectEvent').value;
        const selectVendor = document.getElementById('selectVendor').value;
        const statusDealing = document.getElementById('statusDealing').value;
        
        // Validasi
        let isValid = true;
        let errorMessage = '';
        
        if (!selectEvent) {
            isValid = false;
            errorMessage = 'Silakan pilih event terlebih dahulu';
        } else if (!selectVendor) {
            isValid = false;
            errorMessage = 'Silakan pilih vendor terlebih dahulu';
        } else if (!hargaDealing || parseFloat(hargaDealing) <= 0) {
            isValid = false;
            errorMessage = 'Harga dealing harus lebih dari 0';
        } else if (!statusDealing) {
            isValid = false;
            errorMessage = 'Silakan pilih status dealing';
        }
        
        if (!isValid) {
            e.preventDefault();
            showNotification(errorMessage, 'error');
            
            // Scroll ke field yang error
            if (!selectEvent) {
                document.getElementById('selectEvent').scrollIntoView({ behavior: 'smooth' });
            } else if (!selectVendor) {
                document.getElementById('selectVendor').scrollIntoView({ behavior: 'smooth' });
            } else if (!hargaDealing) {
                document.getElementById('hargaDealing').scrollIntoView({ behavior: 'smooth' });
            }
        }
    }
    
    // Validasi form edit harga
    function validateEditHargaForm(e) {
        const editHarga = document.getElementById('editHarga').value;
        const editStatus = document.getElementById('editStatus').value;
        
        if (!editHarga || parseFloat(editHarga) <= 0) {
            e.preventDefault();
            showNotification('Harga dealing harus lebih dari 0', 'error');
        } else if (!editStatus) {
            e.preventDefault();
            showNotification('Silakan pilih status dealing', 'error');
        }
    }
    
    // ========== HELPER FUNCTIONS ==========
    
    // Format angka ke Rupiah
    function formatRupiah(angka) {
        if (!angka) return '0';
        return angka.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    }
    
    // Tampilkan notifikasi
    function showNotification(message, type = 'info') {
        // Hapus notifikasi sebelumnya
        const existingNotif = document.querySelector('.custom-notification');
        if (existingNotif) {
            existingNotif.remove();
        }
        
        // Buat notifikasi
        const notif = document.createElement('div');
        notif.className = `custom-notification ${type}`;
        notif.textContent = message;
        
        // Style notifikasi
        notif.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 4px;
            color: white;
            font-weight: bold;
            z-index: 10000;
            animation: slideIn 0.3s ease;
        `;
        
        // Warna berdasarkan type
        const colors = {
            'success': '#4CAF50',
            'error': '#f44336',
            'info': '#2196F3',
            'warning': '#ff9800'
        };
        
        notif.style.backgroundColor = colors[type] || colors.info;
        
        // Tambahkan ke body
        document.body.appendChild(notif);
        
        // Hapus otomatis setelah 3 detik
        setTimeout(() => {
            notif.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (notif.parentNode) {
                    notif.remove();
                }
            }, 300);
        }, 3000);
        
        // Tambahkan CSS animation jika belum ada
        if (!document.querySelector('#notification-styles')) {
            const style = document.createElement('style');
            style.id = 'notification-styles';
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOut {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
                .loading-spinner {
                    display: inline-block;
                    width: 20px;
                    height: 20px;
                    border: 3px solid #f3f3f3;
                    border-top: 3px solid #3498db;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin-right: 10px;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(style);
        }
    }
    
    // Initialize
    console.log('Budgeting Asisten JS loaded dengan data real');
});