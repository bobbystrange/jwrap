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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * Create by tuke on 2020/4/7
 */
public final class BZip2Util {

    private BZip2Util() {
    }

    private static final int bufferSize = 4096;

    public static byte[] compress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs);
                return outs.toByteArray();
            }
        }
    }

    public static void compress(InputStream ins, OutputStream outs) throws IOException {
        try (BZip2CompressorOutputStream cos = new BZip2CompressorOutputStream(outs)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = ins.read(data)) > 0) {
                cos.write(data, 0, count);
            }
            cos.finish();
        }
    }

    public static void compress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos);
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
        try (CompressorInputStream cis = new BZip2CompressorInputStream(ins)) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = cis.read(data)) > 0) {
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
