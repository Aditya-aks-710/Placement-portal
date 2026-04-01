package com.nit.placement_portal.dto;

import java.util.List;

public class PublicStudentPageDTO {

    private List<PublicStudentDTO> students;
    private long total;
    private int page;
    private int size;
    private boolean hasMore;
    private long placedCount;
    private long unplacedCount;
    private long internshipCount;
    private long pendingCount;

    public List<PublicStudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<PublicStudentDTO> students) {
        this.students = students;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public long getPlacedCount() {
        return placedCount;
    }

    public void setPlacedCount(long placedCount) {
        this.placedCount = placedCount;
    }

    public long getUnplacedCount() {
        return unplacedCount;
    }

    public void setUnplacedCount(long unplacedCount) {
        this.unplacedCount = unplacedCount;
    }

    public long getInternshipCount() {
        return internshipCount;
    }

    public void setInternshipCount(long internshipCount) {
        this.internshipCount = internshipCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }
}
