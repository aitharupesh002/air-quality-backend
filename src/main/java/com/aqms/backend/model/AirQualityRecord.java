package com.aqms.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality_records")
public class AirQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotNull(message = "AQI value is required")
    @Column(nullable = false)
    private Integer aqi;

    private Double pm25;

    private Double pm10;

    // Good, Moderate, Unhealthy for Sensitive Groups, Unhealthy, Very Unhealthy, Hazardous
    private String category;

    @Column(length = 500)
    private String remarks;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public AirQualityRecord() {
    }

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        if (category == null && aqi != null) {
            category = categorize(aqi);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (aqi != null) {
            category = categorize(aqi);
        }
    }

    private String categorize(int aqiValue) {
        if (aqiValue <= 50) return "Good";
        if (aqiValue <= 100) return "Moderate";
        if (aqiValue <= 150) return "Unhealthy for Sensitive Groups";
        if (aqiValue <= 200) return "Unhealthy";
        if (aqiValue <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
