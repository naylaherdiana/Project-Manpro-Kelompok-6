-- Hapus tabel jika sudah ada (opsional)
DROP TABLE IF EXISTS Menangani;
DROP TABLE IF EXISTS Vendor;
DROP TABLE IF EXISTS JenisVendor;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Klien;
DROP TABLE IF EXISTS Asisten;

-- Buat tabel Asisten
CREATE TABLE Asisten (
    idasisten SERIAL PRIMARY KEY,
    namaasisten VARCHAR(255),
    passwordasisten VARCHAR(255),
    alamatasisten VARCHAR(255),
    kontakasisten VARCHAR(255),
    emailasisten VARCHAR(255)
);

-- Buat tabel Klien
CREATE TABLE Klien (
    idklien SERIAL PRIMARY KEY,
    namaklien VARCHAR(255),
    alamatklien VARCHAR(255),
    kontakklien VARCHAR(255)
);

-- Buat tabel JenisVendor
CREATE TABLE JenisVendor (
    idjenisvendor SERIAL PRIMARY KEY,
    kisaranhargamin NUMERIC(15,2),
    kisaranhargamax NUMERIC(15,2),
    namajenisvendor VARCHAR(255)
);

-- Buat tabel Vendor
CREATE TABLE Vendor (
    idvendor SERIAL PRIMARY KEY,
    namapemilik VARCHAR(255),
    namavendor VARCHAR(255),
    alamatvendor VARCHAR(255),
    kontakvendor VARCHAR(255),
    idjenisvendor INTEGER REFERENCES JenisVendor(idjenisvendor)
);

-- Buat tabel Event
CREATE TABLE Event (
    idevent SERIAL PRIMARY KEY,
    namaevent VARCHAR(255),
    jenisevent VARCHAR(255),
    tanggal DATE,
    jumlahundangan INTEGER,
    statusevent VARCHAR(255),
    idklien INTEGER REFERENCES Klien(idklien),
    idasisten INTEGER REFERENCES Asisten(idasisten)
);

-- Buat tabel Menangani
CREATE TABLE Menangani (
    idasisten INTEGER REFERENCES Asisten(idasisten),
    idevent INTEGER REFERENCES Event(idevent),
    idvendor INTEGER REFERENCES Vendor(idvendor),
    hargadealing NUMERIC(15,2),
    statusdealing VARCHAR(255),
    PRIMARY KEY (idasisten, idevent, idvendor)
);


--Masukkan tiap data

-- Insert data Asisten
INSERT INTO Asisten (namaasisten, passwordasisten, alamatasisten, kontakasisten, emailasisten) VALUES
('Rina Maharani', 'Asisten1', 'Jl. Merdeka No.10', '081234567890', 'rina@email.com'),
('Budi Santoso', 'Asisten2', 'Jl. Sudirman No.20', '082345678901', 'budi@email.com'),
('Citra Lestari', 'Asisten3', 'Jl. Diponegoro No.5', '083456789012', 'citra@email.com'),
('Dedi Wijaya', 'Asisten4', 'Jl. Gajah Mada No.7', '084567890123', 'dedi@email.com'),
('Eka Prasetyo', 'Asisten5', 'Jl. Soekarno Hatta No.9', '085678901234', 'eka@email.com');

-- Insert data Klien
INSERT INTO Klien (namaklien, alamatklien, kontakklien) VALUES
('Andi Pratama', 'Jl. Melati No.15', '081212121212'),
('Siti Aminah', 'Jl. Anggrek No.5', '081313131313'),
('Bayu Rahman', 'Jl. Dahlia No.18', '081414141414'),
('Lina Rosdiana', 'Jl. Mawar No.21', '081515151515'),
('Joko Saputra', 'Jl. Flamboyan No.25', '081616161616');

-- Insert data JenisVendor
INSERT INTO JenisVendor (idjenisvendor, kisaranhargamin, kisaranhargamax, namajenisvendor) VALUES
(301, 3000000, 8000000, 'Dekorasi'),
(302, 5000000, 15000000, 'Katering'),
(303, 2000000, 6000000, 'Musik'),
(304, 4000000, 10000000, 'Dokumentasi'),
(305, 3500000, 9000000, 'Panggung');

-- Insert data Vendor
INSERT INTO Vendor (idvendor, namapemilik, namavendor, alamatvendor, kontakvendor, idjenisvendor) VALUES
(201, 'Anton Wijaya', 'Vendor Dekorasi Ceria', 'Jl. Kenanga No.12', '081717171717', 301),
(202, 'Maya Lestari', 'Vendor Makanan Enak', 'Jl. Cemara No.21', '081818181818', 302),
(203, 'Rafi Gunawan', 'Vendor Musik Merdu', 'Jl. Angsana No.33', '081919191919', 303),
(204, 'Tari Melati', 'Vendor Dokumentasi Hebat', 'Jl. Akasia No.44', '082020202020', 304),
(205, 'Yoga Prasetya', 'Vendor Panggung Megah', 'Jl. Teratai No.55', '082121212121', 305),
(206, 'Gita Gutawa', 'Vendor Orkestra', 'Jl. Sudirman No.28', '08124401030', 303),
(207, 'Joko Anwar', 'Vendor SinemaTografi', 'Jl. Lebak Bulus No. 123', '1858111930', 304);

-- Insert data Event
INSERT INTO Event (idevent, namaevent, jenisevent, tanggal, jumlahundangan, statusevent, idklien, idasisten) VALUES
(101, 'Pernikahan Andi & Dinda', 'Pernikahan', '2025-02-15', 200, 'TUNTAS', 1, 1),
(102, 'Ulang Tahun Siti', 'Ulang Tahun', '2025-07-01', 50, 'BERLANGSUNG', 2, 2),
(103, 'Seminar Bayu', 'Seminar', '2025-08-10', 100, 'BERLANGSUNG', 3, 3),
(104, 'Gathering Kantor Lina', 'Gathering', '2025-09-12', 150, 'TUNTAS', 4, 4),
(105, 'Pernikahan Joko & Rina', 'Pernikahan', '2025-10-05', 300, 'BERLANGSUNG', 5, 5), 
(106, 'Ulang Tahun Kakek', 'Ulang Tahun', '2025-01-12', 30, 'TUNTAS', 4, 2), 
(107, 'Pernikahan Andi dan Citra', 'Pernikahan', '2026-01-01', 200, 'BERLANGSUNG', 3, 4);

-- Insert data Menangani
INSERT INTO Menangani (idasisten, idevent, idvendor, hargadealing, statusdealing) VALUES
(1, 101, 201, 6000000, 'Disetujui'),
(1, 101, 202, 12000000, 'Disetujui'),
(2, 102, 203, 3500000, 'Negosiasi'),
(3, 103, 201, 5000000, 'Disetujui'),
(3, 103, 204, 7000000, 'Disetujui'),
(4, 104, 205, 8000000, 'Disetujui'),
(4, 104, 203, 4000000, 'Disetujui'),
(5, 105, 201, 6500000, 'Negosiasi'),
(5, 105, 202, 14000000, 'Disetujui'),
(5, 105, 204, 9000000, 'Negosiasi'),
(2, 106, 202, 6000000, 'Disetujui'),
(2, 106, 201, 4000000, 'Negosiasi'),
(4, 107, 203, 5000000, 'Disetujui');