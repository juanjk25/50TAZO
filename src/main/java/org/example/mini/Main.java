package org.example.mini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mini.controller.GameController;
import org.example.mini.model.game.Game;
import org.example.mini.view.StartView;

public class Main extends Application {

    // Cambia este valor a 'false' para probar directamente el juego
    private static final boolean SHOW_START_MENU = true;
    private static final int TEST_CPU_COUNT = 2; // 1, 2 o 3 CPUs para pruebas

    @Override
    public void start(Stage primaryStage) {
        if (SHOW_START_MENU) {
            // Modo normal: mostrar menú de inicio
            StartView startView = new StartView(primaryStage);
            startView.show();
        } else {
            // Modo prueba: iniciar directamente el juego
            startGameDirectly(primaryStage, TEST_CPU_COUNT);
        }
    }

    /**
     * Inicia el juego directamente sin pasar por el menú de inicio
     * Útil para pruebas rápidas durante el desarrollo
     */
    private void startGameDirectly(Stage stage, int cpuCount) {
        try {
            // Cargar el FXML del juego
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
            Parent root = loader.load();


            // Obtener el controlador y configurar el juego
            GameController gameController = loader.getController();

            // Crear e inicializar el juego
            Game game = new Game(cpuCount);
            game.start(); // Reparte cartas e inicia mesa

            // Inicializar el controlador con el juego
            gameController.init(game);

            Scene scene = new Scene(root, 400, 500);
            scene.getStylesheets().add(getClass().getResource("org/example/mini/view/css/game-style.css").toExternalForm());

            // Configurar y mostrar la ventana
            stage.setTitle("Cincuentazo - Prueba con " + cpuCount + " CPUs");
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

            System.out.println("=== MODO PRUEBA ACTIVADO ===");
            System.out.println("Jugadores: 1 humano + " + cpuCount + " CPUs");
            System.out.println("Suma inicial en mesa: " + game.getTable().getTableSum());
            System.out.println("Cartas en mazo: " + game.getDeck().size());
            System.out.println("=============================");

        } catch (Exception e) {
            System.err.println("Error al iniciar el juego directamente:");
            e.printStackTrace();

            // Fallback: mostrar menú de inicio si hay error
            StartView startView = new StartView(stage);
            startView.show();
        }
    }

    public static void main(String[] args) {
        // Mostrar configuración de prueba al iniciar
        System.out.println("Iniciando Cincuentazo en modo prueba:");
        System.out.println("SHOW_START_MENU: " + SHOW_START_MENU);
        System.out.println("TEST_CPU_COUNT: " + TEST_CPU_COUNT);

        launch(args);
    }
}