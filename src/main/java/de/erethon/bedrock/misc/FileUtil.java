package de.erethon.bedrock.misc;

import de.erethon.bedrock.chat.MessageUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class FileUtil {

    /**
     * @param folder the folder to check
     * @return a List of Files inside the folder and its subfolders
     */
    public static List<File> getFilesForFolder(File folder) {
        List<File> files = new ArrayList<>();
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File \"" + folder.getName() + "\" is not a directory");
        }
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFilesForFolder(file));
            } else {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * @param folder the folder to check
     * @return a List of subfolders inside the folder.
     * @since 1.2.4
     */
    public static List<File> getSubFolders(File folder) {
        List<File> subFolders = new ArrayList<>();
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("File \"" + folder.getName() + "\" is not a directory");
        }
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                subFolders.add(file);
            }
        }
        return subFolders;
    }

    /**
     * Copies a file or directory
     *
     * @param inFile  the File to copy
     * @param outFile the target path
     */
    public static void copy(File inFile, File outFile) {
        if (inFile.isDirectory()) {
            copyDir(inFile, outFile);
        } else {
            copyFile(inFile, outFile);
        }
    }

    /**
     * @param inFile  the File to copy
     * @param outFile the target path
     * @return if the file has been copied correctly
     */
    public static boolean copyFile(File inFile, File outFile) {
        return org.bukkit.util.FileUtil.copy(inFile, outFile);
    }

    /**
     * @param inDir   the directory to copy
     * @param outDir  the target path
     * @param exclude files to exclude
     */
    public static void copyDir(File inDir, File outDir, String... exclude) {
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        for (String p : inDir.list()) {
            if (!Arrays.asList(exclude).contains(p)) {
                copy(new File(inDir, p), new File(outDir, p));
            }
        }
    }

    /**
     * @param dir the directory to remove
     * @return if the directory has been removed correctly
     */
    public static boolean removeDir(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (!removeDir(f)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * @param path a file path
     * @return the File
     */
    public static File createIfNotExisting(String path) {
        return createIfNotExisting(new File(path));
    }

    /**
     * @param file a file
     * @return the File
     */
    public static File createIfNotExisting(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Tries to find a resource for the given path and copies it into the specified file if found.
     *
     * @param plugin the plugin to get resource with
     * @param file the file to get resource for
     * @param resourcePath the path of the resource file
     * @return the final file
     */
    public static File initFile(Plugin plugin, File file, String resourcePath) {
        try {
            if (!file.exists()) {
                InputStream link = plugin.getResource(resourcePath);
                if (link != null) {
                    Files.copy(link, file.getAbsoluteFile().toPath());
                } else {
                    MessageUtil.log(plugin, "Couldn't copy " + file.getName() + " into the plugin folder");
                    MessageUtil.log(plugin, "Cause: Resource not found");
                }
            }
        } catch (IOException e) {
            MessageUtil.log(plugin, "Couldn't copy " + file.getName() + " into the plugin folder");
            MessageUtil.log(plugin, "Cause: " + e.getMessage());
        }
        return file;
    }

}
