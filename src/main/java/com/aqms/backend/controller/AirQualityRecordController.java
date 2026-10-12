package com.aqms.backend.controller;

import com.aqms.backend.exception.ResourceNotFoundException;
import com.aqms.backend.model.AirQualityRecord;
import com.aqms.backend.repository.AirQualityRecordRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class AirQualityRecordController {

    private final AirQualityRecordRepository repository;

    public AirQualityRecordController(AirQualityRecordRepository repository) {
        this.repository = repository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AirQualityRecord> create(@Valid @RequestBody AirQualityRecord record) {
        AirQualityRecord saved = repository.save(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // READ (all, with optional ?city= filter)
    @GetMapping
    public ResponseEntity<List<AirQualityRecord>> getAll(
            @RequestParam(required = false) String city) {
        List<AirQualityRecord> records = (city == null || city.isBlank())
                ? repository.findAll()
                : repository.findByCityIgnoreCaseContaining(city);
        return ResponseEntity.ok(records);
    }

    // READ (one)
    @GetMapping("/{id}")
    public ResponseEntity<AirQualityRecord> getById(@PathVariable Long id) {
        AirQualityRecord record = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id " + id));
        return ResponseEntity.ok(record);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<AirQualityRecord> update(@PathVariable Long id,
                                                     @Valid @RequestBody AirQualityRecord updated) {
        AirQualityRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id " + id));

        existing.setCity(updated.getCity());
        existing.setAqi(updated.getAqi());
        existing.setPm25(updated.getPm25());
        existing.setPm10(updated.getPm10());
        existing.setRemarks(updated.getRemarks());

        AirQualityRecord saved = repository.save(existing);
        return ResponseEntity.ok(saved);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        AirQualityRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id " + id));
        repository.delete(existing);
        return ResponseEntity.noContent().build();
    }

}
