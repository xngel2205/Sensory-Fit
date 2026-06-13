package com.unicesar.proyectogym.persistence.file;

import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.interfaces.MetaDao;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class FileMetaDao implements MetaDao {

    private static final Path DEFAULT_FILE = Paths.get("data", "metas.dat");

    private final FileStore<PhysicalGoal> store;
    private final List<PhysicalGoal> cache;

    public FileMetaDao() {
        this(DEFAULT_FILE);
    }

    public FileMetaDao(Path file) {
        this.store = new FileStore<>(file);
        this.cache = store.readAll();
    }

    @Override
    public synchronized void save(PhysicalGoal meta) {
        cache.add(meta);
        store.writeAll(cache);
    }

    @Override
    public synchronized void saveAll(List<PhysicalGoal> goals) {
        cache.clear();
        cache.addAll(goals);
        store.writeAll(cache);
    }

    @Override
    public synchronized List<PhysicalGoal> findByDocument(String document) {
        List<PhysicalGoal> result = new ArrayList<>();
        for (PhysicalGoal m : cache) {
            if (Objects.equals(m.getDocumentUser(), document)) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public synchronized Optional<PhysicalGoal> findActiveByDocument(String document) {
        return cache.stream()
                .filter(m -> Objects.equals(m.getDocumentUser(), document) && m.isActive())
                .findFirst();
    }

    @Override
    public synchronized List<PhysicalGoal> findAll() {
        return new ArrayList<>(cache);
    }
}
