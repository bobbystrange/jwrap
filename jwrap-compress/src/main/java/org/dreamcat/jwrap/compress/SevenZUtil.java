package org.dreamcat.jwrap.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2020/4/7
 */
@Slf4j
public final class SevenZUtil {

    private SevenZUtil() {
    }

    private static final int BUFFER_SIZE = 4096;

    public static void archive(File srcFile, File destFile) throws IOException {
        archive(srcFile, destFile, SevenZMethod.LZMA2);
    }

    public static void archive(File srcFile, File destFile, SevenZMethod method)
            throws IOException {
        try (SevenZOutputFile outs = new SevenZOutputFile(destFile)) {
            outs.setContentCompression(method);
            archive(srcFile, outs, "");
        }
    }

    public static void archive(File srcFile, SevenZOutputFile outs, String basePath)
            throws IOException {
        if (srcFile.isDirectory()) {
            archiveDir(srcFile, outs, basePath);
        } else {
            archiveFile(srcFile, outs, basePath);
        }
    }

    private static void archiveDir(File dir, SevenZOutputFile outs, String basePath)
            throws IOException {
        File[] files = dir.listFiles();
        if (ObjectUtil.isEmpty(files)) {
            SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName(basePath + dir.getName() + "/");
            outs.putArchiveEntry(entry);
            outs.closeArchiveEntry();
            return;
        }

        for (File file : files) {
            archive(file, outs, basePath + dir.getName() + "/");
        }
    }

    private static void archiveFile(File file, SevenZOutputFile outs, String basePath)
            throws IOException {
        SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName(basePath + file.getName());
        entry.setSize(file.length());
        outs.putArchiveEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = bis.read(data)) > 0) {
                outs.write(data, 0, count);
            }
        }
        outs.closeArchiveEntry();
    }

    public void archive(String srcFile, String destFile) throws IOException {
        archive(new File(srcFile), new File(destFile));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void unarchive(String srcFile, String destFile) throws IOException {
        unarchive(new File(srcFile), new File(destFile));
    }

    public void unarchive(File srcFile, File destFile) throws IOException {
        try (SevenZFile ins = new SevenZFile(srcFile)) {
            SevenZArchiveEntry entry;
            while ((entry = ins.getNextEntry()) != null) {
                File dirFile = new File(destFile.getPath() + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    if (!dirFile.mkdirs() && !dirFile.exists()) {
                        log.error("Failed to mkdir {}", dirFile);
                    }
                } else {
                    File parentDir = dirFile.getParentFile();
                    if (!parentDir.mkdirs() && !parentDir.exists()) {
                        log.error("Failed to mkdir {}", parentDir);
                        return;
                    }
                    unarchiveFile(dirFile, ins, entry.getSize());
                }
            }
        }
    }

    private void unarchiveFile(File destFile, SevenZFile ins, long size) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] data = new byte[BUFFER_SIZE];
            int remaining = (int) (size % BUFFER_SIZE);
            long quotient = size / BUFFER_SIZE;
            int count;
            for (int i = 0; i < quotient; i++) {
                count = ins.read(data);
                if (count != BUFFER_SIZE) {
                    log.error("Discontinuous file stream, expect read {} but got {}", BUFFER_SIZE,
                            count);
                    if (!destFile.delete() && destFile.exists()) {
                        log.error("Failed to rm {}", destFile);
                    }
                    return;
                }
                fos.write(data);
            }
            if (remaining > 0) {
                ins.read(data, 0, remaining);
                fos.write(data, 0, remaining);
            }
        }
    }

}
