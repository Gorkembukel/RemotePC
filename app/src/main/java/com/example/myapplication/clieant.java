package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class clieant {

    private static final BlockingQueue<Bitmap> frameQueue = new ArrayBlockingQueue<>(120);

    public static void startStream(Activity activity, String[] args, AtomicBoolean keepStream) {
        final Handler handler = new Handler(Looper.getMainLooper());

        // Gösterim thread'i
        new Thread(() -> {
            while (keepStream.get()) {
                try {
                    Bitmap bitmap = frameQueue.take();
                    handler.post(() -> {
                        ImageView imageView = activity.findViewById(R.id.imageView);
                        if (bitmap != null && !bitmap.isRecycled()) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (InterruptedException ignored) {}
            }
        }).start();

// Alım thread'i
        new Thread(() -> {
            try {
                String serverName = args[0];
                int port = Integer.parseInt(args[1]);
                Socket socket = new Socket(serverName, port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                while (keepStream.get()) {
                    int length = in.readInt();
                    if (length > 0) {
                        byte[] imageBytes = new byte[length];
                        in.readFully(imageBytes);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, length);
                        if (bitmap != null) frameQueue.put(bitmap); // OFFER yerine PUT!
                        Log.d("StartStream", "Yeni frame geldi");
                    }
                    out.write(5);
                    out.flush();
                }

                in.close();
                out.close();
                socket.close();
                keepStream.set(false);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                keepStream.set(false);
            }
        }).start();
    }
}