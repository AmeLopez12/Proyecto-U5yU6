/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Operaciones;

import Modelo.Reporte;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ReporteDao {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    
    public List ListarRM(int mes, int anio){
       List<Reporte> ListaRM = new ArrayList();
       String sql = "SELECT * FROM reporte_ventas_mensual\n" +
                    "WHERE MONTH(STR_TO_DATE(fecha, '%d/%m/%Y')) = ? AND YEAR(STR_TO_DATE(fecha, '%d/%m/%Y')) = ? order by folio;";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, mes);
           ps.setInt(2, anio);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Reporte r = new Reporte();
               r.setFolio(rs.getInt("folio"));
               r.setFecha(rs.getString("fecha"));
               r.setCliente(rs.getString("cliente"));
               r.setEmpleado(rs.getString("empleado"));
               r.setTotal(rs.getString("total"));
               r.setCantidadDetalles(rs.getInt("CANT. DETALLES"));
               ListaRM.add(r);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaRM;
   }
    
   public List ListarRE(int mes, int anio){
       List<Reporte> ListaRE = new ArrayList();
       String sql = "SELECT EMPLEADO, TOTAL, `CANT. VENTAS` FROM reporte_ventas_empleados \n" +
                    "WHERE mes = ? AND anio = ?;";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, mes);
           ps.setInt(2, anio);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Reporte r = new Reporte();
               r.setEmpleado(rs.getString("EMPLEADO"));
               r.setTotal(rs.getString("TOTAL"));
               r.setCantidadVentas(rs.getInt("CANT. VENTAS"));
               ListaRE.add(r);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaRE;
   }
   
   public List ListarRT(int anio){
       List<Reporte> ListaRE = new ArrayList();
       String sql = "SELECT PRODUCTO,`TRIM. 1`,`TRIM. 2`,`TRIM. 3`,`TRIM. 4` FROM reporte_trimestral \n" +
                    "WHERE YEAR(STR_TO_DATE(fecha, '%d/%m/%Y')) = ?;";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, anio);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Reporte r = new Reporte();
               r.setProducto(rs.getString("PRODUCTO"));
               r.setTri1(rs.getString("TRIM. 1"));
               r.setTri2(rs.getString("TRIM. 2"));
               r.setTri3(rs.getString("TRIM. 3"));
               r.setTri4(rs.getString("TRIM. 4"));
               ListaRE.add(r);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaRE;
   }
}
