package Operaciones;

import Modelo.Detalle;
import Modelo.Venta;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.filechooser.FileSystemView;

public class VentaDao {

    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;

    public int IdVenta() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }

    public int RegistrarVentaConDetalle(Venta v, List<Detalle> detalles) {
        String sqlVenta = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
        String sqlDetalle = "INSERT INTO detalle (codigo_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        int resultado = 0;
        int idVenta = 0;

        try (Connection con = cn.getConnection()) {
            con.setAutoCommit(false); // Iniciar transacción

            // Registrar la venta
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, v.getCliente());
                psVenta.setInt(2, v.getVendedor());
                psVenta.setDouble(3, v.getTotal());

                // Convertir la fecha al formato adecuado
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormat.parse(v.getFecha());
                String formattedDate = outputFormat.format(date);

                psVenta.setString(4, formattedDate);

                int filasAfectadas = psVenta.executeUpdate();

                // Verificar si se registró la venta y obtener el ID generado
                if (filasAfectadas > 0) {
                    try (ResultSet rsVenta = psVenta.getGeneratedKeys()) {
                        if (rsVenta.next()) {
                            idVenta = rsVenta.getInt(1);
                            v.setId(idVenta);
                        } else {
                            throw new SQLException("No se generó un ID de venta.");
                        }
                    }
                } else {
                    throw new SQLException("Error al insertar la venta.");
                }
            }
            // Registrar los detalles de la venta
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                for (Detalle detalle : detalles) {
                    psDetalle.setInt(1, detalle.getCodigoPro());
                    psDetalle.setInt(2, detalle.getCantidad());
                    psDetalle.setDouble(3, detalle.getPrecio());
                    psDetalle.setInt(4, idVenta);
                    psDetalle.addBatch(); // Añadir a un lote para mayor eficiencia
                    // Actualizar el stock del producto
                    actualizarStockProducto(detalle.getCodigoPro(), detalle.getCantidad());
                }
                psDetalle.executeBatch(); // Ejecutar todos los inserts del lote
            }
            // Confirmar la transacción
            con.commit();
            resultado = 1; // Operación exitosa
        } catch (Exception e) {
            // Manejar cualquier error y realizar rollback
            try {
                if (cn.getConnection() != null) {
                    cn.getConnection().rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }
            System.out.println("Error al registrar la venta con detalle: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }

    public void actualizarStockProducto(int codigoPro, int cantidadVendida) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cantidadVendida);
            ps.setInt(2, codigoPro);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar el stock: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión después de actualizar stock: " + e.toString());
            }
        }
    }

    public boolean ActualizarStock(int cant, int id) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public List Listarventas() {
        List<Venta> ListaVenta = new ArrayList();
        String sql = "SELECT c.id AS id_cli, c.nombre AS cliente, u.nombre, v.*FROM clientes c INNER JOIN ventas v ON c.id = v.cliente INNER JOIN usuarios u ON v.vendedor = u.id order by v.id";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Venta vent = new Venta();
                vent.setId(rs.getInt("id"));
                vent.setNombre_cli(rs.getString("cliente"));
                vent.setNom_vendedor(rs.getString("nombre"));
                vent.setTotal(rs.getDouble("total"));
                ListaVenta.add(vent);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaVenta;
    }

    public Venta BuscarVenta(int id) {
        Venta cl = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setId(rs.getInt("id"));
                cl.setCliente(rs.getInt("cliente"));
                cl.setTotal(rs.getDouble("total"));
                cl.setVendedor(rs.getInt("vendedor"));
                cl.setFecha(rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }

    public void pdfV(int idventa, int Cliente, double total, String usuario) {
        con = cn.getConnection();
        try {
            // Definir ruta
            String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            File salida = new File(url + "/venta" + idventa + ".pdf");

            // Crear el PDF
            FileOutputStream archivo = new FileOutputStream(salida);
            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            // Obtener la fecha de la venta
            String fechaVenta = "";
            String queryFecha = "SELECT fecha FROM ventas WHERE id = ?";
            try {
                ps = con.prepareStatement(queryFecha);
                ps.setInt(1, idventa);
                rs = ps.executeQuery();
                if (rs.next()) {
                    // Convertir fecha al formato deseado
                    SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat desiredFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                    fechaVenta = desiredFormat.format(dbFormat.parse(rs.getString("fecha")));
                } else {
                    fechaVenta = "Fecha no disponible";
                }
            } catch (Exception e) {
                System.out.println("Error obteniendo la fecha de la venta: " + e.toString());
                fechaVenta = "Error al obtener fecha";
            }

            // Agregar la fecha al documento
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            Paragraph fecha = new Paragraph(fechaVenta, negrita);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            doc.add(fecha);
            doc.add(Chunk.NEWLINE);

            // Encabezado
            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);
            encabezado.getDefaultCell().setBorder(0);
            float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
            encabezado.setWidths(columnWidthsEncabezado);
            encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            encabezado.addCell("");

            // Datos del cliente
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);
            cli.add("DATOS DEL CLIENTE" + "\n\n");
            doc.add(cli);

            PdfPTable proveedor = new PdfPTable(3);
            proveedor.setWidthPercentage(100);
            proveedor.getDefaultCell().setBorder(0);
            float[] columnWidthsCliente = new float[]{50f, 25f, 25f};
            proveedor.setWidths(columnWidthsCliente);
            proveedor.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell cliNom = new PdfPCell(new Phrase("Nombre", negrita));
            PdfPCell cliTel = new PdfPCell(new Phrase("Teléfono", negrita));
            PdfPCell cliDir = new PdfPCell(new Phrase("Dirección", negrita));
            cliNom.setBorder(Rectangle.NO_BORDER);
            cliTel.setBorder(Rectangle.NO_BORDER);
            cliDir.setBorder(Rectangle.NO_BORDER);
            proveedor.addCell(cliNom);
            proveedor.addCell(cliTel);
            proveedor.addCell(cliDir);

            String queryCliente = "SELECT * FROM clientes WHERE id = ?";
            try {
                ps = con.prepareStatement(queryCliente);
                ps.setInt(1, Cliente);
                rs = ps.executeQuery();
                if (rs.next()) {
                    proveedor.addCell(rs.getString("nombre"));
                    proveedor.addCell(rs.getString("telefono"));
                    proveedor.addCell(rs.getString("direccion") + "\n\n");
                } else {
                    proveedor.addCell("Público en General");
                    proveedor.addCell("S/N");
                    proveedor.addCell("S/N" + "\n\n");
                }
            } catch (SQLException e) {
                System.out.println("Error obteniendo datos del cliente: " + e.toString());
            }
            doc.add(proveedor);

            // Tabla de productos
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.getDefaultCell().setBorder(0);
            float[] columnWidths = new float[]{10f, 50f, 15f, 15f};
            tabla.setWidths(columnWidths);
            tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell c2 = new PdfPCell(new Phrase("Descripción.", negrita));
            PdfPCell c3 = new PdfPCell(new Phrase("P. Unit.", negrita));
            PdfPCell c4 = new PdfPCell(new Phrase("P. Total ", negrita));
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c4.setBorder(Rectangle.NO_BORDER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(c1);
            tabla.addCell(c2);
            tabla.addCell(c3);
            tabla.addCell(c4);

            String queryProductos = "SELECT d.id, d.codigo_pro, d.id_venta, d.precio, d.cantidad, p.id, p.nombre "
                    + "FROM detalle d "
                    + "INNER JOIN productos p ON d.codigo_pro = p.id "
                    + "WHERE d.id_venta = ?";
            try {
                ps = con.prepareStatement(queryProductos);
                ps.setInt(1, idventa);
                rs = ps.executeQuery();
                while (rs.next()) {
                    double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                    tabla.addCell(rs.getString("cantidad"));
                    tabla.addCell(rs.getString("nombre"));
                    tabla.addCell("$" + rs.getString("precio"));
                    tabla.addCell("$" + String.valueOf(subTotal));
                }
            } catch (SQLException e) {
                System.out.println("Error obteniendo productos: " + e.toString());
            }
            doc.add(tabla);

            // Llamar a la función cantidad_productos y obtener el total
            int totalProductos = 0;
            String queryCantidadProductos = "SELECT cantidad_productos(?) AS total_productos";
            try {
                ps = con.prepareStatement(queryCantidadProductos);
                ps.setInt(1, idventa);
                rs = ps.executeQuery();
                if (rs.next()) {
                    totalProductos = rs.getInt("total_productos");
                }
            } catch (SQLException e) {
                System.out.println("Error obteniendo el total de productos: " + e.toString());
            }

            // Agregar el total de productos al documento
            Paragraph infoProductos = new Paragraph();
            infoProductos.add(Chunk.NEWLINE);
            infoProductos.add("Total de productos: " + totalProductos + "\n");
            infoProductos.setAlignment(Element.ALIGN_RIGHT);
            doc.add(infoProductos);

            // Información adicional
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            info.add("Total: $" + total);
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);

            doc.close();
            archivo.close();
            Desktop.getDesktop().open(salida);
        } catch (DocumentException | IOException e) {
            System.out.println("Error generando el PDF: " + e.toString());
        }
    }
}
