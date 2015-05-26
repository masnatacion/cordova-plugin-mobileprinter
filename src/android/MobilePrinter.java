package com.phonegap.plugins.mobileprinter;
 
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.util.Log;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Date;

// import org.apache.cordova.api.Plugin;
// import org.apache.cordova.api.PluginResult;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;

public class MobilePrinter  extends CordovaPlugin 
{
    public static final String PRINT_LABEL = "printLabel";
    public static final String SUCCESS = "success";
    static boolean busy = false;
   
 
 
   public MobilePrinter() {}
   
 
   public String getVersion() { return "0.1.1"; }
   
   public String print(String BluetoothAddress, String jSerial, String description) {
     String jser = jSerial;
     if (jser.length() > 8) jser = jser.substring(0, 8);
     String desc = description;
     if (desc.length() > 24) desc = desc.substring(0, 24);
     Integer posit = Integer.valueOf((int)Math.floor((24 - desc.length()) / 2 * 14));
     String Label = "! 0 200 200 253 1\r\nPW 381\r\nTONE 0\r\nSPEED 3\r\nON-FEED FEED\r\nB QR 120 30 M 2 U 6\r\nLA," + jser + "\r\nENDQR\r\nTEXT 0 3 " + posit.toString() + " 175 " + desc + "\r\nTEXT 0 3 " + Integer.valueOf(posit.intValue() + 2).toString() + " 175 " + desc + "\r\nFORM\r\nPRINT\r\n";
     return printraw(BluetoothAddress, Label);
   }
   
   public String printqre(String BluetoothAddress, String qrEntityBuffer, String headerText, String noteText) {
     String[] qre = qrEntityBuffer.split("[|]");
     String theQR = qrEntityBuffer;
     String MaterialDescription = qre[2];
     String CustomerName = qre[11];
     String JSerialNumber = qre[4];
     String PlantID = qre[15];
     Date dt = new Date();
     String dts = Integer.toString(dt.getMonth() + 1) + "/" + Integer.toString(dt.getDate()) + "/" + Integer.toString(dt.getYear() - 100);
     
     int ln = MaterialDescription.length();
     if (ln > 40) ln = 40;
     String desc = MaterialDescription.substring(0, ln);
     if (desc.length() < MaterialDescription.length()) desc = desc + " ...";
     ln = CustomerName.length();
     if (ln > 40) ln = 40;
     String cust = CustomerName.substring(0, ln);
     if (cust != "") cust = "Customer: " + cust;
     if (noteText != "") {
       ln = noteText.length();
       if (ln > 40) ln = 40;
       cust = noteText.substring(0, ln);
     }
     String headerText1 = headerText;
     if (headerText1 != "") {
       ln = headerText1.length();
       if (ln > 21) ln = 21;
       headerText1 = headerText1.substring(0, ln);
       for (int q = 1; q <= (21 - headerText1.length()) / 2; q++) {
         headerText1 = " " + headerText1;
       }
     }
     String qrSize = "5";
     if (theQR.length() > 160) qrSize = "4";
     if (theQR.length() > 280) qrSize = "3";
     String theLabel = "! 0 200 200 450 1\r\nPW 381\r\nTONE 0\r\nSPEED 3\r\nON-FEED FEED\r\nB QR 80 40 M 2 U " + 
     
       qrSize + "\r\n" + 
       "LA," + theQR + "\r\n" + 
       "ENDQR\r\n" + 
       "TEXT 0 3 22 10 " + headerText1 + "\r\n" + 
       "TEXT 0 3 24 10 " + headerText1 + "\r\n" + 
       "TEXT 0 3 120 285 " + JSerialNumber + "\r\n" + 
       "TEXT 0 3 122 285 " + JSerialNumber + "\r\n" + 
       "TEXT 0 0 22 320 " + desc + "\r\n" + 
       "TEXT 0 0 23 320 " + desc + "\r\n" + 
       "TEXT 0 0 22 340 " + cust + "\r\n" + 
       "TEXT 0 0 23 340 " + cust + "\r\n" + 
       "TEXT 0 0 22 360 " + dts + "  " + PlantID + "\r\n" + 
       "TEXT 0 0 23 360 " + dts + "  " + PlantID + "\r\n" + 
       "FORM\r\nPRINT\r\n";
     
     return printraw(BluetoothAddress, theLabel);
   }
   
   public String printraw(String BluetoothAddress, String Label)
   {
     OutputStream output = null;
     BluetoothSocket s = null;
     BluetoothAdapter bTadapter = null;
     BluetoothDevice dev = null;
     Looper threadLooper = Looper.myLooper();
     
     try
     {
       Log.d("MobilPrinting", "Bluetooth Address = " + BluetoothAddress);
       Log.d("MobilPrinting", "Label = " + Label);
       
       for (int ii = 1; ii < 60; ii++) {
         if (!busy) break;
         Thread.sleep(500L);
       }
       busy = true;
       if (threadLooper == null) Looper.prepare();
       bTadapter = BluetoothAdapter.getDefaultAdapter();
       if (!bTadapter.isEnabled()) {
         Log.i("MobilPrinting", "Starting Bluetooth services...");
         bTadapter.enable();
         for (int ii = 1; ii < 10; ii++) {
           if (bTadapter.isEnabled()) break;
           try { Thread.sleep(1000L);
           } catch (Exception localException1) {}
         } }
       bTadapter.cancelDiscovery();
       dev = bTadapter.getRemoteDevice(BluetoothAddress);
       Method m = dev.getClass().getMethod("createInsecureRfcommSocket", new Class[] { Integer.TYPE });
       s = (BluetoothSocket)m.invoke(dev, new Object[] { Integer.valueOf(1) });
       
       s.connect();
       
       output = s.getOutputStream();
       output.write(Label.getBytes("Cp1252"));
       output.flush();
       Thread.sleep(500L);
       try { output.close(); } catch (Exception localException2) {}
       output = null;
       try { s.close(); } catch (Exception e) { Log.e("MobilPrinting", "Close: " + e.toString()); }
       s = null;
       dev = null;
       bTadapter = null;
       m = null;
       Thread.sleep(1000L);
       busy = false;
       if (threadLooper == null) { Looper.myLooper().quit();
       }
       return "success";
     }
     catch (Exception e)
     {
       Log.e("MobilPrinting", e.toString());
       try { output.close(); } catch (Exception localException3) {}
       output = null;
       try { s.close(); } catch (Exception localException4) {}
       s = null;
       dev = null;
       bTadapter = null;
       busy = false;
       if (threadLooper == null) Looper.myLooper().quit();
       String error = e.toString();
       error = error.replaceAll("java.io.IOException: ", "");
       error = error.replaceAll("Host ", "Printer ");
       return "Print Error: " + error;
     }
   }

   public PluginResult execute(String paramString1, JSONArray paramJSONArray, String paramString2)
   {
     if (paramString1.equals("printLabel"))
     {
       Log.d("MobilePrinter", "PrintLabel Function Called");
       String str1;
       try
       {
         String str2 = printraw(paramJSONArray.getString(0), paramJSONArray.getString(1));
         str1 = str2;
       }
       catch (Exception localException)
       {
         for (;;)
         {
           Log.d("MobilePrinter", localException.toString());
           str1 = null;
         }
       }
       if (str1.equals("success")) {
         return new PluginResult(PluginResult.Status.OK, "Label Printed");
       }
       return new PluginResult(PluginResult.Status.ERROR, str1);
     }
     return null;
   }

}
