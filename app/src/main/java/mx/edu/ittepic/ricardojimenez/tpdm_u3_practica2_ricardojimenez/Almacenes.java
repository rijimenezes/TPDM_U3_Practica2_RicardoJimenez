package mx.edu.ittepic.ricardojimenez.tpdm_u3_practica2_ricardojimenez;

public class Almacenes {
    public String idAlmacen,descripcion,ubicacion;
    public int capacidad;

    public Almacenes(String idAlmacen, String descripcion, int capacidad, String ubicacion) {
        this.idAlmacen = idAlmacen;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
    }

    public Almacenes() {
    }
}
