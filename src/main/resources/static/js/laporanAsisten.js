// laporanAsisten.js
document.addEventListener('DOMContentLoaded', function() {
    
    // ========== VARIABLES ==========
    const btnPrint = document.getElementById('btnPrint');
    const btnExport = document.getElementById('btnExport');
    const modalEventDetail = document.getElementById('modalEventDetail');
    const closeModal = document.querySelector('.close-modal');
    const modalEventBody = document.getElementById('modalEventBody');
    const modalEventTitle = document.getElementById('modalEventTitle');
    
    // ========== EVENT LISTENERS ==========
    
    // Tombol Print
    if (btnPrint) {
        btnPrint.addEventListener('click', function() {
            printLaporan();
        });
    }
    
    // Tombol Export
    if (btnExport) {
        btnExport.addEventListener('click', function() {
            exportToExcel();
        });
    }
    
    // Close modal
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            modalEventDetail.style.display = 'none';
        });
    }
    
    // Close modal saat klik di luar
    window.addEventListener('click', function(event) {
        if (event.target === modalEventDetail) {
            modalEventDetail.style.display = 'none';
        }
    });
    
    // Setup event listener untuk klik pada baris event
    setupEventRowClickListeners();
    
    // ========== FUNCTIONS ==========
    
    // Setup klik pada baris event untuk lihat detail
    function setupEventRowClickListeners() {
        // Event berlangsung
        document.querySelectorAll('#tabel-event-berjalan tbody tr').forEach(row => {
            if (!row.querySelector('td[colspan]')) { // Skip row kosong
                row.addEventListener('click', function() {
                    const eventId = this.querySelector('td:first-child').textContent;
                    const eventName = this.querySelector('td:nth-child(2)').textContent;
                    showEventDetail(eventId, eventName, 'berlangsung');
                });
            }
        });
        
        // Event tuntas
        document.querySelectorAll('#tabel-riwayat-event tbody tr').forEach(row => {
            if (!row.querySelector('td[colspan]')) { // Skip row kosong
                row.addEventListener('click', function() {
                    const eventId = this.querySelector('td:first-child').textContent;
                    const eventName = this.querySelector('td:nth-child(2)').textContent;
                    showEventDetail(eventId, eventName, 'tuntas');
                });
            }
        });
    }
    
    // Tampilkan detail event
    function showEventDetail(eventId, eventName, type) {
        // Dalam implementasi real, ini akan fetch data detail dari backend
        // Untuk demo, kita tampilkan data dummy
        
        modalEventTitle.textContent = 'Detail Event: ' + eventName;
        
        // Data dummy
        const eventData = {
            id: eventId,
            nama: eventName,
            jenis: type === 'berlangsung' ? 'Wedding' : 'Seminar',
            tanggal: '15 Februari 2025',
            jumlahUndangan: '200',
            klien: 'Andi Pratama',
            status: type === 'berlangsung' ? 'BERLANGSUNG' : 'TUNTAS',
            lokasi: 'Gedung Serbaguna Jakarta',
            budget: 'Rp 50.000.000',
            vendor: ['Vendor Dekorasi Ceria', 'Vendor Makanan Enak'],
            catatan: 'Event berjalan sesuai rencana'
        };
        
        // Render detail event
        modalEventBody.innerHTML = `
            <div class="event-detail">
                <div class="detail-row">
                    <span class="detail-label">ID Event:</span>
                    <span class="detail-value">${eventData.id}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Nama Event:</span>
                    <span class="detail-value">${eventData.nama}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Jenis Event:</span>
                    <span class="detail-value">${eventData.jenis}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Tanggal:</span>
                    <span class="detail-value">${eventData.tanggal}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Jumlah Undangan:</span>
                    <span class="detail-value">${eventData.jumlahUndangan} orang</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Klien:</span>
                    <span class="detail-value">${eventData.klien}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Status:</span>
                    <span class="detail-value status-${eventData.status.toLowerCase()}">${eventData.status}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Lokasi:</span>
                    <span class="detail-value">${eventData.lokasi}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Budget:</span>
                    <span class="detail-value">${eventData.budget}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Vendor Terlibat:</span>
                    <span class="detail-value">${eventData.vendor.join(', ')}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Catatan:</span>
                    <span class="detail-value">${eventData.catatan}</span>
                </div>
            </div>
        `;
        
        modalEventDetail.style.display = 'block';
    }
    
    // Fungsi Print Laporan
    function printLaporan() {
        // Simpan konten asli
        const originalContent = document.body.innerHTML;
        
        // Ambil konten yang akan dicetak
        const printContent = document.querySelector('.container').innerHTML;
        
        // Buat halaman khusus untuk print
        document.body.innerHTML = `
            <!DOCTYPE html>
            <html>
            <head>
                <title>Laporan Asisten - Selaras Organizer</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    h1 { color: #333; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
                    h2 { color: #555; margin-top: 30px; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                    th { background-color: #f2f2f2; }
                    .summary-stats { display: flex; justify-content: space-between; margin-bottom: 30px; }
                    .stat-card { text-align: center; padding: 15px; border: 1px solid #ddd; border-radius: 8px; flex: 1; margin: 0 10px; }
                    .stat-number { font-size: 24px; font-weight: bold; color: #4CAF50; }
                    .status-berlangsung { background-color: #fff3cd; color: #856404; padding: 4px 8px; border-radius: 12px; }
                    .status-tuntas { background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 12px; }
                    .print-header { text-align: center; margin-bottom: 30px; }
                    .print-date { text-align: right; margin-bottom: 20px; }
                    @media print {
                        .no-print { display: none; }
                        .action-buttons { display: none; }
                    }
                </style>
            </head>
            <body>
                <div class="print-header">
                    <h1>Laporan Asisten Event Organizer</h1>
                    <p>Selaras Organizer - ${new Date().toLocaleDateString('id-ID', { 
                        weekday: 'long', 
                        year: 'numeric', 
                        month: 'long', 
                        day: 'numeric' 
                    })}</p>
                </div>
                ${printContent}
                <div class="print-date">
                    <p>Dicetak pada: ${new Date().toLocaleString('id-ID')}</p>
                </div>
                <script>
                    window.onload = function() {
                        window.print();
                        setTimeout(function() {
                            window.history.back();
                        }, 100);
                    };
                <\/script>
            </body>
            </html>
        `;
        
        // Trigger print
        window.print();
        
        // Kembali ke halaman asli
        document.body.innerHTML = originalContent;
        location.reload();
    }
    
    // Fungsi Export ke Excel
    function exportToExcel() {
        // Buat data untuk export
        const data = [];
        
        // Header
        data.push(['LAPORAN ASISTEN EVENT ORGANIZER']);
        data.push(['Selaras Organizer']);
        data.push(['Tanggal: ' + new Date().toLocaleDateString('id-ID')]);
        data.push([]);
        
        // Event Berlangsung
        data.push(['EVENT YANG SEDANG BERJALAN']);
        data.push(['ID', 'Nama Event', 'Jenis', 'Tanggal', 'Jumlah Undangan', 'Klien', 'Status']);
        
        // Data event berlangsung
        document.querySelectorAll('#tabel-event-berjalan tbody tr').forEach(row => {
            if (!row.querySelector('td[colspan]')) {
                const cells = row.querySelectorAll('td');
                const rowData = [];
                cells.forEach(cell => {
                    rowData.push(cell.textContent.trim());
                });
                data.push(rowData);
            }
        });
        
        data.push([]);
        
        // Event Tuntas
        data.push(['RIWAYAT EVENT TUNTAS']);
        data.push(['ID', 'Nama Event', 'Jenis', 'Tanggal', 'Jumlah Undangan', 'Klien', 'Status']);
        
        // Data event tuntas
        document.querySelectorAll('#tabel-riwayat-event tbody tr').forEach(row => {
            if (!row.querySelector('td[colspan]')) {
                const cells = row.querySelectorAll('td');
                const rowData = [];
                cells.forEach(cell => {
                    rowData.push(cell.textContent.trim());
                });
                data.push(rowData);
            }
        });
        
        // Buat worksheet
        const ws = XLSX.utils.aoa_to_sheet(data);
        
        // Style untuk header
        const wscols = [
            {wch: 10}, // ID
            {wch: 30}, // Nama Event
            {wch: 15}, // Jenis
            {wch: 20}, // Tanggal
            {wch: 15}, // Jumlah Undangan
            {wch: 20}, // Klien
            {wch: 15}  // Status
        ];
        ws['!cols'] = wscols;
        
        // Buat workbook
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Laporan Asisten");
        
        // Export ke file
        XLSX.writeFile(wb, `Laporan_Asisten_${new Date().toISOString().slice(0,10)}.xlsx`);
        
        // Tampilkan notifikasi
        showNotification('Laporan berhasil diexport ke Excel', 'success');
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
        `;
        
        // Warna berdasarkan type
        if (type === 'success') {
            notif.style.backgroundColor = '#4CAF50';
        } else if (type === 'error') {
            notif.style.backgroundColor = '#f44336';
        } else {
            notif.style.backgroundColor = '#2196F3';
        }
        
        // Tambahkan ke body
        document.body.appendChild(notif);
        
        // Hapus otomatis setelah 3 detik
        setTimeout(() => {
            if (notif.parentNode) {
                notif.remove();
            }
        }, 3000);
    }
    
    // Initialize
    console.log('Laporan Asisten JS loaded');
});