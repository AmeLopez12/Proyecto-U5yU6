/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;


public class Reporte {
    private int folio;
    private String fecha;
    private String cliente;
    private String empleado;
    private String total;
    private int cantidadDetalles;
    private int cantidadVentas;
    private String producto;
    private String tri1;
    private String tri2;
    private String tri3;
    private String tri4;

    public Reporte() {
    }

    public Reporte(int folio, String fecha, String cliente, String empleado, String total, int cantidadDetalles, int cantidadVentas, String producto, String tri1, String tri2, String tri3, String tri4) {
        this.folio = folio;
        this.fecha = fecha;
        this.cliente = cliente;
        this.empleado = empleado;
        this.total = total;
        this.cantidadDetalles = cantidadDetalles;
        this.cantidadVentas = cantidadVentas;
        this.producto = producto;
        this.tri1 = tri1;
        this.tri2 = tri2;
        this.tri3 = tri3;
        this.tri4 = tri4;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getCantidadDetalles() {
        return cantidadDetalles;
    }

    public void setCantidadDetalles(int cantidadDetalles) {
        this.cantidadDetalles = cantidadDetalles;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTri1() {
        return tri1;
    }

    public void setTri1(String tri1) {
        this.tri1 = tri1;
    }

    public String getTri2() {
        return tri2;
    }

    public void setTri2(String tri2) {
        this.tri2 = tri2;
    }

    public String getTri3() {
        return tri3;
    }

    public void setTri3(String tri3) {
        this.tri3 = tri3;
    }

    public String getTri4() {
        return tri4;
    }

    public void setTri4(String tri4) {
        this.tri4 = tri4;
    }
}
