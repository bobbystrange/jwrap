package org.dreamcat.jwrap.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

/**
 * Create by tuke on 2020/4/7
 */
public final class GzipUtil {

    private GzipUtil() {
    }

    private static final int bufferSize = 4096;
    private static final int DEFAULT_LEVEL = -1;

    // compress
    public static byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_LEVEL);
    }

    public static byte[] compress(byte[] data, int level) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs, level);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    public static void compress(InputStream ins, OutputStream outs) throws IOException {
        compress(ins, outs, DEFAULT_LEVEL);
    }

    public static void compress(InputStream ins, OutputStream outs, int level) throws IOException {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(level);
        try (GzipCompressorOutputStream cos = new GzipCompressorOutputStream(outs, parameters)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = ins.read(data)) > 0) {
                cos.write(data, 0, count);
            }
            cos.finish();
        }
    }

    public static void compress(File srcFile, File destFile) throws IOException {
        compress(srcFile, destFile, DEFAULT_LEVEL);
    }

    public static void compress(File srcFile, File destFile, int level) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos, level);
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    // uncompress
    public static byte[] uncompress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                uncompress(ins, outs);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    public static void uncompress(InputStream ins, OutputStream outs) throws IOException {
        try (CompressorInputStream cis = new GzipCompressorInputStream(ins)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = cis.read(data)) > -1) {
                outs.write(data, 0, count);
            }
        }
    }

    public static void uncompress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                uncompress(fis, fos);
            }
        }
    }


}
