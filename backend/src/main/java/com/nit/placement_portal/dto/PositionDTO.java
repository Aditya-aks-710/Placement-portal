package com.nit.placement_portal.dto;

import java.util.ArrayList;
import java.util.List;

import com.nit.placement_portal.model.Position;
import com.nit.placement_portal.model.StudentCompany;

public class PositionDTO {

    private String id;
    private String title;
    private String type; // "internship" or "full-time"
    private String startDate;
    private String endDate;
    private String stipend;
    private String ctc;

    public PositionDTO() {
    }

    public static PositionDTO fromModel(Position p) {
        PositionDTO dto = new PositionDTO();
        dto.id = p.getId();
        dto.title = p.getTitle();
        dto.type = p.getType();
        dto.startDate = p.getStartDate();
        dto.endDate = p.getEndDate();
        dto.stipend = p.getStipend();
        dto.ctc = p.getCtc();
        return dto;
    }

    public Position toModel() {
        Position p = new Position();
        p.setId(id);
        p.setTitle(title);
        p.setType(type);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setStipend(stipend);
        p.setCtc(ctc);
        return p;
    }

    /**
     * Build the position timeline for a company record. When explicit positions are
     * stored they are used as-is; otherwise a sensible timeline is synthesized from
     * the legacy flat fields so existing data still renders as a timeline.
     */
    public static List<PositionDTO> deriveFrom(StudentCompany sc) {
        if (sc.getPositions() != null && !sc.getPositions().isEmpty()) {
            List<PositionDTO> result = new ArrayList<>();
            for (Position p : sc.getPositions()) {
                result.add(fromModel(p));
            }
            return result;
        }

        List<PositionDTO> derived = new ArrayList<>();
        boolean isInternship = "internship".equalsIgnoreCase(sc.getType());
        boolean hasStipend = notBlank(sc.getInternshipStipend());
        boolean hasFullTime = notBlank(sc.getFullTimePackage());
        // A two-phase intern -> full-time timeline only makes sense when the record
        // actually had an internship phase (a stipend) AND a full-time phase.
        boolean converted = hasStipend && hasFullTime;

        if (converted) {
            // Internship phase
            PositionDTO intern = new PositionDTO();
            intern.id = "p-intern";
            intern.title = sc.getRole();
            intern.type = "internship";
            intern.startDate = sc.getJoinDate();
            intern.endDate = sc.getConversionDate();
            intern.stipend = sc.getInternshipStipend();
            derived.add(intern);

            // Full-time phase
            PositionDTO ft = new PositionDTO();
            ft.id = "p-fulltime";
            ft.title = sc.getRole();
            ft.type = "full-time";
            ft.startDate = sc.getConversionDate();
            ft.endDate = sc.getEndDate();
            ft.ctc = sc.getFullTimePackage();
            derived.add(ft);
            return derived;
        }

        PositionDTO single = new PositionDTO();
        single.id = "p-0";
        single.title = sc.getRole();
        single.type = isInternship ? "internship" : "full-time";
        single.startDate = sc.getJoinDate();
        single.endDate = sc.getEndDate();
        if (isInternship || hasStipend) {
            single.stipend = sc.getInternshipStipend();
        } else {
            single.ctc = sc.getFullTimePackage();
        }
        derived.add(single);
        return derived;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }

    public String getCtc() {
        return ctc;
    }

    public void setCtc(String ctc) {
        this.ctc = ctc;
    }
}
