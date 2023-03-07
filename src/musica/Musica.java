package musica;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author char_
 */
public class Musica {

    private static AudioFormat format = null;
    private static SourceDataLine line = null;
    private static final int SAMPLE_RATE = 22050; // num muestras por segundo
    private static final double MAX_AMPLITUD = 32760; // Máx volúmen altavoz
    private static final int MIN_FREQ = 250;

    public static void main(String[] args) {
        System.out.println(1<<1);
        createOutput();
        play();
    }

    //Inicializamos audio
    private static void createOutput() {
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
        System.out.println("Audio format: " + format);
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line does not support: " + format);
                System.exit(0);
            }
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void play() {
        //Calculamos el tamaño de buffer
        int maxSize = (int) Math.round((SAMPLE_RATE * format.getFrameSize()) / MIN_FREQ);
        //Cada frame tiene 4 bytes
        System.out.println("Tamaño máximo buffer=" + maxSize);
        byte[] samples = new byte[200000];
        line.start();
        //Enviamos sonido al altavoz con una frecuencia de 300Hz 30 veces
        long t1 = System.currentTimeMillis();
        for (int n = 0; n < 100; n++) {
            sendNote(800, samples);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("\nTiempo total=" + (t2 - t1) + " ms");
        //Cerramos la línea
        line.drain();
        line.stop();
        line.close();
    }

    //Generamos y reproducimos la onda senoidal
    private static void sendNote(int freq, byte[] samples) {
        //int numMuestrasDentroDeLaOnda = (int) Math.round(((double) SAMPLE_RATE) / freq);
        int numMuestrasDentroDeLaOnda = 100;
        System.out.println("Num muestras dentro de la onda=" + numMuestrasDentroDeLaOnda);
        int idx = 0;
        //Creamos la onda senoidal
        for (int i = 0; i < numMuestrasDentroDeLaOnda; i++) {
            double sine = Math.sin(((double) i / numMuestrasDentroDeLaOnda) * 2.0 * Math.PI);
            int sample = (int) (sine * MAX_AMPLITUD);
            //System.out.println(sample);
            // Canal izquierdo
            samples[idx + 0] = (byte) (sample & 0xFF); // low byte
            samples[idx + 1] = (byte) ((sample >> 8) & 0xFF); // high byte
            // Canal derecho
            samples[idx + 2] = (byte) (sample & 0xFF);
            samples[idx + 3] = (byte) ((sample >> 8) & 0xFF);
            idx += 4;
        }
        // Reproduce el sonido
        int offset = 0;
        long cargaDatos = System.currentTimeMillis();
        /*while (offset < idx) {
            offset += line.write(samples, offset, idx - offset);
        }*/
        line.write(samples, 0, idx);
        long t1 = System.currentTimeMillis();
        System.out.println("Tiempo carga datos " + (t1 - cargaDatos) + " ms");
        //line.start();
        //line.drain();
        //line.flush();
        //line.stop();
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo en pausa por reproducción del audio " + (t2 - t1) + " ms , samples="+(idx/4));
        System.out.println("----------------------------------------------------");
    }
}
