package com.unicesar.proyectogym.persistence.file;

import com.unicesar.proyectogym.persistence.DataAccessException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileStore<T extends Serializable> {

    private static final Logger LOG = Logger.getLogger(FileStore.class.getName());

    private final Path file;

    public FileStore(Path file) {
        this.file = file;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<T> readAll() {
        if (!Files.exists(file)) {
            LOG.log(Level.INFO, "No existe {0}; se iniciará con datos vacíos.", file);
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return new ArrayList<>((List<T>) obj);
            }
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException("No se pudo leer el archivo: " + file, ex);
        }
    }

    public synchronized void writeAll(List<T> data) {
        try {
            Path dir = file.toAbsolutePath().getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tmp))) {
                out.writeObject(new ArrayList<>(data));
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFail) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new DataAccessException("No se pudo escribir el archivo: " + file, ex);
        }
    }
}
