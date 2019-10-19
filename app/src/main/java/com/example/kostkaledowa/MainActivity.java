package com.example.kostkaledowa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    TextView tv1,tv2;
    Button ref, red, green, blue;
    boolean colorChoice=false;
    String address, name;
    InputStream inputStream;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    Set<BluetoothDevice> btDevice;
    int readyFlag=1;
    char colorChar, playerColor;
    static final UUID hcUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int[][][] idArray = {
            {
                    {R.id.radioButton111, R.id.radioButton211, R.id.radioButton311, R.id.radioButton411},
                    {R.id.radioButton121, R.id.radioButton221, R.id.radioButton321, R.id.radioButton421},
                    {R.id.radioButton131, R.id.radioButton231, R.id.radioButton331, R.id.radioButton431},
                    {R.id.radioButton141, R.id.radioButton241, R.id.radioButton341, R.id.radioButton441},
            },
            {
                    {R.id.radioButton112, R.id.radioButton212, R.id.radioButton312, R.id.radioButton412},
                    {R.id.radioButton122, R.id.radioButton222, R.id.radioButton322, R.id.radioButton422},
                    {R.id.radioButton132, R.id.radioButton232, R.id.radioButton332, R.id.radioButton432},
                    {R.id.radioButton142, R.id.radioButton242, R.id.radioButton342, R.id.radioButton442},
            },
            {
                    {R.id.radioButton113, R.id.radioButton213, R.id.radioButton313, R.id.radioButton413},
                    {R.id.radioButton123, R.id.radioButton223, R.id.radioButton323, R.id.radioButton423},
                    {R.id.radioButton133, R.id.radioButton233, R.id.radioButton333, R.id.radioButton433},
                    {R.id.radioButton143, R.id.radioButton243, R.id.radioButton343, R.id.radioButton443},
            },
            {
                    {R.id.radioButton114, R.id.radioButton214, R.id.radioButton314, R.id.radioButton414},
                    {R.id.radioButton124, R.id.radioButton224, R.id.radioButton324, R.id.radioButton424},
                    {R.id.radioButton134, R.id.radioButton234, R.id.radioButton334, R.id.radioButton434},
                    {R.id.radioButton144, R.id.radioButton244, R.id.radioButton344, R.id.radioButton444},
            }
    };
    static Button radiobutton[][][] = new Button[100][100][100];
    char matrix[][][] = new char[100][100][100];
    boolean win = false;
    boolean connected = false;
    char temp4=' ';
    public void declareButtons() {
        for (int z = 0; z < 4; z++) {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    radiobutton[z][y][x] = (Button) findViewById(idArray[z][y][x]);
                    radiobutton[z][y][x].setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            }
        }
    }

    public void cleanMatrix() {
        for (int z = 0; z < 4; z++) {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    matrix[z][y][x] = ' ';
                }
            }
        }
    }
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String text = intent.getStringExtra("the message");
            tv2.setText(text);

        }
    };
    public void checkIfWin(char color) { //juz dziala, 100 rozmiary macierzy i buttonow naprawily
        for (int z = 0; z < 4; z++) {
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    if (((matrix[z][y][x] == color) && (matrix[z][y][x + 1] == color) &&
                            (matrix[z][y][x + 2] == color) && (matrix[z][y][x + 3] == color)) ||
                            ((matrix[z][y][x] == color) && (matrix[z][y + 1][x] == color) &&
                                    (matrix[z][y + 2][x] == color) && (matrix[z][y + 3][x] == color)) ||
                            ((matrix[z][y][x] == color) && (matrix[z + 1][y][x] == color) &&
                                    (matrix[z + 2][y][x] == color) && (matrix[z + 3][y][x] == color)) ||
                            (((matrix[z][y][x] == color) && (matrix[z + 1][y + 1][x + 1] == color) &&
                                    (matrix[z + 2][y + 2][x + 2] == color) && (matrix[z + 3][y + 3][x + 3] == color))) ||
                            (((matrix[z][y][x + 3] == color) && (matrix[z + 1][y + 1][x + 2] == color) &&
                                    (matrix[z + 2][y + 2][x + 1] == color) && (matrix[z + 3][y + 3][x] == color))) ||
                            (((matrix[z][y + 3][x] == color) && (matrix[z + 1][y + 2][x + 1] == color) &&
                                    (matrix[z + 2][y + 1][x + 2] == color) && (matrix[z + 3][y][x + 3] == color))) ||
                            (((matrix[z + 3][y][x] == color) && (matrix[z + 2][y + 1][x + 1] == color) &&
                                    (matrix[z + 1][y + 2][x + 2] == color) && (matrix[z][y + 3][x + 3] == color)))) {
                        win = true;
                        colorChar=color;
                    }

                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declareButtons();
        cleanMatrix();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter());
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        ref = (Button) findViewById(R.id.refresh);
        red = (Button) findViewById(R.id.buttonRED);
        green = (Button) findViewById(R.id.buttonGREEN);
        blue = (Button) findViewById(R.id.buttonBLUE);
        red.setBackgroundColor(Color.RED);
        green.setBackgroundColor(Color.GREEN);
        blue.setBackgroundColor(Color.BLUE);
        try {
            bluetoothConnect();
        } catch (IOException e) {

        }
    }


    public void afterClick(View v) {
            if (win) {
                transmit("WIN"+colorChar);
            }
            else if(connected && colorChoice){
                for (int z = 0; z < 4; z++) {
                    for (int y = 0; y < 4; y++) {
                        for (int x = 0; x < 4; x++) {
                            if (v.getId() == idArray[z][y][x]) {
                                if (matrix[z][y][x] == ' ') {
                                    String temp1=Integer.toString(x);
                                    String temp2=Integer.toString(y);
                                    String temp3=Integer.toString(z);
                                    temp4=playerColor;
                                    checkIfWin(temp4);
                                    transmit(temp3+temp2+temp1+temp4);
                                }
                            }
                        }
                    }
                }
            }
    }

    public void refresh(View v) {
        receive();
        readyFlag=1;
    }
    public void chooseRed(View v) {
        if(!colorChoice){
            red.setBackgroundColor(Color.RED);
            green.setBackgroundColor(Color.GRAY);
            blue.setBackgroundColor(Color.GRAY);
            colorChoice=true;
            playerColor='R';
        }
    }
    public void chooseGreen(View v) {
        if(!colorChoice){
            green.setBackgroundColor(Color.GREEN);
            red.setBackgroundColor(Color.GRAY);
            blue.setBackgroundColor(Color.GRAY);
            colorChoice=true;
            playerColor='G';
        }
    }
    public void chooseBlue(View v) {
        if(!colorChoice){
            blue.setBackgroundColor(Color.BLUE);
            red.setBackgroundColor(Color.GRAY);
            green.setBackgroundColor(Color.GRAY);
            colorChoice=true;
            playerColor='B';
        }
    }
    private void bluetoothConnect() throws IOException {
        while(!connected){
            try {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                address = btAdapter.getAddress();
                btDevice = btAdapter.getBondedDevices();
                if (btDevice.size() > 0) {
                    for (BluetoothDevice bt : btDevice) {
                        address = bt.getAddress().toString();
                        name = bt.getName().toString();

                    }
                }

            } catch (Exception we) {
            }
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice dispositivo = btAdapter.getRemoteDevice(address);
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(hcUUID);
            btSocket.connect();

            try {
                tv1.setText("BT Name: " + name + "\nBT Address: " + address);
            } catch (Exception e) {
            }
            connected=btSocket.isConnected();
        }

    }

    @Override
    public void onClick(View v) {
        try {

        } catch (Exception e) {


        }

    }
    private void transmit(String i) {
        try {
            if (btSocket != null) {
                if(readyFlag==1){
                    btSocket.getOutputStream().write(i.toString().getBytes());
                }
                readyFlag=0;


            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

        }

    }

    public void receive(){
        try {

            if(colorChoice){
                byte[] buffer = new byte[1024];
                int bytes;
                inputStream = btSocket.getInputStream();
                bytes = inputStream.read(buffer);
                String incomingMessage = new String(buffer, 0, bytes);
                if(!win){
                    tv2.setText(incomingMessage);
                }else{
                    tv2.setText("WIN"+colorChar);
                }
                char[] charArray = incomingMessage.toCharArray();
                int z=charArray[0]-'0';
                int y=charArray[1]-'0';
                int x=charArray[2]-'0';
                if(charArray[3]=='R' && matrix[z][y][x]==' ' && !win){
                    radiobutton[z][y][x].setBackgroundColor(Color.RED);
                    matrix[z][y][x]=charArray[3];
                }
                else if(charArray[3]=='G'&& matrix[z][y][x]==' ' && !win){
                    radiobutton[z][y][x].setBackgroundColor(Color.GREEN);
                    matrix[z][y][x]=charArray[3];
                }else if(charArray[3]=='B'&& matrix[z][y][x]==' ' && !win){
                    radiobutton[z][y][x].setBackgroundColor(Color.BLUE);
                    matrix[z][y][x]=charArray[3];
                }
            }

        } catch (IOException e) {

        }

    }
}


//http://strefakodera.pl/programowanie/android-java/broadcast-receiver-w-androidzie-co-to-takiego
//https://developer.android.com/reference/android/content/BroadcastReceiver
//https://zocada.com/using-intents-extras-pass-data-activities-android-beginners-guide/
//https://developer.android.com/reference/android/content/Intent.html#putExtra(java.lang.String,%20android.os.Bundle)
//https://stackoverflow.com/questions/20439753/how-to-get-back-message-in-android-using-intent-and-startactivityfor-result