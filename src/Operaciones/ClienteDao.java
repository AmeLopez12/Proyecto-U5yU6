/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Operaciones;

import Modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.sql.CallableStatement;


/**
 *
 * @author USUARIO
 */
public class ClienteDao {
    
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    CallableStatement cs = null;

    
    public boolean RegistrarCliente(Cliente cl) {
    String sql = "{CALL InsertarCliente(?, ?, ?, ?)}";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, cl.getDni());
        ps.setString(2, cl.getNombre());
        ps.setString(3, cl.getTelefono());
        ps.setString(4, cl.getDireccion());
        ps.execute();
        return true;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.toString());
        return false;
    } finally {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
}
    
   public List ListarCliente(){
       List<Cliente> ListaCl = new ArrayList();
       String sql = "SELECT * FROM clientes WHERE eliminado = 0";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Cliente cl = new Cliente();
               cl.setId(rs.getInt("id"));
               cl.setDni(rs.getString("dni"));
               cl.setNombre(rs.getString("nombre"));
               cl.setTelefono(rs.getString("telefono"));
               cl.setDireccion(rs.getString("direccion"));
               ListaCl.add(cl);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaCl;
   }
   
  public boolean EliminarCliente(int id) {
    String sql = "{CALL EliminarClienteProc(?)}"; 
    try {
        con = cn.getConnection();  
        cs = con.prepareCall(sql); 
        cs.setInt(1, id);
        cs.execute();
        return true;
    } catch (SQLException e) {
        System.out.println(e.toString());
        return false;
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}

   
public boolean ModificarCliente(Cliente cl) {
    String sql = "{CALL ModificarClienteProc(?, ?, ?, ?, ?)}";
    try {
        con = cn.getConnection();  
        cs = con.prepareCall(sql); 
        cs.setInt(1, cl.getId());      
        cs.setString(2, cl.getDni()); 
        cs.setString(3, cl.getNombre()); 
        cs.setString(4, cl.getTelefono()); 
        cs.setString(5, cl.getDireccion()); 
        cs.execute();
        return true;
    } catch (SQLException e) {
        System.out.println(e.toString());
        return false;
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}

   
   public Cliente Buscarcliente(int dni){
       Cliente cl = new Cliente();
       String sql = "SELECT * FROM clientes WHERE dni = ? AND eliminado = 0";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, dni);
           rs = ps.executeQuery();
           if (rs.next()) {
               cl.setId(rs.getInt("id"));
               cl.setNombre(rs.getString("nombre"));
               cl.setTelefono(rs.getString("telefono"));
               cl.setDireccion(rs.getString("direccion"));
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return cl;
   }
   
   public boolean ExisteDniCliente(String dni) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean existe = false;

    try(Connection con = cn.getConnection()){
        String sql = "SELECT COUNT(*) FROM clientes WHERE dni = ? AND eliminado = 0";
        ps = con.prepareStatement(sql);
        ps.setString(1, dni);
        rs = ps.executeQuery();

        if (rs.next()) {
            //Si es mayor a 0, el DNI ya existe en la base de datos
            existe = rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar DNI: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + ex.getMessage());
        }
    }

    return existe;
}
}