package com.college.attendance.entity;

public enum AttendanceStatus {
    PRESENT,    // Both check-in and check-out completed within time windows
    ABSENT,     // No check-in or check-out, or outside time windows
    PARTIAL     // Only check-in or check-out completed
}