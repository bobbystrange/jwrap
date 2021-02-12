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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;

/**
 * Create by tuke on 2020/4/7
 */
@RequiredArgsConstructor
public enum CompressorEnum {
    FramedSnappy(FramedSnappyCompressorOutputStream::new, FramedSnappyCompressorInputStream::new,
            CompressorEnum::finishFramedSnappyCompressorOutputStream),
    BZ2(BZip2CompressorOutputStream::new, BZip2CompressorInputStream::new,
            CompressorEnum::finishBZip2CompressorOutputStream),
    FramedLZ4(FramedLZ4CompressorOutputStream::new, FramedLZ4CompressorInputStream::new,
            CompressorEnum::finishFramedLZ4CompressorOutputStream);

    private final OutputConstructor outputConstructor;
    private final InputConstructor inputConstructor;
    private final FinishMethod finishMethod;
    private volatile int bufferSize = 4096;

    private static void finishFramedSnappyCompressorOutputStream(CompressorOutputStream cos)
            throws IOException {
        ((FramedSnappyCompressorOutputStream) cos).finish();
    }

    private static void finishBZip2CompressorOutputStream(CompressorOutputStream cos)
            throws IOException {
        ((BZip2CompressorOutputStream) cos).finish();
    }

    private static void finishFramedLZ4CompressorOutputStream(CompressorOutputStream cos)
            throws IOException {
        ((FramedLZ4CompressorOutputStream) cos).finish();
    }

    public synchronized CompressorEnum withBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    // compress
    public byte[] compress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs);
                return outs.toByteArray();
            }
        }
    }

    public void compress(InputStream ins, OutputStream outs) throws IOException {
        try (CompressorOutputStream cos = outputConstructor.newInstance(outs)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = ins.read(data)) > 0) {
                cos.write(data, 0, count);
            }
            finishMethod.finish(cos);
        }
    }

    public void compress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos);
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

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
            while ((count = cis.read(data)) > 0) {
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

        CompressorOutputStream newInstance(OutputStream outs) throws IOException;
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
