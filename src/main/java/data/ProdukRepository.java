package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import db.Database;
import model.Produk;

public class ProdukRepository {

    public void add(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (nama, harga, foto) VALUES (?, ?, ?)";
        try ( 
            Connection koneksi = Database.bukaKoneksi();  
            PreparedStatement perintah = koneksi.prepareStatement(sql)) {
            perintah.setString(1, produk.getNama());
            perintah.setLong(2, produk.getHarga());
            perintah.setString(3, produk.getFoto());
            perintah.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
    String sql = "DELETE FROM produk WHERE id = ?";
    try (
        Connection koneksi = Database.bukaKoneksi();
        PreparedStatement perintah = koneksi.prepareStatement(sql)) {
        perintah.setInt(1, id);
        perintah.executeUpdate();
        }
    }
    
   public void update(Produk produk) throws SQLException {
    String sql = "UPDATE produk SET nama = ?, harga = ?, foto = ? WHERE id = ?";
    try (
        Connection koneksi = Database.bukaKoneksi();
        PreparedStatement perintah = koneksi.prepareStatement(sql)) {
        
        perintah.setString(1, produk.getNama());
        perintah.setLong(2, produk.getHarga());
        perintah.setString(3, produk.getFoto());
        perintah.setInt(4, (int) produk.getId()); 
        
        int rowsUpdated = perintah.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Update berhasil di database untuk ID: " + produk.getId());
        } else {
            System.out.println("Gagal update. ID tidak ditemukan di database.");
        }
    }
}

    public ArrayList<Produk> getAll() throws SQLException {
        ArrayList<Produk> daftarProduk = new ArrayList<Produk>();
        String sql = "SELECT id, nama, harga, foto FROM produk ORDER BY id";
        try ( 
            Connection koneksi = Database.bukaKoneksi();  
            PreparedStatement perintah = koneksi.prepareStatement(sql);  
            ResultSet hasil = perintah.executeQuery()) {
            while (hasil.next()) {
                long id = hasil.getLong("id");
                String nama = hasil.getString("nama");
                long harga = hasil.getLong("harga");
                String foto = hasil.getString("foto");
                Produk produk = new Produk(id, nama, harga, foto);
                daftarProduk.add(produk);
            }
        }
        return daftarProduk;
    }
}
