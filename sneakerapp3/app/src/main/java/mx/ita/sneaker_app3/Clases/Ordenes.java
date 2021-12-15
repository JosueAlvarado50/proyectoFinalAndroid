package mx.ita.sneaker_app3.Clases;

public class Ordenes {
    private String correo, direccion, estado, nombre, fecha, hora, total, telefono;

    public Ordenes(){

    }

    public Ordenes(String correo, String direccion, String estado, String nombre, String fecha, String hora, String total, String telefono) {
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.total = total;
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
