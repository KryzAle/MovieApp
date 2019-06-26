
package Model;


public class Socio {
    private int id;
    private String nombre,correo,telefono,cedula,direccion;

    public Socio() {
    }

    public Socio(int id, String nombre, String correo, String telefono, String cedula, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.cedula = cedula;
        this.direccion = direccion;
    }

    public Socio(String nombre, String correo, String telefono, String cedula, String direccion) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.cedula = cedula;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    
}
