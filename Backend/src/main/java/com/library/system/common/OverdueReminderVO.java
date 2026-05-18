package com.library.system.common;

import java.util.List;

import com.library.system.entity.BorrowRecord;

/**
 * 逾期提醒汇总（供首页、借阅页展示）
 */
public class OverdueReminderVO {
    private int overdueCount;
    private List<BorrowRecord> records;

    public OverdueReminderVO() {
    }

    public OverdueReminderVO(int overdueCount, List<BorrowRecord> records) {
        this.overdueCount = overdueCount;
        this.records = records;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public List<BorrowRecord> getRecords() {
        return records;
    }

    public void setRecords(List<BorrowRecord> records) {
        this.records = records;
    }
}
