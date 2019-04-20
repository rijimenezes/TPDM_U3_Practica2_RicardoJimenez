package mx.edu.ittepic.ricardojimenez.tpdm_u3_practica2_ricardojimenez;

public class Productos {
    public String sku,descripcion;
    public float precio;
    public int stock;

    public Productos(String sku, String descripcion, float precio, int stock) {
        this.sku = sku;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public Productos() {
    }
}
