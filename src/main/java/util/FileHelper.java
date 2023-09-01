package util;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class FileHelper {

    /**
     * This method creates a new folder if this folder does not exist
     * @param file The file of the Folder that should be created
     * @return If the folder was created successfully true else false
     */
    public static boolean createDir(final @NonNull File file) {
        if (isFileExisting(file) || file == null)
            return false;
        return file.mkdir();
    }

    /**
     * This method creates a File from Path and then calls {@link #createNewFile(File)}
     * @param path The String of the file that should be created
     * @return {@link #createNewFile(File) If the File was successfully created}
     */
    public static boolean createNewFile(final @NonNull String path) {
        return createNewFile(getFile(path));
    }

    /**
     * This method creates a File out of an <Code>File</Code> object
     * @param file The file can be null, but will return false else if is not already existing there will be created
     *             a new one
     * @return When the file was successfully created true, else false
     */
    public static boolean createNewFile(final @Nullable File file) {
        if (isFileExisting(file) || file == null)
            return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * This methods checks if a file is existing.
     * @param file Can be null, the file that should be checked
     * @return When File is existing then true else false
     */
    public static boolean isFileExisting(final @Nullable File file) {
        if (file == null)
            return false;
        return file.exists();
    }

    /**
     * This methods gets a File out of a String
     * @param path The path as String that is pointing to the file. If the path is null then there will throwed a
     *              {@link NullPointerException NullPointerException} by the {@link File File class}
     * @return A File object with the parameter as path
     */
    public static @NonNull File getFile(final @NonNull String path) {
        System.out.println(Bukkit.getPluginsFolder().getPath());
        return new File(Bukkit.getPluginsFolder().getPath() + "/" + path);
    }
}
