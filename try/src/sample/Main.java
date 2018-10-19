package sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage stage) throws Exception {
        Window window = new Window();
        controller = new Controller(window);
        window.setController(controller);
     }

    @Override
    public void stop() throws Exception {
        controller.disconnect();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
