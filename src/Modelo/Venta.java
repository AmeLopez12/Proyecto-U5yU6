
package Modelo;

public class Venta {
    private int id;
    private int cliente;
    private String nombre_cli;
    private int vendedor;
    private String nom_vendedor;
    private double total;
    private String fecha;
    
    public Venta(){
        
    }

    public Venta(int id, int cliente, String nombre_cli,int vendedor, String nom_vendedor, double total, String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.nombre_cli = nombre_cli;
        this.vendedor = vendedor;
        this.vendedor = vendedor;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCliente() {
        return cliente;
    }

    public void setCliente(int cliente) {
        this.cliente = cliente;
    }

    public String getNombre_cli() {
        return nombre_cli;
    }

    public void setNombre_cli(String nombre_cli) {
        this.nombre_cli = nombre_cli;
    }

    public int getVendedor() {
        return vendedor;
    }

    public void setVendedor(int vendedor) {
        this.vendedor = vendedor;
    }

    public String getNom_vendedor() {
        return nom_vendedor;
    }

    public void setNom_vendedor(String nom_vendedor) {
        this.nom_vendedor = nom_vendedor;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }    
}
