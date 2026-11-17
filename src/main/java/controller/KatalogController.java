package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import data.ProdukRepository;
import model.Produk;

public class KatalogController {

    @FXML
    private TextField namaField;
    @FXML
    private TextField hargaField;
    @FXML
    private ComboBox<String> fotoBox;
    @FXML
    private Label pesanLabel;
    @FXML
    private Label jumlahLabel;
    @FXML
    private TilePane katalogPane;
    @FXML
    private TextField idField;
    private final ProdukRepository repository = new ProdukRepository();
    private Produk produkYangDiedit = null;

    @FXML
    private void initialize() {
        fotoBox.getItems().addAll("roti.png", "susu.png", "kopi.png");
        fotoBox.setValue("roti.png");
        try {
            showData();
        } catch (SQLException e) {
            pesanLabel.setText("Gagal membaca database. Lihat Output.");
            e.printStackTrace();
        }
    }

    private void showData() throws SQLException {
        ArrayList<Produk> daftar = repository.getAll();
        katalogPane.getChildren().clear();
        for (Produk produk : daftar) {
            VBox kartu = createProductCard(produk);
            katalogPane.getChildren().add(kartu);
        }
        jumlahLabel.setText("Jumlah produk: " + daftar.size());
    }

    private VBox createProductCard(Produk produk) {
        ImageView fotoView = new ImageView();
        fotoView.setFitWidth(170);
        fotoView.setFitHeight(110);
        fotoView.setPreserveRatio(true);
        StackPane bingkai = new StackPane();
        bingkai.setPrefSize(180, 120);
        bingkai.setMinSize(180, 120);
        bingkai.setMaxSize(180, 120);

        URL lokasi = getClass().getResource("/image/" + produk.getFoto());
        if (lokasi != null) {
            Image gambar = new Image(lokasi.toExternalForm(),
                    170, 110, true, true);
            if (!gambar.isError()) {
                fotoView.setImage(gambar);
                bingkai.getChildren().add(fotoView);
            } else {
                bingkai.getChildren().add(new Label("Gambar tidak terbaca"));
            }
        } else {
            bingkai.getChildren().add(new Label("Gambar tidak tersedia"));
        }

        javafx.scene.control.Button btnHapus = new javafx.scene.control.Button("Hapus");
        btnHapus.getStyleClass().add("btn-hapus"); 

        Label namaLabel = new Label(produk.getNama());
        namaLabel.setWrapText(true);
        namaLabel.getStyleClass().add("nama-produk");
        Label hargaLabel = new Label("Rp" + produk.getHarga());
        Label idLabel = new Label("ID: " + produk.getId());
        idLabel.getStyleClass().add("muted");

        btnHapus.setOnAction(event -> {
            try {
                repository.delete((int) produk.getId());
                showData(); 
                pesanLabel.setText("Produk " + produk.getNama() + " berhasil dihapus.");
            } catch (SQLException e) {
                pesanLabel.setText("Gagal menghapus produk.");
                e.printStackTrace();
            }
        });

        javafx.scene.control.Button btnEdit = new javafx.scene.control.Button("Edit");
        btnEdit.getStyleClass().add("btn-edit");
        btnEdit.setOnAction(e -> {
            namaField.setText(produk.getNama());
            hargaField.setText(String.valueOf(produk.getHarga()));
            fotoBox.setValue(produk.getFoto());
            produkYangDiedit = produk;
            pesanLabel.setText("Silakan ubah data, lalu klik tombol Simpan.");
        });

        VBox kartu = new VBox();
        kartu.setSpacing(8);
        kartu.setMinWidth(220);
        kartu.setPrefWidth(220);
        kartu.setMaxWidth(220);
        kartu.getStyleClass().add("kartu-produk");
        kartu.getChildren().add(bingkai);
        kartu.getChildren().add(namaLabel);
        kartu.getChildren().add(hargaLabel);
        kartu.getChildren().add(idLabel);
        kartu.getChildren().add(btnHapus);
        kartu.getChildren().add(btnEdit);
        return kartu;
    }

    @FXML
    private void saveProduct() {
        Produk produk;
        try {
            String nama = namaField.getText();
            long harga = Long.parseLong(hargaField.getText().trim());
            String foto = fotoBox.getValue();
            produk = new Produk(nama, harga, foto);
        } catch (NumberFormatException e) {
            pesanLabel.setText("Harga harus angka bulat, contoh 12000.");
            return;
        } catch (IllegalArgumentException e) {
            pesanLabel.setText(e.getMessage());
            return;
        }

        String pesanSukses = "Produk berhasil disimpan."; 
        try {
            if (produkYangDiedit == null) {
                repository.add(produk);
            } else {
            produk.setId(produkYangDiedit.getId()); 
            System.out.println("Mencoba update produk dengan ID: " + produk.getId()); 
            
            repository.update(produk);
            pesanSukses = "Produk berhasil diperbarui.";
            produkYangDiedit = null; 
            }
        } catch (SQLException e) {
            pesanLabel.setText("Gagal menyimpan. Lihat Output.");
            e.printStackTrace();
            return;
        }

        namaField.clear();
        hargaField.clear();
        try {
            showData();
            pesanLabel.setText(pesanSukses); 
        } catch (SQLException e) {
            pesanLabel.setText("Data tersimpan, tetapi katalog gagal dimuat ulang.");
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteProduct() {
        try {
            int idHapus = Integer.parseInt(idField.getText().trim());
            repository.delete(idHapus);
            idField.clear();
            showData();

            pesanLabel.setText("Produk dengan ID " + idHapus + " berhasil dihapus.");

        } catch (NumberFormatException e) {
            pesanLabel.setText("Format ID salah. Masukkan ID berupa angka.");
        } catch (SQLException e) {
            pesanLabel.setText("Gagal menghapus produk. Lihat Output.");
            e.printStackTrace();
        }
    }
}
