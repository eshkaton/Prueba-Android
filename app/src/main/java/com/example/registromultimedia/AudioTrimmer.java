package com.example.registromultimedia;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Corta un audio .m4a (AAC) entre dos marcas de tiempo, copiando las
 * muestras directamente del contenedor original a uno nuevo, sin
 * necesidad de decodificar ni volver a codificar el audio.
 */
public final class AudioTrimmer {

    private AudioTrimmer() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Copia {@code origen} sobre {@code destino}.
     *
     * <p>El recorte se aplica copiando el resultado encima del audio
     * original en lugar de borrarlo y renombrar el temporal: si el
     * renombrado fallaba, el original ya estaba borrado y la grabación se
     * perdía para siempre.</p>
     *
     * @return {@code true} si la copia se completó.
     */
    public static boolean copiar(@NonNull File origen, @NonNull File destino) {

        try (FileInputStream entrada = new FileInputStream(origen);
             FileOutputStream salida = new FileOutputStream(destino)) {

            byte[] buffer = new byte[8192];
            int leidos;

            while ((leidos = entrada.read(buffer)) > 0) {
                salida.write(buffer, 0, leidos);
            }

            salida.getFD().sync();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    public static boolean cortarAudio(
            String rutaOrigen,
            String rutaDestino,
            long inicioMs,
            long finMs
    ) {

        MediaExtractor extractor = new MediaExtractor();
        MediaMuxer muxer = null;
        boolean muxerIniciado = false;

        try {

            extractor.setDataSource(rutaOrigen);

            int pistaAudio = -1;
            MediaFormat formatoPista = null;

            for (int i = 0; i < extractor.getTrackCount(); i++) {

                MediaFormat formato = extractor.getTrackFormat(i);
                String mime = formato.getString(MediaFormat.KEY_MIME);

                if (mime != null && mime.startsWith("audio/")) {
                    pistaAudio = i;
                    formatoPista = formato;
                    break;
                }
            }

            if (pistaAudio == -1 || formatoPista == null) {
                return false;
            }

            extractor.selectTrack(pistaAudio);

            long inicioUs = inicioMs * 1000L;
            long finUs = finMs * 1000L;

            extractor.seekTo(inicioUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

            muxer = new MediaMuxer(rutaDestino, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int pistaDestino = muxer.addTrack(formatoPista);
            muxer.start();
            muxerIniciado = true;

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

            boolean seEscribioAlgo = false;

            while (true) {

                int tamano = extractor.readSampleData(buffer, 0);
                if (tamano < 0) break;

                long tiempoMuestra = extractor.getSampleTime();
                if (tiempoMuestra < 0 || tiempoMuestra > finUs) break;

                if (tiempoMuestra >= inicioUs) {

                    info.offset = 0;
                    info.size = tamano;
                    info.presentationTimeUs = tiempoMuestra - inicioUs;
                    info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;

                    muxer.writeSampleData(pistaDestino, buffer, info);
                    seEscribioAlgo = true;
                }

                extractor.advance();
            }

            return seEscribioAlgo;

        } catch (Exception e) {

            return false;

        } finally {

            try {
                extractor.release();
            } catch (Exception ignored) {
            }

            if (muxer != null) {
                try {
                    if (muxerIniciado) {
                        muxer.stop();
                    }
                } catch (Exception ignored) {
                }
                try {
                    muxer.release();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
