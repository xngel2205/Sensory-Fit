
package com.unicesar.proyectogym.persistence.file;

import com.unicesar.proyectogym.model.Attendance;
import com.unicesar.proyectogym.interfaces.AttendanceDao;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class FileAsistenciaDao implements AttendanceDao {

    private static final Path DEFAULT_FILE = Paths.get("data", "asistencias.dat");

    private final FileStore<Attendance> store;
    private final List<Attendance> cache;

    public FileAsistenciaDao() {
        this(DEFAULT_FILE);
    }

    public FileAsistenciaDao(Path file) {
        this.store = new FileStore<>(file);
        this.cache = store.readAll();
    }

    @Override
    public synchronized void save(Attendance attendance) {
        cache.add(attendance);
        store.writeAll(cache);
    }

    @Override
    public synchronized List<Attendance> findAll() {
        List<Attendance> result = new ArrayList<>(cache);
        result.sort(Comparator.comparing(Attendance::getDateTime).reversed());
        return result;
    }

    @Override
    public synchronized List<Attendance> findByDocument(String document) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : cache) {
            if (Objects.equals(a.getDocumentUser(), document)) {
                result.add(a);
            }
        }
        result.sort(Comparator.comparing(Attendance::getDateTime).reversed());
        return result;
    }
}
