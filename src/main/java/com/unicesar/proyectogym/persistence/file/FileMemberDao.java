package com.unicesar.proyectogym.persistence.file;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.interfaces.MemberDao;
import com.unicesar.proyectogym.persistence.DataAccessException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileMemberDao implements MemberDao {

    private static final Logger LOG = Logger.getLogger(FileMemberDao.class.getName());
    private static final String DEFAULT_DIR = "data";
    private static final String DEFAULT_FILE = "members.dat";

    private final Path dataFile;

    private final Map<String, Member> cache = new LinkedHashMap<>();


    public FileMemberDao() {
        this(Paths.get(DEFAULT_DIR, DEFAULT_FILE));
    }

  
    public FileMemberDao(Path dataFile) {
        this.dataFile = dataFile;
        load();
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        File file = dataFile.toFile();
        if (!file.exists()) {
            LOG.log(Level.INFO, "No existe {0}; se iniciará con datos vacíos.", dataFile);
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(dataFile))) {
            Object obj = in.readObject();
            cache.clear();
            if (obj instanceof List<?>) {
                for (Member m : (List<Member>) obj) {
                    if (m != null && m.getIdentification() != null) {
                        cache.put(m.getIdentification(), m);
                    }
                }
            }
            LOG.log(Level.INFO, "Cargados {0} usuarios desde {1}",
                    new Object[]{cache.size(), dataFile});
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(
                    "No se pudo leer el archivo de datos: " + dataFile, ex);
        }
    }

    private synchronized void flush() {
        try {
            Path dir = dataFile.toAbsolutePath().getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            Path tmp = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tmp))) {
                out.writeObject(new ArrayList<>(cache.values()));
            }
            try {
                Files.move(tmp, dataFile,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicFail) {
                Files.move(tmp, dataFile,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new DataAccessException(
                    "No se pudo escribir el archivo de datos: " + dataFile, ex);
        }
    }


    @Override
    public synchronized void save(Member member) {
        if (member == null || member.getIdentification() == null
                || member.getIdentification().isBlank()) {
            throw new DataAccessException("El usuario debe tener una identificación válida.");
        }
        cache.put(member.getIdentification(), member);
        flush();
    }

    @Override
    public synchronized Optional<Member> findById(String identification) {
        if (identification == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(identification));
    }

    @Override
    public synchronized List<Member> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public synchronized boolean existsById(String identification) {
        return identification != null && cache.containsKey(identification);
    }

    @Override
    public synchronized boolean deleteById(String identification) {
        if (identification == null || !cache.containsKey(identification)) {
            return false;
        }
        cache.remove(identification);
        flush();
        return true;
    }
}
