package com.rhf.rider.controller;

import com.rhf.rider.entity.Rider;
import com.rhf.rider.service.RiderService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/riders")
public class RiderController {

    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @GetMapping
    public List<Rider> getAll() {
        return riderService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rider> getById(@PathVariable Long id) {
        return riderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String phone) {
        if (email != null)
            return riderService.findByEmail(email)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        if (phone != null)
            return riderService.findByPhone(phone)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        if (name != null)
            return ResponseEntity.ok(riderService.findByNameContainingIgnoreCase(name));

        return ResponseEntity.badRequest().body("Provide name, email, or phone");
    }

    @PostMapping
    public ResponseEntity<Rider> create(@Valid @RequestBody Rider rider) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riderService.save(rider));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rider> update(@PathVariable Long id, @Valid @RequestBody Rider rider) {
        return riderService.update(id, rider)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Rider> patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return riderService.patch(id, fields)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = riderService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
