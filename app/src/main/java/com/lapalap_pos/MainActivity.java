package com.lapalap_pos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity implements View.OnClickListener {

    TextView myLabel;
    // will enable user to enter any text to be printed
    EditText myTextbox;
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    Button mBtnPrintBill;
    private LpHandler handler;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private LpThread lpThreadInstance;
    private Button mbtnWeighItem;
    private TextView mTxtVwMeasuredWeight;
    private Button mBtnResetFormatting;
    private Button mBtnSetFormatting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }


    private void setup(){
        // we are going to have three buttons for specific functions
        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
        mBtnPrintBill = (Button)findViewById(R.id.btnPrintBill);
        mTxtVwMeasuredWeight=(TextView) findViewById(R.id.txtvwWeight);
        mBtnResetFormatting=(Button)findViewById(R.id.resetFormatting);
        mBtnSetFormatting=(Button)findViewById(R.id.setFormatting);

// text label and input box
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);
        mbtnWeighItem = (Button)findViewById(R.id.btnweighItem);
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });



        mbtnWeighItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                   lpThreadInstance.write("<GW>");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // send data typed by the user to be printed
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    openBT();;
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mBtnPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                  //  openBT();;
                    printBill();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        handler = new LpHandler();
    }



    private void printBill(){
        try {

            String msg="";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
            String formattedDate = df.format(c.getTime());
            byte[] reset_format = {27, '@' };
            lpThreadInstance.writeCommand(reset_format);

           // lpThreadInstance.write(formattedDate+"\n");
            lpThreadInstance.writeCommand(new byte[]{27, '!',1 });
            String msgToBePrinter="";



            StringBuffer receiptBuf = new StringBuffer();

            lpThreadInstance.write("\n\n");
            lpThreadInstance.write("                    **                    ");
           // receiptBuf.append("_______________________________________\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("SINDI SWEETS\n");
            lpThreadInstance.write("S.C.O 1, Sector 17\n");
            lpThreadInstance.write("Chandigarh\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("Time :"+formattedDate+"\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("ITEM          RATE       QTY           AMT");
            lpThreadInstance.write("\n");
            // sku name can be of 10 chars max
            lpThreadInstance.write("PIZZA MGRT     200         2           400");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("GRLC BREAD     100         4           400");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("BARFI KAJU     500        10          5000");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("TOTAL                                 5800");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("VAT                            @10%    580");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("------------------------------------------");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("AMT DUE                               6380");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("\n");
            lpThreadInstance.write("Thank you\nVisit us on www.lapalap.com\n\n\n\n\n\n");
          //  lpThreadInstance.write(receiptBuf.toString());

           // myTextbox.setText(receiptBuf.toString());

          //  byte[] reset_format = {27, '@' };
         //   lpThreadInstance.writeCommand(reset_format);

            // lpThreadInstance.write(formattedDate+"\n");
          //  lpThreadInstance.writeCommand(new byte[]{27, '!',1 });




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {

            // the text typed by the user
            String msg = myTextbox.getText().toString();
           // msg += "\n";

            //mmOutputStream.write(msg.getBytes());

            lpThreadInstance.write(msg);

            // tell the user data were sent
            myLabel.setText("Data sent.");
            myTextbox.setText("");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    //device.getAddress().equalsIgnoreCase("20:15:05:05:69:97")
                    //if (device.getName().equals("EST")) {
                    if(device.getAddress().equalsIgnoreCase("20:15:05:05:69:97")){
                        mmDevice = device;
                        mBluetoothAdapter.cancelDiscovery();
                        break;
                    }
                }
            }
            myLabel.setText("Bluetooth device found.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            if(lpThreadInstance!=null){
                return;
            }
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();

            lpThreadInstance = new LpThread(mmSocket);
            lpThreadInstance.start();
           // lpThreadInstance.write("--CONNECTION--ESTABLISHED--\n");


            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view==mBtnResetFormatting){
            byte[] reset_format = {27, '@' };
            if(lpThreadInstance!=null)
            lpThreadInstance.writeCommand(reset_format);

        }if(view==mBtnSetFormatting){

            if(lpThreadInstance!=null)
                lpThreadInstance.writeCommand(new byte[]{27, '!',1 });

        }
    }


    private class LpHandler extends Handler{

       @Override
      public void handleMessage(Message msg){

           switch (msg.what){
               case CursorAdapter.FLAG_AUTO_REQUERY:
                    String incomingMessage = (String) msg.obj;
                    mTxtVwMeasuredWeight.setText(incomingMessage);
           }
       }
   }



    static void tDelay(int mSec){
        long delayT = new Date().getTime()+mSec;
        long j=0;
        while(delayT>(new Date().getTime())){
                for(int i=0;i<100;i++){
                    j++;
                }
        }

    }



    private class LpThread extends Thread{

        private InputStream lpInputStream;
        private OutputStream lpOutputStream;

        public LpThread(BluetoothSocket socket){
            try{
                lpInputStream = socket.getInputStream();
                lpOutputStream=socket.getOutputStream();
            }catch (Exception e){
                lpInputStream=null;
                lpOutputStream=null;
            }
        }

        @Override
        public void run(){
            byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
            while(true){
                try {
                    int bytes = this.lpInputStream.read(buffer);
                    handler.obtainMessage(1, bytes, -1, new String(buffer, 0, bytes)).sendToTarget();
                } catch (IOException e) {
                    return;
                }
            }
        }


        public synchronized void write(String message) {
            try {
                this.lpOutputStream.write(message.getBytes());
                tDelay(300);
            } catch (IOException e) {
                MainActivity.this.finish();
                Toast.makeText(MainActivity.this.getBaseContext(), "DEVICE UNAVAILABLE", Toast.LENGTH_SHORT).show();
            }
        }

        public void writeCommand(byte[] b){
            try {
                this.lpOutputStream.write(b);
            } catch (IOException e) {
                MainActivity.this.finish();
                Toast.makeText(MainActivity.this.getBaseContext(), "DEVICE UNAVAILABLE", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
