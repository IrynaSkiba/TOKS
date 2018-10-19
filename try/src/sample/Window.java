package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Window {
    private Controller controller;
    private Button buttonSend;
    private Button buttonConnect;
    private Button buttonDisconnect;
    private TextField textField;
    private ChoiceBox choiceBoxPort;
    private ChoiceBox choiceBoxSpeed;
    ObservableList<String> itemsList = FXCollections.observableArrayList ();

    public void setController(Controller controller){ this.controller = controller;}

    Window(){
        Stage stage = new Stage();
        Scene scene = new Scene(fillScene());
        buildButtons();
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private GridPane fillScene() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(15, 15, 15, 15));

        ListView<String> listView = new ListView<>();
        listView.setPrefSize(300,250);
        listView.setItems(itemsList);
        gridPane.add(listView,0,0,3,4);

        textField = new TextField();
        gridPane.add(textField,0,4,3,1);
        Label labelPort = new Label("Choice port");
        gridPane.add(labelPort,3,0);

        choiceBoxPort = new ChoiceBox(FXCollections.observableArrayList (
                "COM1", "COM2","COM3","COM4","COM5","COM6","COM7","COM8"));
        gridPane.add(choiceBoxPort,4,0);

        Label labelSpeed = new Label("Choice speed");
        gridPane.add(labelSpeed,3,1);

        choiceBoxSpeed = new ChoiceBox(FXCollections.observableArrayList (
                "300", "600","1200","2400","4800","9600","19200","38400","57600","115200"));
        gridPane.add(choiceBoxSpeed,4,1);

        buttonConnect = new Button("Connect");
        gridPane.add(buttonConnect,3,2);

        buttonDisconnect = new Button("Disconnect");
        gridPane.add(buttonDisconnect,4,2);

        buttonSend = new Button("Send");
        gridPane.add(buttonSend,3,4);
        return gridPane;
    }

    private void buildButtons(){
        buttonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!textField.getText().isEmpty()) {
                    controller.sendMessage(textField.getText());

                    textField.clear();
                }
            }
        });
        buttonConnect.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                controller.connect(choiceBoxPort.getValue().toString(),choiceBoxSpeed.getValue().toString());
            }
        });
        buttonDisconnect.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                controller.disconnect();
            }
        });
    }

}
