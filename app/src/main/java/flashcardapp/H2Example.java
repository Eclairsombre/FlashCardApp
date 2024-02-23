package flashcardapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Example {
    public static void main(String[] args) {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");

            // Connexion à la base de données en mode fichier
            Connection connection = DriverManager.getConnection("jdbc:h2:file:./test.db", "username", "password");

            // Création de la table
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS example (id INT PRIMARY KEY, name VARCHAR)");

            // Insertion de données
            statement.execute("INSERT INTO example (id, name) VALUES (1, 'John')");
            statement.execute("INSERT INTO example (id, name) VALUES (2, 'Alice')");
            statement.execute("INSERT INTO example (id, name) VALUES (3, 'Bob')");

            // Sélection de données
            ResultSet resultSet = statement.executeQuery("SELECT * FROM example");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }

            // Fermer les ressources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
