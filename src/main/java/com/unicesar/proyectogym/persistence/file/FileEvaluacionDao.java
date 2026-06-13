
package com.unicesar.proyectogym.persistence.file;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.interfaces.EvaluationDao;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class FileEvaluacionDao implements EvaluationDao {

    private static final Path DEFAULT_FILE = Paths.get("data", "evaluaciones.dat");

    private final FileStore<PhysicalEvaluation> store;
    private final List<PhysicalEvaluation> cache;

    public FileEvaluacionDao() {
        this(DEFAULT_FILE);
    }

    public FileEvaluacionDao(Path file) {
        this.store = new FileStore<>(file);
        this.cache = store.readAll();
    }

    @Override
    public synchronized void save(PhysicalEvaluation e) {
        Objects.requireNonNull(e, "evaluación nula");
        cache.removeIf(x -> sameKey(x, e.getDocumentUser(), e.getDate()));
        cache.add(e);
        store.writeAll(cache);
    }

    @Override
    public synchronized List<PhysicalEvaluation> findByDocument(String document) {
        List<PhysicalEvaluation> result = new ArrayList<>();
        for (PhysicalEvaluation e : cache) {
            if (Objects.equals(e.getDocumentUser(), document)) {
                result.add(e);
            }
        }
        result.sort(Comparator.comparing(PhysicalEvaluation::getDate));
        return result;
    }

    @Override
    public synchronized List<PhysicalEvaluation> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized boolean existsByDocumentAndDate(String document, LocalDate date) {
        return cache.stream().anyMatch(x -> sameKey(x, document, date));
    }

    @Override
    public synchronized boolean delete(String document, LocalDate date) {
        boolean removed = cache.removeIf(x -> sameKey(x, document, date));
        if (removed) {
            store.writeAll(cache);
        }
        return removed;
    }

    private boolean sameKey(PhysicalEvaluation e, String document, LocalDate date) {
        return Objects.equals(e.getDocumentUser(), document)
                && Objects.equals(e.getDate(), date);
    }
}
