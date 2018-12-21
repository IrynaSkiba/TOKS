package sample;

import javafx.application.Platform;
import jssc.*;

import java.util.Random;

import static java.lang.Byte.compare;
import static java.lang.Thread.sleep;

public class PortControl {
    private SerialPort serialPort;
    private String portName;
    private String speedInBod;
    private Controller controller;
    private boolean isPortOpened = false;
    private int sizeJamSignal = 8;
    private byte[] jamMessage;
    private int amauntMaxAttempt = 4;

    PortControl(Controller controller) {
        this.controller = controller;
        jamMessage = new byte[sizeJamSignal];
        for (int i = 0; i < sizeJamSignal; i++) {
            jamMessage[i] = 69;
        }
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

    private static String getBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    private static byte[] getByteArray(String s) {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for (int i = 0; i < sLen; i++)
            if ((c = s.charAt(i)) == '1')
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if (c != '0')
                throw new IllegalArgumentException();
        return toReturn;
    }

    private byte[] insertBit(byte[] byteArray) {
        String resString = getBinaryString(byteArray).replaceAll("0111111", "01111111");
        resString = "01111110" + resString; //строка бит
        return getByteArray(resString);
    }

    private byte[] removeBit(byte[] byteArray) {
        String temp = getBinaryString(byteArray);
        int start = temp.indexOf("01111110");
        if (start >= 0) {
            temp = temp.substring(start + 8);
            temp = temp.replaceAll("01111111", "0111111");
            return getByteArray(temp); //строка бит
        } else {
            return null;
        }
    }

    private byte[] hammingCode(String bitString) {
        int k = 0;
        int m = bitString.length();
        while (!(k >= Math.log(k + m + 1) / Math.log(2))) {
            k++;
        }
        StringBuilder controlBits = new StringBuilder(k);
        //int sizeNewBitSeq = bitString.length() + k;
        StringBuilder newBitSeq = new StringBuilder(bitString.length() + k);

        int i = 0;
        int j = 0;
        while (i < bitString.length() + k) {
            double c = (Math.log(i + 1) / Math.log(2)) % 1;
            if ((Math.log(i + 1) / Math.log(2)) % 1 == 0) {
                newBitSeq.append('0');
                i++;
                continue;
            }
            newBitSeq.append(bitString.charAt(j));
            i++;
            j++;
        }
        i = 0;
        while (i < k) {
            int a = 0;
            j = (int) Math.pow(2, i) - 1;
            while (j < bitString.length() + k) {
                int step = (int) Math.pow(2, i);
                while (step != 0) {
                    if (j == bitString.length() + k) {
                        break;
                    }
                    if (newBitSeq.charAt(j) == '1')
                        a += 1;
                    //else a+=0;
                    j++;
                    step--;
                }
                j += Math.pow(2, i);
            }
            if (a % 2 == 0) {
                controlBits.append('0');
                i++;
                continue;
            }
            controlBits.append('1');
            i++;
        }

        i = 0;
        j = 0;
        while (i < bitString.length() + k) {
            if (Math.log(i + 1) / Math.log(2) % 1 == 0) {
                newBitSeq.setCharAt(i, controlBits.charAt(j));
                j++;
            }
            i++;
        }
        String endString = newBitSeq.toString();
        return getByteArray(endString);
    }

    private byte[] removeBitControl(String binaryString) {
        int k = (int) ((Math.log(binaryString.length()) / Math.log(2)));
        if ((Math.log(binaryString.length()) / Math.log(2)) % 1 != 0)
            k++;

        StringBuilder new_bit_message = new StringBuilder(binaryString.length() - k);
        int i = 0;
        int j = 0;
        while (i < binaryString.length()) {
            if (Math.log(i + 1) / Math.log(2) % 1 == 0) {
                i++;
                continue;
            }
            new_bit_message.append(binaryString.charAt(i));
            j++;
            i++;
        }
        return getByteArray(new_bit_message.toString());
    }

    private String checkError(String binaryString) {
        int m = binaryString.length();
        int k = (int) (Math.log(m) / Math.log(2) - Math.log(m) / Math.log(2) % 1);
        if (Math.log(m) / Math.log(2) % 1 != 0) {
            k++;
        }
        StringBuilder newControlBits = new StringBuilder(k);
        int i = 0;
        while (i < k) {
            int a = 0;
            int j = (int) Math.pow(2, i) - 1;
            while (j < binaryString.length()) {
                int step = (int) Math.pow(2, i);
                while (step != 0) {
                    if (j == binaryString.length()) {
                        break;
                    }
                    if (binaryString.charAt(j) == '1')
                        a += 1;
                    // a += binaryString.charAt(j);
                    j++;
                    step--;
                }
                j += Math.pow(2, i);
            }
            if (a % 2 == 0) {
                newControlBits.append('0');
                i++;
                continue;
            }
            newControlBits.append('1');
            i++;
        }
        return newControlBits.toString();
    }

    private String correctError(byte[] byteArray, String mistake) {
        String binaryString = getBinaryString(byteArray);
        StringBuilder correct_message = new StringBuilder(binaryString);
        int place = 0;
        for (int i = 0; i < mistake.length(); i++) {
            if (mistake.charAt(i) == '1') {
                place += (int) Math.pow(2, i);
            }
        }
        if (correct_message.charAt(place) == '1') {
            correct_message.setCharAt(place, '0');
        } else {
            correct_message.setCharAt(place, '1');
        }
        return correct_message.toString();
    }

    private boolean isPackageMode() {
        return false;
    }

    private boolean isChannelFree() {
        Random rand = new Random();
        return rand.nextBoolean();
        //return true;
    }

    private boolean wasCollision() {
        Random rand = new Random();
        return rand.nextBoolean();
    }

    private byte[] getJamMessage() {
        return jamMessage;
    }

    void sendMessage(String s) {
        try {
            byte[] byteArray = s.getBytes();
            byte[] message = hammingCode(getBinaryString(insertBit(byteArray)));

            int amountAttemps = 0;
            while (true) {
                if (isPackageMode()) {
                    while (!isChannelFree()) ;
                }

                sleep(30);
                if (wasCollision()) {
                    System.out.println("Collision!");
                    byte[] mes = getJamMessage();
                    serialPort.writeBytes(mes);
                    amountAttemps++;
                    if (amountAttemps > amauntMaxAttempt)
                        break;
                    Random rand = new Random();
                    int k = Math.min(amountAttemps, amauntMaxAttempt);
                    int r = rand.nextInt((int) Math.pow(2, k));
                    Thread.sleep(r * 20);
                } else {
                    serialPort.writeBytes(message);
                    break;
                }
            }
        } catch (SerialPortException e) {
            System.out.println("Cannot send message: " + e.getExceptionType());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

           /* String strFalse;
            StringBuilder str = new StringBuilder(getBinaryString(message));
            if (controller.isMistake()) {
                if (str.charAt(13) == '0')
                    str.setCharAt(13, '1');
                else str.setCharAt(13, '0');
                //newBitsControl = checkError(str.toString());
            } //else {
                //newBitsControl = checkError(getBinaryString(byteArray));
           // }
            strFalse = str.toString();
            serialPort.writeBytes(getByteArray(strFalse));*/
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] byteArray;
                    byteArray = serialPort.readBytes(event.getEventValue());


                    if (!byteArray.equals(jamMessage)) {
                        byte[] newByteArray = removeBit(removeBitControl(getBinaryString(byteArray)));
                        printMessage(new String(newByteArray));
                        /*String my_message = new String(newByteArray);
                        StringBuilder newMes = new StringBuilder(my_message);
                        newMes.append(" collision");
                        printMessage(newMes.toString());*/
                    } else System.out.println("There's a collision");
                    //else printMessage(new String(newByteArray));
                } catch (SerialPortException e) {
                    e.printStackTrace();
                    System.out.println("Cannot read message: serialEvent(SerialPortEvent event)");
                }

                    /*
                    String newBitsControl;
                    StringBuilder str = new StringBuilder(getBinaryString(byteArray));
                    newBitsControl = checkError(getBinaryString(byteArray));
                    byte[] newByteArray;

                    if (newBitsControl.indexOf('1') == -1) {
                        System.out.println("OK!!!!");
                        controller.setError("Ok!!!");
                        newByteArray = removeBit(removeBitControl(getBinaryString(byteArray)));
                    } else {
                        // String newBinaryString = correctError(byteArray, newBitsControl);
                        //newByteArray = removeBit(removeBitControl(newBinaryString));
                        System.out.println("Error!!!!");
                        controller.setError("Error!!!");
                        newByteArray = removeBit(removeBitControl(getBinaryString(byteArray)));
                    }
                    printMessage(new String(newByteArray));*/

//                    newByteArray = removeBit(removeBitControl(getBinaryString(byteArray)));
            }
        }
    }

    synchronized private void printMessage(String s) {
        Platform.runLater(() -> controller.receiveMessage(s));  //controller.receiveMessage(s);
    }
}