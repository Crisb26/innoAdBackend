import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CrearBaseDatos {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String usuario = "postgres";
        String contrasena = "Cris930226**";

        try {
            System.out.println("Conectando a PostgreSQL...");
            Connection conn = DriverManager.getConnection(url, usuario, contrasena);
            Statement stmt = conn.createStatement();

            System.out.println("Creando base de datos innoad_db...");
            stmt.executeUpdate("DROP DATABASE IF EXISTS innoad_db");
            stmt.executeUpdate("CREATE DATABASE innoad_db WITH OWNER = postgres ENCODING = 'UTF8'");

            System.out.println("Base de datos innoad_db creada exitosamente!");

            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPosibles causas:");
            System.err.println("1. PostgreSQL no esta instalado");
            System.err.println("2. PostgreSQL no esta ejecutandose");
            System.err.println("3. La contraseña es incorrecta");
            System.err.println("4. El puerto 5432 no esta disponible");
            e.printStackTrace();
        }
    }
}
