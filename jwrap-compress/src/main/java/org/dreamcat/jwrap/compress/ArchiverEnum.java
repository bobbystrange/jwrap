package org.dreamcat.jwrap.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Create by tuke on 2020/4/7
 */
@Slf4j
@RequiredArgsConstructor
public enum ArchiverEnum {
    Ar(ArArchiveOutputStream::new, ArjArchiveInputStream::new,
            ArArchiveEntry::new),
    Cpio(CpioArchiveOutputStream::new, CpioArchiveInputStream::new,
            ArchiverEnum::newCpioArchiveEntry),

    Tar(TarArchiveOutputStream::new, TarArchiveInputStream::new,
            ArchiverEnum::newTarArchiveEntry);

    private final OutputConstructor outputConstructor;
    private final InputConstructor inputConstructor;
    private final EntryConstructor entryConstructor;
    private final int bufferSize = 4096;

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static ArchiveEntry newTarArchiveEntry(String name, long size) {
        TarArchiveEntry entry = new TarArchiveEntry(name);
        entry.setSize(size);
        return entry;
    }

    private static ArchiveEntry newCpioArchiveEntry(String name, long size) {
        CpioArchiveEntry entry = new CpioArchiveEntry(name);
        entry.setSize(size);
        return entry;
    }

    public void archive(String srcFile, String destFile) throws IOException {
        archive(new File(srcFile), new File(destFile));
    }

    public void archive(File srcFile, File destFile) throws IOException {
        try (ArchiveOutputStream outs = outputConstructor.newInstance(
                new FileOutputStream(destFile))) {
            archive(srcFile, outs, "");
            outs.flush();
        }
    }

    public void archive(File srcFile, ArchiveOutputStream outs, String basePath)
            throws IOException {
        if (srcFile.isDirectory()) {
            archiveDir(srcFile, outs, basePath);
        } else {
            archiveFile(srcFile, outs, basePath);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void archiveDir(File dir, ArchiveOutputStream outs, String basePath)
            throws IOException {
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            ArchiveEntry entry = entryConstructor.newInstance(basePath + dir.getName() + "/", 0);

            outs.putArchiveEntry(entry);
            outs.closeArchiveEntry();
            return;
        }

        for (File file : files) {
            archive(file, outs, basePath + dir.getName() + "/");
        }
    }

    public void archiveFile(File file, ArchiveOutputStream outs, String basePath)
            throws IOException {
        ArchiveEntry entry = entryConstructor.newInstance(basePath + file.getName(), file.length());
        outs.putArchiveEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = bis.read(data, 0, bufferSize)) != -1) {
                outs.write(data, 0, count);
            }
        }

        outs.closeArchiveEntry();
    }

    public void unarchive(String srcFile, String destFile) throws IOException, ArchiveException {
        unarchive(new File(srcFile), new File(destFile));
    }

    public void unarchive(File srcFile, File destFile) throws IOException, ArchiveException {
        try (ArchiveInputStream ins = inputConstructor.newInstance(
                new FileInputStream(srcFile))) {
            unarchive(destFile, ins);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void unarchive(File destFile, ArchiveInputStream ins) throws IOException {
        ArchiveEntry entry;
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
                unarchiveFile(dirFile, ins);
            }
        }
    }

    public void unarchiveFile(File destFile, ArchiveInputStream ins) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile))) {
            int count;
            byte[] data = new byte[bufferSize];
            while ((count = ins.read(data, 0, bufferSize)) != -1) {
                bos.write(data, 0, count);
            }
        }
    }

    @FunctionalInterface
    interface OutputConstructor {

        ArchiveOutputStream newInstance(OutputStream outs);
    }

    @FunctionalInterface
    interface InputConstructor {

        ArchiveInputStream newInstance(InputStream outs) throws ArchiveException;
    }

    @FunctionalInterface
    interface EntryConstructor {

        ArchiveEntry newInstance(String name, long size);
    }
}
