package com.nit.placement_portal.service;

import com.nit.placement_portal.data.MockStudentsData;
import com.nit.placement_portal.dto.CompanyDTO;
import com.nit.placement_portal.dto.EducationDTO;
import com.nit.placement_portal.dto.PublicInterviewExperienceDTO;
import com.nit.placement_portal.dto.PublicInterviewRoundDTO;
import com.nit.placement_portal.dto.PublicStudentDTO;
import com.nit.placement_portal.model.Company;
import com.nit.placement_portal.model.Education;
import com.nit.placement_portal.model.InterviewExperience;
import com.nit.placement_portal.model.InterviewQuestion;
import com.nit.placement_portal.model.InterviewRound;
import com.nit.placement_portal.model.Skill;
import com.nit.placement_portal.model.Student;
import com.nit.placement_portal.model.StudentCompany;
import com.nit.placement_portal.model.StudentPlacement;
import com.nit.placement_portal.repository.CompanyRepository;
import com.nit.placement_portal.repository.EducationRepository;
import com.nit.placement_portal.repository.InterviewExperienceRepository;
import com.nit.placement_portal.repository.SkillRepository;
import com.nit.placement_portal.repository.StudentCompanyRepository;
import com.nit.placement_portal.repository.StudentPlacementRepository;
import com.nit.placement_portal.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StudentService {

    private static final Pattern BATCH_PATTERN = Pattern.compile("^(\\d{4})");

    private final StudentRepository studentRepository;
    private final StudentCompanyRepository studentCompanyRepository;
    private final StudentPlacementRepository studentPlacementRepository;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final InterviewExperienceRepository interviewExperienceRepository;
    private final CompanyRepository companyRepository;
    private final Map<String, MockStudentsData.StudentData> mockStudentsByName;

    public StudentService(
            StudentRepository studentRepository,
            StudentCompanyRepository studentCompanyRepository,
            StudentPlacementRepository studentPlacementRepository,
            SkillRepository skillRepository,
            EducationRepository educationRepository,
            InterviewExperienceRepository interviewExperienceRepository,
            CompanyRepository companyRepository
    ) {
        this.studentRepository = studentRepository;
        this.studentCompanyRepository = studentCompanyRepository;
        this.studentPlacementRepository = studentPlacementRepository;
        this.skillRepository = skillRepository;
        this.educationRepository = educationRepository;
        this.interviewExperienceRepository = interviewExperienceRepository;
        this.companyRepository = companyRepository;
        this.mockStudentsByName = buildMockStudentLookup();
    }

    public List<PublicStudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toPublicStudent)
                .toList();
    }

    public Student markStudentPending(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student Not Found"));

        student.setStatus("PENDING");
        return studentRepository.save(student);
    }

    public List<PublicStudentDTO> getStudentsByStatus(String status) {
        String normalizedFilter = normalizeStatus(status);

        return studentRepository.findAll().stream()
                .filter(student -> normalizeStatus(student.getStatus()).equals(normalizedFilter))
                .map(this::toPublicStudent)
                .toList();
    }

    private PublicStudentDTO toPublicStudent(Student student) {
        MockStudentsData.StudentData mock = findMockStudent(student);

        List<StudentCompany> studentCompanies = studentCompanyRepository.findByStudentId(student.getId());
        List<StudentPlacement> placements = studentPlacementRepository.findByStudentId(student.getId());
        List<Skill> skills = skillRepository.findByStudentId(student.getId());
        List<Education> educations = educationRepository.findByStudentId(student.getId());
        List<InterviewExperience> experiences = interviewExperienceRepository.findByStudentId(student.getId());

        CompanyDTO currentCompany = resolveCurrentCompany(student, studentCompanies, placements, mock);
        List<CompanyDTO> pastCompanies = resolvePastCompanies(studentCompanies, currentCompany, mock);
        List<String> skillNames = resolveSkills(skills, mock);
        List<EducationDTO> education = resolveEducation(educations, mock);
        List<PublicInterviewExperienceDTO> interviewExperiences = resolveInterviewExperiences(experiences, mock);

        PublicStudentDTO dto = new PublicStudentDTO();
        dto.setId(student.getId());
        dto.setRegNo(student.getRegNo());
        dto.setName(defaultString(student.getName(), safe(() -> mock.name)));
        dto.setBranch(defaultString(student.getBranch(), safe(() -> mock.branch), "Unknown"));
        dto.setStatus(normalizeStatus(defaultString(student.getStatus(), safe(() -> mock.status))));
        dto.setBatch(resolveBatch(student.getRegNo(), safe(() -> mock.batch)));

        dto.setEmail(defaultString(safe(() -> mock.email), ""));
        dto.setPhone(defaultString(safe(() -> mock.phone), ""));
        dto.setAvatar(defaultString(safe(() -> mock.avatar), student.getProfilePic(), ""));
        dto.setProfilePic(defaultString(student.getProfilePic(), safe(() -> mock.avatar), null));

        dto.setCurrentCompany(currentCompany);
        dto.setPastCompanies(pastCompanies);
        dto.setSkills(skillNames);
        dto.setEducation(education);
        dto.setInterviewExperiences(interviewExperiences);

        dto.setLinkedin(defaultString(safe(() -> mock.linkedin), null));
        dto.setGithub(defaultString(safe(() -> mock.github), null));
        dto.setBio(defaultString(safe(() -> mock.bio), ""));

        dto.setCompany(defaultString(
                currentCompany != null ? currentCompany.getName() : null,
                student.getCompany(),
                null
        ));
        dto.setCompanyLogo(defaultString(
                currentCompany != null ? currentCompany.getLogo() : null,
                student.getCompanyLogo(),
                null
        ));
        dto.setRole(defaultString(currentCompany != null ? currentCompany.getRole() : null, null));
        dto.setPackageValue(defaultString(currentCompany != null ? currentCompany.getPackageValue() : null, null));
        dto.setJoinDate(defaultString(currentCompany != null ? currentCompany.getJoinDate() : null, null));
        dto.setEndDate(defaultString(currentCompany != null ? currentCompany.getEndDate() : null, null));
        dto.setType(defaultString(currentCompany != null ? currentCompany.getType() : null, null));
        dto.setDuration(defaultString(currentCompany != null ? currentCompany.getDuration() : null, null));

        return dto;
    }

    private CompanyDTO resolveCurrentCompany(
            Student student,
            List<StudentCompany> studentCompanies,
            List<StudentPlacement> placements,
            MockStudentsData.StudentData mock
    ) {
        CompanyDTO current = null;

        Optional<StudentCompany> currentRecord = studentCompanies.stream()
                .filter(company -> isBlank(company.getEndDate()))
                .findFirst();

        if (currentRecord.isPresent()) {
            current = toCompanyDTO(currentRecord.get());
        } else if (!studentCompanies.isEmpty()) {
            current = toCompanyDTO(studentCompanies.get(0));
        } else if (!placements.isEmpty()) {
            current = toCompanyDTO(placements.get(0));
        } else if (safe(() -> mock.currentCompany) != null) {
            current = toCompanyDTO(mock.currentCompany);
        }

        if (current == null && !isBlank(student.getCompany())) {
            current = new CompanyDTO();
            current.setName(student.getCompany());
            current.setLogo(student.getCompanyLogo());
        }

        if (current != null) {
            current.setName(defaultString(current.getName(), student.getCompany(), null));
            current.setLogo(defaultString(current.getLogo(), student.getCompanyLogo(), null));
            current.setType(defaultString(current.getType(), inferCompanyType(student.getStatus()), "full-time"));
        }

        return current;
    }

    private List<CompanyDTO> resolvePastCompanies(
            List<StudentCompany> studentCompanies,
            CompanyDTO currentCompany,
            MockStudentsData.StudentData mock
    ) {
        List<CompanyDTO> past = new ArrayList<>();

        for (StudentCompany studentCompany : studentCompanies) {
            if (!isBlank(studentCompany.getEndDate())) {
                past.add(toCompanyDTO(studentCompany));
            }
        }

        if (past.isEmpty() && safe(() -> mock.pastCompanies) != null) {
            for (MockStudentsData.CompanyData companyData : mock.pastCompanies) {
                past.add(toCompanyDTO(companyData));
            }
        }

        if (currentCompany != null) {
            past.removeIf(company -> sameCompany(company, currentCompany));
        }

        return past;
    }

    private List<String> resolveSkills(List<Skill> skills, MockStudentsData.StudentData mock) {
        List<String> result = skills.stream()
                .map(Skill::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();

        if (!result.isEmpty()) {
            return result;
        }

        if (safe(() -> mock.skills) != null) {
            return mock.skills;
        }

        return List.of();
    }

    private List<EducationDTO> resolveEducation(List<Education> educations, MockStudentsData.StudentData mock) {
        List<EducationDTO> result = educations.stream()
                .map(this::toEducationDTO)
                .toList();

        if (!result.isEmpty()) {
            return result;
        }

        if (safe(() -> mock.education) != null) {
            return mock.education.stream()
                    .map(this::toEducationDTO)
                    .toList();
        }

        return List.of();
    }

    private List<PublicInterviewExperienceDTO> resolveInterviewExperiences(
            List<InterviewExperience> experiences,
            MockStudentsData.StudentData mock
    ) {
        List<PublicInterviewExperienceDTO> result = experiences.stream()
                .map(this::toInterviewExperienceDTO)
                .toList();

        if (!result.isEmpty()) {
            return result;
        }

        if (safe(() -> mock.interviewExperiences) != null) {
            return mock.interviewExperiences.stream()
                    .map(this::toInterviewExperienceDTO)
                    .toList();
        }

        return List.of();
    }

    private CompanyDTO toCompanyDTO(StudentCompany studentCompany) {
        CompanyDTO dto = new CompanyDTO();
        Company company = resolveCompany(studentCompany.getCompanyId());

        dto.setName(company != null ? company.getName() : null);
        dto.setLogo(company != null ? company.getLogoUrl() : null);
        dto.setRole(studentCompany.getRole());
        dto.setPackageValue(studentCompany.getPackageValue());
        dto.setJoinDate(studentCompany.getJoinDate());
        dto.setEndDate(studentCompany.getEndDate());
        dto.setType(studentCompany.getType());
        dto.setDuration(studentCompany.getDuration());
        return dto;
    }

    private CompanyDTO toCompanyDTO(StudentPlacement placement) {
        CompanyDTO dto = new CompanyDTO();
        dto.setName(placement.getCompany());
        dto.setLogo(placement.getCompanyLogo());
        dto.setRole(placement.getRole());
        dto.setPackageValue(formatCtc(placement.getCtc()));
        dto.setType(inferPlacementType(placement.getPlacementNature()));
        dto.setJoinDate(String.valueOf(placement.getPlacementYear()));
        return dto;
    }

    private CompanyDTO toCompanyDTO(MockStudentsData.CompanyData companyData) {
        CompanyDTO dto = new CompanyDTO();
        dto.setName(companyData.name);
        dto.setRole(companyData.role);
        dto.setPackageValue(companyData.packageValue);
        dto.setJoinDate(companyData.joinDate);
        dto.setEndDate(companyData.endDate);
        dto.setType(companyData.type);
        dto.setDuration(companyData.duration);
        return dto;
    }

    private EducationDTO toEducationDTO(Education education) {
        EducationDTO dto = new EducationDTO();
        dto.setDegree(education.getDegree());
        dto.setInstitution(education.getInstitution());
        dto.setYear(education.getYear());
        dto.setGrade(education.getGrade());
        return dto;
    }

    private EducationDTO toEducationDTO(MockStudentsData.EducationData educationData) {
        EducationDTO dto = new EducationDTO();
        dto.setDegree(educationData.degree);
        dto.setInstitution(educationData.institution);
        dto.setYear(educationData.year);
        dto.setGrade(educationData.grade);
        return dto;
    }

    private PublicInterviewExperienceDTO toInterviewExperienceDTO(InterviewExperience experience) {
        PublicInterviewExperienceDTO dto = new PublicInterviewExperienceDTO();
        dto.setCompany(experience.getCompanyName());
        dto.setOverallTips(experience.getOverallTips());
        dto.setDifficulty(experience.getDifficulty());
        dto.setRating(experience.getRating() == null ? null : Math.round(experience.getRating()));

        List<PublicInterviewRoundDTO> rounds = new ArrayList<>();
        if (experience.getRounds() != null) {
            for (InterviewRound round : experience.getRounds()) {
                rounds.add(toInterviewRoundDTO(round));
            }
        }
        dto.setRounds(rounds);
        return dto;
    }

    private PublicInterviewExperienceDTO toInterviewExperienceDTO(MockStudentsData.InterviewExperienceData experienceData) {
        PublicInterviewExperienceDTO dto = new PublicInterviewExperienceDTO();
        dto.setCompany(experienceData.company);
        dto.setOverallTips(experienceData.overallTips);
        dto.setDifficulty(experienceData.difficulty);
        dto.setRating(experienceData.rating);

        List<PublicInterviewRoundDTO> rounds = new ArrayList<>();
        if (experienceData.rounds != null) {
            for (MockStudentsData.InterviewRoundData roundData : experienceData.rounds) {
                PublicInterviewRoundDTO round = new PublicInterviewRoundDTO();
                round.setName(roundData.name);
                round.setDescription(roundData.description);
                round.setTips(roundData.tips);
                rounds.add(round);
            }
        }
        dto.setRounds(rounds);
        return dto;
    }

    private PublicInterviewRoundDTO toInterviewRoundDTO(InterviewRound round) {
        PublicInterviewRoundDTO dto = new PublicInterviewRoundDTO();
        dto.setName(defaultString(round.getRoundType(), "Interview Round"));

        String description = "Details not available.";
        if (!isBlank(round.getMonth()) || !isBlank(round.getYear())) {
            description = "Conducted in "
                    + defaultString(round.getMonth(), "unknown month")
                    + " "
                    + defaultString(round.getYear(), "unknown year")
                    + ".";
        }

        if (round.getQuestions() != null && !round.getQuestions().isEmpty()) {
            description = "Covers " + round.getQuestions().size() + " question(s).";
        }

        dto.setDescription(description);
        dto.setTips(resolveTips(round.getQuestions()));
        return dto;
    }

    private String resolveTips(List<InterviewQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            return "Prepare fundamentals and communicate your approach clearly.";
        }

        InterviewQuestion first = questions.get(0);
        if (!isBlank(first.getQuestion())) {
            return first.getQuestion();
        }

        if (!isBlank(first.getLink())) {
            return "Practice from: " + first.getLink();
        }

        return "Prepare fundamentals and communicate your approach clearly.";
    }

    private Company resolveCompany(String companyId) {
        if (isBlank(companyId)) {
            return null;
        }
        return companyRepository.findById(companyId).orElse(null);
    }

    private boolean sameCompany(CompanyDTO a, CompanyDTO b) {
        return equalsIgnoreCase(a.getName(), b.getName())
                && equalsIgnoreCase(a.getRole(), b.getRole())
                && equalsIgnoreCase(a.getJoinDate(), b.getJoinDate());
    }

    private String resolveBatch(String regNo, String mockBatch) {
        if (!isBlank(mockBatch)) {
            return mockBatch;
        }

        if (isBlank(regNo)) {
            return "Unknown";
        }

        Matcher matcher = BATCH_PATTERN.matcher(regNo.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    private String formatCtc(double ctc) {
        if (ctc <= 0) {
            return null;
        }
        if (ctc >= 1) {
            return String.format(Locale.ROOT, "%.1f LPA", ctc);
        }
        return String.format(Locale.ROOT, "%.0f K", ctc * 100);
    }

    private String inferPlacementType(String placementNature) {
        String normalized = normalizeStatus(placementNature);
        if ("INTERNSHIP".equals(normalized)) {
            return "internship";
        }
        return "full-time";
    }

    private String inferCompanyType(String status) {
        String normalized = normalizeStatus(status);
        if ("INTERNSHIP".equals(normalized)) {
            return "internship";
        }
        return "full-time";
    }

    private String normalizeStatus(String status) {
        if (isBlank(status)) {
            return "UNPLACED";
        }

        String value = status.trim().toUpperCase(Locale.ROOT);
        if ("PLACE".equals(value)) {
            return "PLACED";
        }
        if ("INTERN".equals(value)) {
            return "INTERNSHIP";
        }
        return value;
    }

    private Map<String, MockStudentsData.StudentData> buildMockStudentLookup() {
        Map<String, MockStudentsData.StudentData> lookup = new LinkedHashMap<>();

        for (MockStudentsData.StudentData studentData : Arrays.asList(MockStudentsData.getMockStudents())) {
            if (studentData == null || isBlank(studentData.name)) {
                continue;
            }
            lookup.put(normalizeKey(studentData.name), studentData);
        }

        return lookup;
    }

    private MockStudentsData.StudentData findMockStudent(Student student) {
        if (student == null || isBlank(student.getName())) {
            return null;
        }
        return mockStudentsByName.get(normalizeKey(student.getName()));
    }

    private String normalizeKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultString(String... candidates) {
        for (String candidate : candidates) {
            if (!isBlank(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private <T> T safe(Supplier<T> supplier) {
        try {
            return supplier == null ? null : supplier.get();
        } catch (NullPointerException ignored) {
            return null;
        }
    }
}
