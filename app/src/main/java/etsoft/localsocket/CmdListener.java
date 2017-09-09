package etsoft.localsocket;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hwp on 2017/6/14.
 */

public class CmdListener extends Thread {

    // from test4android project
    private static final String TAG = "CmdListener";
    public static String SOCKET_ADDRESS = "local.socket.address.listen.native.cmd";//
    private boolean mStopped = false;

    public CmdListener() {
    }

    @Override
    public void run() {
        Log.i(TAG, "Server socket run . . . start");
        LocalServerSocket server = null;
        try {
            server = new LocalServerSocket(SOCKET_ADDRESS);
            while (!mStopped) {
                LocalSocket receiver = server.accept();
                if (receiver != null) {
                    InputStream inputStream = receiver.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String inputLine = null;
                    StringBuilder sb = new StringBuilder();
                    while (((inputLine = bufferedReader.readLine()) != null)) {
                        sb.append(inputLine);
                    }
                    //TODO do command
                    Log.i(TAG, "got cmd: " + sb.toString());
                    //CmdExecutor.doCommand(sb.toString());

                    bufferedReader.close();
                    receiver.close();
                    sb = null;
                }
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    Log.e(getClass().getName(), e.getMessage());
                }
            }
        }

        Log.i(TAG, "Server socket run . . . end");
    }

    public void stopListening() {
        Log.i(TAG, "stopListening");
        mStopped = true;
        try {
            writeSocket("bye bye~~~");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeSocket(String message) throws IOException {
        Log.i(TAG, "writeSocket, " + message);
        LocalSocket sender = new LocalSocket();
        sender.connect(new LocalSocketAddress(SOCKET_ADDRESS));
        sender.getOutputStream().write(message.getBytes());
        sender.getOutputStream().close();
        sender.close();
    }
}
