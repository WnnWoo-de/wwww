package com.example.model;

import java.time.LocalDateTime;

public class Registration {
    private Long id;
    private Long userId;
    private Long activityId;
    private String status; // 'registered', 'attended', 'cancelled'
    private LocalDateTime registrationTime;
    private String notes;

    // 默认构造函数
    public Registration() {}

    // 带参数构造函数
    public Registration(Long userId, Long activityId, String status) {
        this.userId = userId;
        this.activityId = activityId;
        this.status = status;
        this.registrationTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", userId=" + userId +
                ", activityId=" + activityId +
                ", status='" + status + '\'' +
                ", registrationTime=" + registrationTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}