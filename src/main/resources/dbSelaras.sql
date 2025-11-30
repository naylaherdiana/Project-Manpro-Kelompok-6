-- Hapus tabel jika sudah ada (opsional)
DROP TABLE IF EXISTS Menangani;
DROP TABLE IF EXISTS Vendor;
DROP TABLE IF EXISTS JenisVendor;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Klien;
DROP TABLE IF EXISTS Asisten;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_roles;

-- Buat tabel Users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,   -- untuk login
    email VARCHAR(255) UNIQUE NOT NULL,     -- untuk komunikasi/notifikasi
    password VARCHAR(255) NOT NULL,         -- hash bcrypt
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Buat tabel Roles
CREATE TABLE user_roles (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('PEMILIK', 'ASISTEN')),
    PRIMARY KEY (user_id, role)
);

-- Buat tabel Asisten
CREATE TABLE Asisten (
    id SERIAL PRIMARY KEY,
    nama VARCHAR(255) NOT NULL,
    alamat VARCHAR(255),
    kontak VARCHAR(255),
    user_id INTEGER UNIQUE REFERENCES users(id) ON DELETE CASCADE
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
    idasisten INTEGER REFERENCES Asisten(id)
);

-- Buat tabel Menangani
CREATE TABLE Menangani (
    idasisten INTEGER REFERENCES Asisten(id),
    idevent INTEGER REFERENCES Event(idevent),
    idvendor INTEGER REFERENCES Vendor(idvendor),
    hargadealing NUMERIC(15,2),
    statusdealing VARCHAR(255),
    PRIMARY KEY (idasisten, idevent, idvendor)
);


--Masukkan tiap data

-- Pemilik
INSERT INTO users (username, email, password) VALUES
('Budi Laraso', 'budi@selarasorganizer.com', '$2a$12$Q6EjAJNk.YW3pNoM.rqGz.dQba.eHkCVWP2zW1acO2LScsafmkKkm');

INSERT INTO user_roles (user_id, role) VALUES (1, 'PEMILIK');

-- Asisten
INSERT INTO users (username, email, password) VALUES
('Abigail Maharani', 'abigail@selarasorganizer.com', '$2a$12$4i0UXJg7j8ml4Y.S8PsO5u671bWBSNAYA7o3DtcQqZu9XxTWmtl52'),
('Max Indrawan', 'max@selarasorganizer.com', '$2a$12$dXjLuUbMWvNKIDFoJkqUBeQ4fP438822WS2cfgslJ2Q9CaxulM9Na'),
('Citra Lestari', 'citra@selarasorganizer.com', '$2a$12$ceKV79igwpYmdRkJIbPLHeE6hapyie.Y/qQim2mbbll3.7GUbz6i2'),
('Dedi Wijaya', 'dedi@selarasorganizer.com', '$2a$12$26KZdj5l3StWtAJa6D36N.8cQaN7pwLBVKgJrPj86WR2n4Tr2Ow3S'),
('Eka Prasetyo', 'eka@selarasorganizer.com', '$2a$12$vSj3.afkRnMrUMDdXUK99ONxUTFkK9.cSTc3nmoSMqsdI1ItJiloe');

INSERT INTO user_roles (user_id, role) VALUES
(2, 'ASISTEN'),
(3, 'ASISTEN'),
(4, 'ASISTEN'),
(5, 'ASISTEN'),
(6, 'ASISTEN');

-- Insert data Asisten
INSERT INTO Asisten (nama, alamat, kontak, user_id) VALUES
('Abigail Shanie Maharani', 'Jl. Merdeka No.10', '081234567890', 2),
('Maximilius Indrawan', 'Jl. Sudirman No.20', '082345678901', 3),
('Citra Lestari', 'Jl. Diponegoro No.5', '083456789012', 4),
('Dedi Eko Wijaya', 'Jl. Gajah Mada No.7', '084567890123', 5),
('Eka Prasetyo Respati', 'Jl. Soekarno Hatta No.9', '085678901234', 6);

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