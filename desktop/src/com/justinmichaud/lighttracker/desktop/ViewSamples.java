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

import com.badlogic.gdx.math.Vector3;
import gnu.io.NRSerialPort;

public class ViewSamples {
    public static void main (String[] arg) {
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1200;
        config.height = 600;

        new LwjglApplication(new ViewSamplesApplication() , config);
    }

    private static class ViewSamplesApplication extends ApplicationAdapter {

        private static boolean SERIAL = true;

        private ShapeRenderer shapeRenderer;
        // Coordinates between 0 and 1 inclusive from arduino
        private final Vector3 position = new Vector3();
        private NRSerialPort serial;
        private ReaderThread reader;
        private WriterThread writer;

        private int frame = 0, framesPerSample = 80;
        private int patternSize = 20;

        @Override
        public void create() {
            shapeRenderer = new ShapeRenderer();

            if (!SERIAL) {
                writer = new WriterThread(System.out);
                return;
            }

            for(String s : NRSerialPort.getAvailableSerialPorts()){
                System.out.println("Availible port: "+s);
            }
            final String port = "/dev/ttyACM0";
            int baudRate = 9600;
            serial = new NRSerialPort(port, baudRate);
            serial.connect();

            reader = new ReaderThread(serial.getInputStream(), new Consumer<String>() {
                @Override
                public void accept(String value) {
                    String[] parts = value.split(",");
                    if (parts.length != 2) return;

                    try {
                        float x = Float.parseFloat(parts[0]);
                        float y = Float.parseFloat(parts[1]);

                        synchronized (position) {
                            position.set(x,y,0);
                        }

                        System.out.println("Position: " + x + "," + y);
                    } catch (NumberFormatException e) {
                        //Do nothing
                    }
                }
            });
            writer = new WriterThread(serial.getOutputStream());
        }

        @Override
        public void render() {
            Gdx.gl.glClearColor(0,0,0,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderPattern();
            renderPosition();
            shapeRenderer.end();
        }

        private void renderPattern() {
            int top = 0;
            int bottom = Gdx.graphics.getHeight();
            int left = 0;
            int right = Gdx.graphics.getWidth()/2;

            frame++;
            int elapsedSampleFrames = (frame % framesPerSample);
            int state = elapsedSampleFrames/(framesPerSample/2);

            if (state == 0) { //Horizontal sweep
                if (elapsedSampleFrames == 0) writer.write("v");
                else if (elapsedSampleFrames == framesPerSample/2-1) writer.write("w");

                int rows = (right-left)/ patternSize;
                int totalSamples = framesPerSample/2;
                int currentSample = (elapsedSampleFrames - state*framesPerSample/2);
                int x = currentSample * rows / totalSamples * patternSize;

                shapeRenderer.rect(x,top, patternSize, bottom-top);
            }
            else { //Vertical sweep
                if (elapsedSampleFrames == framesPerSample/2) writer.write("h");
                else if (elapsedSampleFrames == framesPerSample-1) writer.write("i");

                int columns = (bottom-top)/ patternSize;
                int totalSamples = framesPerSample/2;
                int currentSample = (elapsedSampleFrames - state*framesPerSample/2);
                int y = currentSample * columns / totalSamples * patternSize;

                shapeRenderer.rect(left,y, right-left, patternSize);
            }
        }

        private void renderPosition() {
            float top = 0;
            float bottom = Gdx.graphics.getHeight();
            float left = Gdx.graphics.getWidth()/2;
            float right = Gdx.graphics.getWidth();

            shapeRenderer.rect(position.x*(right-left) + left, position.y*(bottom-top) + top, 10, 10);
        }

        @Override
        public void dispose() {
            if (!SERIAL) return;
            reader.requestStop();
            serial.disconnect();
        }
    }

    public interface Consumer<T> {
        void accept(T var1);
    }

    private static class ReaderThread extends Thread {
        private final BufferedReader in;
        private final Consumer<String> callback;
        private boolean running = true;

        public ReaderThread(InputStream in, Consumer<String> callback) {
            this.in = new BufferedReader(new InputStreamReader(in));
            this.callback = callback;
            start();
        }

        public void run() {
            while (running) {
                try {
                    callback.accept(in.readLine());
                } catch (Exception e) {
                    //e.printStackTrace();
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

                for (String s : toWrite) out.print(s);
                out.flush();
            }

            out.close();
        }

        public void write(String line) {
            synchronized (lines) {
                lines.add(line);
            }
        }

        public void requestStop() {
            running = false;
        }
    }
}
