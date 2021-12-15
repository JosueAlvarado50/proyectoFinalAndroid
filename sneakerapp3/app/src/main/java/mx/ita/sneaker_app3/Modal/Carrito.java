package mx.ita.sneaker_app3.Modal;

public class Carrito {

    private String pid, nombre, precio, cantidad, descuento;

    public Carrito (){}

    public Carrito(String pid, String nombre, String precio, String cantidad, String descuento) {
        this.pid = pid;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.descuento = descuento;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
}
