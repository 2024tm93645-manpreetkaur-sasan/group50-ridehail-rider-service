package com.rhf.rider.controller;

import com.rhf.rider.entity.Rider;
import com.rhf.rider.repository.RiderRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/v1/riders")
public class RiderController {

    private final RiderRepository repo;

    public RiderController(RiderRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Rider> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Rider> getById(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String phone) {
        if (email != null) return repo.findByEmail(email).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        if (phone != null) return repo.findByPhone(phone).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        if (name != null) return ResponseEntity.ok(repo.findByNameContainingIgnoreCase(name));
        return ResponseEntity.badRequest().body("Provide name, email, or phone");
    }

    @PostMapping
    public ResponseEntity<Rider> create(@RequestBody Rider rider) {
        Rider saved = repo.save(rider);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rider> update(@PathVariable Long id, @RequestBody Rider rider) {
        return repo.findById(id).map(existing -> {
            existing.setName(rider.getName());
            existing.setEmail(rider.getEmail());
            existing.setPhone(rider.getPhone());
            existing.setActive(rider.getActive());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Rider> patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return repo.findById(id).map(existing -> {
            fields.forEach((k, v) -> {
                switch (k) {
                    case "name" -> existing.setName((String) v);
                    case "email" -> existing.setEmail((String) v);
                    case "phone" -> existing.setPhone((String) v);
                    case "active" -> existing.setActive((Boolean) v);
                }
            });
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repo.existsById(id)) { repo.deleteById(id); return ResponseEntity.noContent().build(); }
        return ResponseEntity.notFound().build();
    }
}
