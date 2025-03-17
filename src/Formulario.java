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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Permitir cerrar ventanas individualmente
        setLayout(new FlowLayout());

        // Menú para abrir nuevas ventanas
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opciones");
        JMenuItem nuevaVentana = new JMenuItem("Abrir Nueva Ventana");
        nuevaVentana.addActionListener(e -> new Formulario().setVisible(true));
        menu.add(nuevaVentana);
        menuBar.add(menu);
        setJMenuBar(menuBar);

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
        
        // Agregar un ActionListener para cambiar el nivel de aislamiento en tiempo real
    cbAislamiento.addActionListener(e -> {
    String nivelAislamiento = (String) cbAislamiento.getSelectedItem();
    try {
        if (conn != null) { // Verificar que la conexión no sea nula
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
            JOptionPane.showMessageDialog(this, "Nivel de aislamiento cambiado a: " + nivelAislamiento);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cambiar nivel de aislamiento: " + ex.getMessage());
    }
    });
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
        JButton btnNuevaVentana = new JButton("Nueva Ventana");
        btnNuevaVentana.addActionListener(e -> new Formulario().setVisible(true));
        add(btnNuevaVentana);

        // Conectar a BD
        conn = ConexionBD.conectar();

        try {
            conn.setAutoCommit(false); // Deshabilitar auto-commit para transacciones
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            stmt.close();

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
        if (conn != null) {
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Transacción revertida correctamente.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al hacer rollback: " + e.getMessage());
    }
}
    // Método para mostrar clientes
    private void mostrarClientes() {
    txtResultados.setText(""); // Limpiar área de resultados

    try {
        // Aplicar el nivel de aislamiento actual antes de ejecutar la consulta
        String nivelAislamiento = (String) cbAislamiento.getSelectedItem();
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

        // Ejecutar la consulta después de aplicar el nivel de aislamiento
        String query = "SELECT * FROM clientes";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append("ID: ").append(rs.getInt("id"))
              .append(", Nombre: ").append(rs.getString("nombre"))
              .append(", Departamento: ").append(rs.getString("departamento"))
              .append(", Teléfono: ").append(rs.getString("telefono"))
              .append(", Dirección: ").append(rs.getString("direccion"))
              .append("\n");
        }

        txtResultados.setText(sb.toString());
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Formulario().setVisible(true));
}
}
