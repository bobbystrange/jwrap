package org.dreamcat.jwrap.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * Create by tuke on 2020/4/7
 */
@Slf4j
public final class ZipUtil {

    private ZipUtil() {
    }

    private static final int BUFFER_SIZE = 4096;

    public static void archive(File srcFile, File destFile) throws IOException {
        archive(srcFile, destFile, Deflater.DEFAULT_COMPRESSION);
    }

    public static void archive(File srcFile, File destFile, int level) throws IOException {
        try (ZipArchiveOutputStream outs = new ZipArchiveOutputStream(
                new FileOutputStream(destFile))) {
            outs.setLevel(level);
            archive(srcFile, outs, "");
            outs.flush();
        }
    }

    private static void archive(File srcFile, ArchiveOutputStream outs, String basePath)
            throws IOException {
        if (srcFile.isDirectory()) {
            archiveDir(srcFile, outs, basePath);
        } else {
            archiveFile(srcFile, outs, basePath);
        }
    }

    private static void archiveDir(File dir, ArchiveOutputStream outs, String basePath)
            throws IOException {
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            ZipArchiveEntry entry = new ZipArchiveEntry(basePath + dir.getName() + "/");
            entry.setSize(0);

            outs.putArchiveEntry(entry);
            outs.closeArchiveEntry();
            return;
        }

        for (File file : files) {
            archive(file, outs, basePath + dir.getName() + "/");
        }
    }

    private static void archiveFile(File file, ArchiveOutputStream outs, String basePath)
            throws IOException {
        ZipArchiveEntry entry = new ZipArchiveEntry(basePath + file.getName());
        entry.setSize(file.length());

        outs.putArchiveEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                outs.write(data, 0, count);
            }
        }

        outs.closeArchiveEntry();
    }

    public static void unarchive(File srcFile, File destFile) throws IOException {
        try (ArchiveInputStream ins = new ZipArchiveInputStream(
                new FileInputStream(srcFile))) {
            unarchive(destFile, ins);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static void unarchive(File destFile, ArchiveInputStream ins) throws IOException {
        ArchiveEntry entry;
        while ((entry = ins.getNextEntry()) != null) {
            File file = new File(destFile.getPath() + File.separator + entry.getName());
            File parentFile = file.getParentFile();
            if (entry.isDirectory()) {
                if (!file.mkdirs() && !file.exists()) {
                    log.error("Failed to mkdir {}", file);
                }
            } else {
                if (!parentFile.mkdirs() && !parentFile.exists()) {
                    log.error("Failed to mkdir {}", parentFile);
                    return;
                }
                unarchiveFile(file, ins);
            }
        }
    }

    public static void unarchiveFile(File destFile, ArchiveInputStream ins) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile))) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = ins.read(data, 0, BUFFER_SIZE)) != -1) {
                bos.write(data, 0, count);
            }
        }
    }

    public void archive(String srcFile, String destFile) throws IOException {
        archive(new File(srcFile), new File(destFile));
    }

    public void unarchive(String srcFile, String destFile) throws IOException, ArchiveException {
        unarchive(new File(srcFile), new File(destFile));
    }
}
