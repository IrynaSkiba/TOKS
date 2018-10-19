package sample;

import javafx.application.Platform;
import jssc.*;

public class PortControl {
    private SerialPort serialPort;
    private String portName;
    private String speedInBod;
    private Controller controller;
    private boolean isPortOpened = false;

    PortControl(Controller controller) {
        this.controller = controller;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSpeedInBod(String speedInBod) {
        this.speedInBod = speedInBod;
    }

    public void connect() {
        if (isPortOpened) {
            System.out.println("Port is already opened!");
            return;
        }
        if (portName == null) {
            System.out.println("Cannot open port! You should set a name of port.");
            return;
        }

        serialPort = new SerialPort(portName);

        try {
            switch (speedInBod) {
                case "300":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_300,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "1200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_1200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "4800":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_4800,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "9600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "19200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_19200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "38400":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_38400,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "57600":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_57600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                case "115200":
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_115200,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    break;
                default:
                    System.out.println("Error: you should set settings.");
                    return;
            }

            //flow control
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            //listener
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            isPortOpened = true;
            System.out.println("Port has been opened. " + "\nSpeed in baud: " + speedInBod);

        } catch (SerialPortException e) {
            e.printStackTrace();

            if (e.getExceptionType().equals("Port busy")) {
                System.out.println("Cannot open port: port is busy.");
            } else {
                System.out.println("Cannot open port: " + e.getExceptionType());
            }
        }
    }

    public void disconnect() {
        if (!isPortOpened) {
            System.out.println("Port is closed!");
            return;
        }

        try {
            serialPort.closePort();
            isPortOpened = false;
            System.out.println("Port has been closed.");
        } catch (SerialPortException e) {
            e.printStackTrace();
            System.out.println("Cannot close port!");
        }
    }

    void sendMessage(String s) {
        try {
            serialPort.writeString(s);
        } catch (SerialPortException e) {
            System.out.println("Cannot send message: " + e.getExceptionType());
        }
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    String data = serialPort.readString(event.getEventValue());
                    printMessage(data);
                }
                catch (SerialPortException e) {
                    e.printStackTrace();
                    System.out.println("Cannot read message: serialEvent(SerialPortEvent event)");
                }
            }
        }
    }

    synchronized private void printMessage(String s) {
        Platform.runLater(() -> controller.receiveMessage(s));
    }

}
