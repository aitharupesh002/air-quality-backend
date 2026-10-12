package com.aqms.backend.repository;

import com.aqms.backend.model.AirQualityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirQualityRecordRepository extends JpaRepository<AirQualityRecord, Long> {

    List<AirQualityRecord> findByCityIgnoreCaseContaining(String city);

}
