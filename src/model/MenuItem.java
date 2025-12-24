package model;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private String judul;
    private String contentKey; // Digunakan untuk memanggil panel di CardLayout
    private List<MenuItem> subMenuItems;

    // Konstruktor untuk menu utama yang punya sub-menu
    public MenuItem(String judul) {
        this.judul = judul;
        this.subMenuItems = new ArrayList<>();
    }

    // Konstruktor untuk menu yang langsung membuka panel (punya contentKey)
    public MenuItem(String judul, String contentKey) {
        this.judul = judul;
        this.contentKey = contentKey;
        this.subMenuItems = new ArrayList<>();
    }

    public void addSubMenuItem(MenuItem item) {
        subMenuItems.add(item);
    }

    public boolean hasSubMenuItem() {
        return !subMenuItems.isEmpty();
    }

    // Getter dan Setter
    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public List<MenuItem> getSubMenuItems() {
        return subMenuItems;
    }
}