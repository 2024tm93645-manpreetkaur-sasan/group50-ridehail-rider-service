package com.rhf.rider.service;

import com.rhf.rider.entity.Rider;
import com.rhf.rider.repository.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RiderService {

    private final RiderRepository repo;

    public RiderService(RiderRepository repo) {
        this.repo = repo;
    }

    public List<Rider> getAll() {
        return repo.findAll();
    }

    public Optional<Rider> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<Rider> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Optional<Rider> findByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    public List<Rider> findByNameContainingIgnoreCase(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    public Rider save(Rider rider) {
        return repo.save(rider);
    }

    public Optional<Rider> update(Long id, Rider rider) {
        return repo.findById(id).map(existing -> {
            existing.setName(rider.getName());
            existing.setEmail(rider.getEmail());
            existing.setPhone(rider.getPhone());
            existing.setActive(rider.getActive());
            return repo.save(existing);
        });
    }

    public Optional<Rider> patch(Long id, Map<String, Object> fields) {
        return repo.findById(id).map(existing -> {
            fields.forEach((k, v) -> {
                switch (k) {
                    case "name" -> existing.setName((String) v);
                    case "email" -> existing.setEmail((String) v);
                    case "phone" -> existing.setPhone((String) v);
                    case "active" -> existing.setActive((Boolean) v);
                }
            });
            return repo.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
