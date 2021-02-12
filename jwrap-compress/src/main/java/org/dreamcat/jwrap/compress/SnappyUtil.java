package org.dreamcat.jwrap.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;

/**
 * Create by tuke on 2020/4/7
 */
public final class SnappyUtil {

    private SnappyUtil() {
    }

    private static final int DEFAULT_BLOCK_SIZE = SnappyCompressorInputStream.DEFAULT_BLOCK_SIZE;

    public static byte[] compress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs, data.length);
                return outs.toByteArray();
            }
        }
    }

    public static void compress(File srcFile, File destFile) throws IOException {
        compress(srcFile, destFile, DEFAULT_BLOCK_SIZE);
    }

    public static void compress(File srcFile, File destFile, int blockSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos, srcFile.length(), blockSize);
            }
        }
    }

    // 32k block size
    public static void compress(InputStream ins, OutputStream outs, long uncompressedSize)
            throws IOException {
        compress(ins, outs, uncompressedSize, DEFAULT_BLOCK_SIZE);
    }

    public static void compress(InputStream ins, OutputStream outs, long uncompressedSize,
            int blockSize) throws IOException {
        SnappyCompressorOutputStream cos = new SnappyCompressorOutputStream(outs, uncompressedSize,
                blockSize);
        int count;
        byte[] data = new byte[4096];
        while ((count = ins.read(data)) > 0) {
            cos.write(data, 0, count);
        }
        cos.finish();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static byte[] uncompress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                uncompress(ins, outs, data.length);
                return outs.toByteArray();
            }
        }
    }

    public static void uncompress(File srcFile, File destFile) throws IOException {
        uncompress(srcFile, destFile, DEFAULT_BLOCK_SIZE);
    }

    public static void uncompress(File srcFile, File destFile, int blockSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                uncompress(fis, fos, blockSize);
            }
        }
    }

    public static void uncompress(InputStream ins, OutputStream outs) throws IOException {
        uncompress(ins, outs, DEFAULT_BLOCK_SIZE);
    }

    public static void uncompress(InputStream ins, OutputStream outs, int blockSize)
            throws IOException {
        SnappyCompressorInputStream cis = new SnappyCompressorInputStream(ins, blockSize);
        int count;
        byte[] data = new byte[4096];
        while ((count = cis.read(data)) > 0) {
            outs.write(data, 0, count);
        }
        outs.flush();
    }

}
