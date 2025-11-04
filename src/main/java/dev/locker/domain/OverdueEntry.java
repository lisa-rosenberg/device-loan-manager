package dev.locker.domain;

public class OverdueEntry {
    public String deviceId;
    public String userId;
    public long daysOverdue;
    public double fee;
    // optional extra
    public String deviceName;

    public OverdueEntry() {
    }

    public OverdueEntry(String deviceId, String userId, long daysOverdue, double fee, String deviceName) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.daysOverdue = daysOverdue;
        this.fee = fee;
        this.deviceName = deviceName;
    }
}

