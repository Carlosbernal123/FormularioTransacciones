import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Formulario extends JFrame {
    private JTextField txtNombre, txtDepartamento, txtTelefono, txtDireccion;
    private JComboBox<String> cbAislamiento;
    private JTextArea txtResultados;
    private Connection conn;
    
    public Formulario() {
        setTitle("Gestión de Clientes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel lblNombre = new JLabel("Nombre:");
        txtNombre = new JTextField(15);
        JLabel lblDepartamento = new JLabel("Departamento:");
        txtDepartamento = new JTextField(15);
        JLabel lblTelefono = new JLabel("Telefono:");
        txtTelefono = new JTextField(10);
        JLabel lblDireccion = new JLabel("Dirección:");
        txtDireccion = new JTextField(20);

        JLabel lblAislamiento = new JLabel("Niveles de Aislamiento:");
        cbAislamiento = new JComboBox<>(new String[]{"Read Uncommitted", "Read Committed", "Repeatable Read", "Serializable"});

        JButton btnGuardar = new JButton("Guardar");
        JButton btnMostrar = new JButton("Mostrar");
        JButton btnCommit = new JButton("Commit");
        JButton btnRollback = new JButton("Rollback");

        txtResultados = new JTextArea(10, 50);
        txtResultados.setEditable(false);

        // Agregar componentes
        add(lblNombre); add(txtNombre);
        add(lblDepartamento); add(txtDepartamento);
        add(lblTelefono); add(txtTelefono);
        add(lblDireccion); add(txtDireccion);
        add(lblAislamiento); add(cbAislamiento);
        add(btnGuardar); add(btnMostrar);
        add(btnCommit); add(btnRollback);
        add(new JScrollPane(txtResultados));

        // Conectar a BD
        conn = ConexionBD.conectar();

        // Eventos
        btnGuardar.addActionListener(e -> insertarCliente());
        btnMostrar.addActionListener(e -> mostrarClientes());
        btnCommit.addActionListener(e -> commitTransaccion());
        btnRollback.addActionListener(e -> rollbackTransaccion());

        setVisible(true);
    }

    // Método para insertar datos
 private void insertarCliente() {
    String nivelAislamiento = cbAislamiento.getSelectedItem().toString();
    try {
        // Ajustar el nivel de aislamiento seleccionado
        switch (nivelAislamiento) {
            case "Read Uncommitted":
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                break;
            case "Read Committed":
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                break;
            case "Repeatable Read":
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                break;
            case "Serializable":
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                break;
        }

        // Preparar la consulta SQL para insertar el cliente
        String sql = "INSERT INTO clientes (nombre, departamento, telefono, direccion) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, txtNombre.getText());
        stmt.setString(2, txtDepartamento.getText());
        stmt.setString(3, txtTelefono.getText());
        stmt.setString(4, txtDireccion.getText());

        // Ejecutar la inserción
        stmt.executeUpdate();
        
        // Mensaje de confirmación
        JOptionPane.showMessageDialog(this, "Cliente guardado con nivel: " + nivelAislamiento);

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Método para hacer COMMIT
    private void commitTransaccion() {
        try {
            conn.commit();
            JOptionPane.showMessageDialog(this, "Transacción confirmada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para hacer ROLLBACK
    private void rollbackTransaccion() {
        try {
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Transacción revertida.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    for (int i = 0; i < 2; i++) { // Abre dos ventanas
        SwingUtilities.invokeLater(() -> new Formulario().setVisible(true));
    }
    }
    
    private void mostrarClientes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
