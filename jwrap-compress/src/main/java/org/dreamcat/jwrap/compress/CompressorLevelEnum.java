package org.dreamcat.jwrap.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

/**
 * Create by tuke on 2020/4/7
 */
@RequiredArgsConstructor
public enum CompressorLevelEnum {
    Gzip(-1, CompressorLevelEnum::newGzipCompressorOutputStream,
            GzipCompressorInputStream::new, CompressorLevelEnum::finishGzipCompressorOutputStream),
    Deflate(-1, CompressorLevelEnum::newDeflateCompressorOutputStream,
            DeflateCompressorInputStream::new,
            CompressorLevelEnum::finishDeflateCompressorOutputStream);

    private final int defaultLevel;
    private final OutputConstructor outputConstructor;
    private final InputConstructor inputConstructor;
    private final org.dreamcat.jwrap.compress.CompressorEnum.FinishMethod finishMethod;
    private volatile int bufferSize = 4096;

    private static CompressorOutputStream newGzipCompressorOutputStream(
            OutputStream outs, int level) throws IOException {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(level);
        return new GzipCompressorOutputStream(outs, parameters);
    }

    private static CompressorOutputStream newDeflateCompressorOutputStream(
            OutputStream outs, int level) throws IOException {
        DeflateParameters parameters = new DeflateParameters();
        parameters.setCompressionLevel(level);
        return new DeflateCompressorOutputStream(outs, parameters);
    }

    private static void finishGzipCompressorOutputStream(CompressorOutputStream cos)
            throws IOException {
        ((GzipCompressorOutputStream) cos).finish();
    }

    private static void finishDeflateCompressorOutputStream(CompressorOutputStream cos)
            throws IOException {
        ((DeflateCompressorOutputStream) cos).finish();
    }

    public synchronized CompressorLevelEnum withBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    // compress
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, defaultLevel);
    }

    public byte[] compress(byte[] data, int level) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs, level);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void compress(InputStream ins, OutputStream outs) throws IOException {
        compress(ins, outs, defaultLevel);
    }

    public void compress(InputStream ins, OutputStream outs, int level) throws IOException {
        try (CompressorOutputStream cos = outputConstructor.newInstance(outs, level)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = ins.read(data)) > 0) {
                cos.write(data, 0, count);
            }
            finishMethod.finish(cos);
        }
    }

    public void compress(File srcFile, File destFile) throws IOException {
        compress(srcFile, destFile, defaultLevel);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void compress(File srcFile, File destFile, int level) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos, level);
            }
        }
    }

    // uncompress
    public byte[] uncompress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                uncompress(ins, outs);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    public void uncompress(InputStream ins, OutputStream outs) throws IOException {
        try (CompressorInputStream cis = inputConstructor.newInstance(ins)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = cis.read(data)) > -1) {
                outs.write(data, 0, count);
            }
        }
    }

    public void uncompress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                uncompress(fis, fos);
            }
        }
    }

    @FunctionalInterface
    interface OutputConstructor {

        CompressorOutputStream newInstance(OutputStream outs, int level) throws IOException;
    }

    @FunctionalInterface
    interface InputConstructor {

        CompressorInputStream newInstance(InputStream outs) throws IOException;
    }

    @FunctionalInterface
    interface FinishMethod {

        void finish(CompressorOutputStream outs) throws IOException;
    }
}
