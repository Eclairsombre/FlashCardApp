package flashcardapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Screen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import flashcardapp.Card;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
        Button btn = new Button("Show Data");
        Button btn2 = new Button("Add Data");
        ListView<Card> listView = new ListView<>();
        btn.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #ffffff;");
        btn2.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #ffffff;");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(btn, btn2);
        
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:/home/alex/Bureau/FlashCardApp/app/src/main/resources/flashcard", "sa", "");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS flashcards (question VARCHAR(255), answer VARCHAR(255))");
            statement.execute("DELETE FROM flashcards");
            statement.execute("INSERT INTO flashcards (question, answer) VALUES ('What is the capital of France?', 'Paris')");
            statement.execute("INSERT INTO flashcards (question, answer) VALUES ('What is the capital of Spain?', 'Madrid')");
            statement.execute("INSERT INTO flashcards (question, answer) VALUES ('What is the capital of Italy?', 'Rome')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btn.setOnAction(e -> {
            try {
                // Connect to the H2 database

                // Clear the existing data in the database

                // Retrieve data from the database
                listView.getItems().clear();
                Connection connection = DriverManager.getConnection("jdbc:h2:/home/alex/Bureau/FlashCardApp/app/src/main/resources/flashcard", "sa", "");
                Statement statement = connection.createStatement();
                // Récupérer les données de la base de données
                
                ResultSet resultSet = statement.executeQuery("SELECT * FROM flashcards"); // Use the statement here
                

                // Afficher les données dans une fenêtre graphique
                while (resultSet != null && resultSet.next()) {
                    String question = resultSet.getString("question");
                    String answer = resultSet.getString("answer");
                    Card card = new Card(question, answer);
                    listView.getItems().add(card);
                }

                // Close the resourcesœ
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        btn2.setOnAction(e -> {
            // Create a popup
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Add New Card");
            alert.initStyle(StageStyle.UTILITY); // Optional : définir le style de la fenêtre comme une fenêtre utilitaire
            alert.setResizable(true); // Permettre à la fenêtre d'être redimensionnable
        
            // Définir la taille minimale de la fenêtre
            alert.getDialogPane().setMinWidth(500);
            alert.getDialogPane().setMinHeight(500);
        
            // Create a custom content for the popup
            VBox vbox = new VBox();
            Label questionLabel = new Label("Question:");
            TextField questionTextField = new TextField();
            Label answerLabel = new Label("Answer:");
            TextField answerTextField = new TextField();
            Button addButton = new Button("Add");
        
            // Create a close button
            Button closeButton = new Button("Close");
            closeButton.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Stage stage = (Stage) closeButton.getScene().getWindow();
                    stage.close();
                }
            });
        
            vbox.getChildren().addAll(questionLabel, questionTextField, answerLabel, answerTextField, addButton, closeButton);
        
            addButton.setOnAction(event -> {
                String question = questionTextField.getText();
                String answer = answerTextField.getText();
                try {
                    if(!question.isEmpty() || !answer.isEmpty()) {
                        Connection connection = DriverManager.getConnection("jdbc:h2:/home/alex/Bureau/FlashCardApp/app/src/main/resources/flashcard", "sa", "");
                        Statement statement = connection.createStatement();
                        statement.execute("INSERT INTO flashcards (question, answer) VALUES ('" + question + "', '" + answer + "')");
                        Card card = new Card(question, answer);
                        listView.getItems().add(card);                   
                     }
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("A SQLException occurred!");
                }
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.close();
            });
        
            // Set the content of the popup
            alert.getDialogPane().setContent(vbox);
        
            // Show the popup
            alert.showAndWait();
        });
        

        VBox root = new VBox();
        root.getChildren().addAll(hbox, listView);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Afficher les informations de la nouvelle carte sélectionnée
                Label selectedCardLabel = new Label();

                TextField questionTextField = new TextField(listView.getSelectionModel().getSelectedItem().getQuestion());
                TextField answerTextField = new TextField(listView.getSelectionModel().getSelectedItem().getAnswer());
                selectedCardLabel.setText("Question: " + listView.getSelectionModel().getSelectedItem().getQuestion() + ", Answer: " + listView.getSelectionModel().getSelectedItem().getAnswer());

                root.getChildren().removeIf(node -> node instanceof Label);
                root.getChildren().removeIf(node -> node instanceof TextField);
                root.getChildren().removeIf(node -> node instanceof Button);

                root.getChildren().add(selectedCardLabel);
                root.getChildren().add(questionTextField);
                root.getChildren().add(answerTextField);

               

                Button saveButton = new Button("Save");
                saveButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #ffffff;");
                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #ffffff;");    

                saveButton.setOnAction(event -> {
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:h2:/home/alex/Bureau/FlashCardApp/app/src/main/resources/flashcard", "sa", "");
                        Statement statement = connection.createStatement();
                        statement.execute("UPDATE flashcards SET question = '" + questionTextField.getText() + "', answer = '" + answerTextField.getText() + "' WHERE question = '" + listView.getSelectionModel().getSelectedItem().getQuestion() + "'");
                        listView.getSelectionModel().getSelectedItem().setQuestion(questionTextField.getText());
                        listView.getSelectionModel().getSelectedItem().setAnswer(answerTextField.getText());
                        selectedCardLabel.setText("Question: " + listView.getSelectionModel().getSelectedItem().getQuestion() + ", Answer: " + listView.getSelectionModel().getSelectedItem().getAnswer());

                        root.getChildren().removeIf(node -> node instanceof Label);
                        root.getChildren().removeIf(node -> node instanceof TextField);
                        root.getChildren().removeIf(node -> node instanceof Button);

                        listView.refresh();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                deleteButton.setOnAction(event -> {
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:h2:/home/alex/Bureau/FlashCardApp/app/src/main/resources/flashcard", "sa", "");
                        Statement statement = connection.createStatement();
                        statement.execute("DELETE FROM flashcards WHERE question = '" + listView.getSelectionModel().getSelectedItem().getQuestion() + "'");
                        listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
                        root.getChildren().remove(selectedCardLabel);
                        root.getChildren().remove(questionTextField);
                        root.getChildren().remove(answerTextField);
                        root.getChildren().remove(saveButton);
                        root.getChildren().remove(deleteButton);

                      
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                root.getChildren().add(saveButton);
                root.getChildren().add(deleteButton);
            }
        });

       


     


        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setTitle("Flashcards");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}