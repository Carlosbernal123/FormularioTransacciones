import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/TransaccionesDB";
    private static final String USER = "root"; // Cambiar si tienes otro usuario
    private static final String PASSWORD = "Miperro123"; // Si tienes contraseña, agrégala aquí

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}