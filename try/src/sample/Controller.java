package sample;

public class Controller {
    private Window window;
    private PortControl portControl;
    Controller(Window window){
        this.window = window;
        portControl = new PortControl(this);
  }

    public void sendMessage(String text) {
        portControl.sendMessage(text);
        window.itemsList.add("Me:  " + text);
    }

    public void receiveMessage(String data){
        window.itemsList.add("  You:  "+ data);
    }

    public void connect(String port, String speed) {
        portControl.setPortName(port);
        portControl.setSpeedInBod(speed);
        portControl.connect();
    }

    public void disconnect() {
        portControl.disconnect();
    }
}
