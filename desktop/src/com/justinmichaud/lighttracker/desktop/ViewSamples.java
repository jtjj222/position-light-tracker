package com.justinmichaud.lighttracker.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import gnu.io.NRSerialPort;

public class ViewSamples {
    public static void main (String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 600;
        config.height = 600;

        new LwjglApplication(new ViewSamplesApplication() , config);
    }

    private static class ViewSamplesApplication extends ApplicationAdapter {

        private ShapeRenderer shapeRenderer;
        private final int[] samples = new int[600];
        private NRSerialPort serial;
        private ReaderThread reader;

        @Override
        public void create() {
            shapeRenderer = new ShapeRenderer();

            for(String s : NRSerialPort.getAvailableSerialPorts()){
                System.out.println("Availible port: "+s);
            }
            String port = "/dev/ttyACM0";
            int baudRate = 9600;
            serial = new NRSerialPort(port, baudRate);
            serial.connect();

            reader = new ReaderThread(serial.getInputStream(), new Consumer<Integer>() {
                @Override
                public void accept(Integer value) {
                    synchronized (samples) {
                        for (int i=1; i<samples.length; i++) {
                            samples[i-1] = samples[i];
                        }
                        samples[samples.length-1] = value;
                    }
                }
            });
        }

        @Override
        public void render() {
            Gdx.gl.glClearColor(0,0,0,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            synchronized (samples) {
                for (int i = 0; i < samples.length; i++) {
                    shapeRenderer.rect(i, 0, 1, (int) (Gdx.graphics.getHeight()/1024.0 * samples[i] * 4 - 350));
                }
            }
            shapeRenderer.end();
        }

        @Override
        public void dispose() {
            reader.requestStop();
            serial.disconnect();
        }
    }

    public interface Consumer<T> {
        void accept(T var1);
    }

    private static class ReaderThread extends Thread {
        private final BufferedReader in;
        private final Consumer<Integer> callback;
        private boolean running = true;

        public ReaderThread(InputStream in, Consumer<Integer> callback) {
            this.in = new BufferedReader(new InputStreamReader(in));
            this.callback = callback;
            start();
        }

        public void run() {
            while (running) {
                try {
                    callback.accept(Integer.parseInt(in.readLine()));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e1) {}
                }
            }

            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void requestStop() {
            running = false;
        }
    }

    private static class WriterThread extends Thread {
        private final PrintWriter out;
        private final Queue<String> lines = new LinkedList<String>();
        private boolean running = true;

        public WriterThread(OutputStream out) {
            this.out = new PrintWriter(out);
            start();
        }

        public void run() {
            while (running) {
                if (lines.isEmpty()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                Queue<String> toWrite;
                synchronized (lines) {
                    toWrite = new LinkedList<String>(lines);
                    lines.clear();
                }

                for (String s : toWrite) out.println(s);
            }

            out.close();
        }

        public void writeLine(String line) {
            synchronized (lines) {
                lines.add(line);
            }
        }

        public void requestStop() {
            running = false;
        }
    }
}
