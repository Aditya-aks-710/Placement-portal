package com.nit.placement_portal.data;

import java.util.Arrays;
import java.util.List;

public class MockStudentsData {

    public static class CompanyData {
        public String name;
        public String role;
        public String packageValue;
        public String joinDate;
        public String endDate;
        public String type;
        public String duration;
    }

    public static class InterviewRoundData {
        public String name;
        public String description;
        public String tips;
    }

    public static class InterviewExperienceData {
        public String company;
        public List<InterviewRoundData> rounds;
        public String overallTips;
        public String difficulty;
        public int rating;
    }

    public static class EducationData {
        public String degree;
        public String institution;
        public String year;
        public String grade;
    }

    public static class StudentData {
        public String name;
        public String email;
        public String phone;
        public String avatar;
        public String branch;
        public String batch;
        public String status;
        public CompanyData currentCompany;
        public List<CompanyData> pastCompanies;
        public List<InterviewExperienceData> interviewExperiences;
        public List<EducationData> education;
        public List<String> skills;
        public String linkedin;
        public String github;
        public String bio;
    }

    public static StudentData[] getMockStudents() {
        StudentData[] students = new StudentData[10];

        // Student 1: Arjun Mehta
        students[0] = new StudentData();
        students[0].name = "Arjun Mehta";
        students[0].email = "arjun.mehta@college.edu";
        students[0].phone = "+91 98765 43210";
        students[0].avatar = "";
        students[0].branch = "Computer Science";
        students[0].batch = "2024";
        students[0].status = "placed";
        students[0].currentCompany = new CompanyData();
        students[0].currentCompany.name = "Google";
        students[0].currentCompany.role = "Software Engineer";
        students[0].currentCompany.packageValue = "₹45 LPA";
        students[0].currentCompany.joinDate = "Jul 2024";
        students[0].currentCompany.type = "full-time";
        students[0].pastCompanies = Arrays.asList(
            createCompanyData("Microsoft", "SDE Intern", "₹80K/month", "May 2023", "Jul 2023", "internship", "3 months")
        );
        students[0].interviewExperiences = Arrays.asList(
            createInterviewExperience("Google", Arrays.asList(
                createInterviewRound("Online Assessment", "2 DSA problems on HackerRank. Medium-Hard level.", "Practice graph and DP problems."),
                createInterviewRound("Technical Round 1", "System design for URL shortener and 1 coding problem.", "Think aloud, communicate your approach."),
                createInterviewRound("Technical Round 2", "Advanced DSA + OS concepts.", "Brush up on OS and networking basics."),
                createInterviewRound("HR Round", "Behavioral questions about teamwork and leadership.", "Use STAR method for answers.")
            ), "Focus on DSA fundamentals. Google values problem-solving approach over just getting the answer.", "Hard", 5)
        );
        students[0].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2020-2024", "9.2 CGPA"),
            createEducationData("12th CBSE", "Delhi Public School", "2020", "95.4%"),
            createEducationData("10th CBSE", "Delhi Public School", "2018", "96.2%")
        );
        students[0].skills = Arrays.asList("Java", "Python", "React", "System Design", "DSA", "Machine Learning");
        students[0].linkedin = "linkedin.com/in/arjunmehta";
        students[0].github = "github.com/arjunmehta";
        students[0].bio = "Passionate software engineer with expertise in distributed systems and full-stack development.";

        // Student 2: Priya Sharma
        students[1] = new StudentData();
        students[1].name = "Priya Sharma";
        students[1].email = "priya.sharma@college.edu";
        students[1].phone = "+91 98765 43211";
        students[1].avatar = "";
        students[1].branch = "Computer Science";
        students[1].batch = "2024";
        students[1].status = "placed";
        students[1].currentCompany = new CompanyData();
        students[1].currentCompany.name = "Amazon";
        students[1].currentCompany.role = "SDE-1";
        students[1].currentCompany.packageValue = "₹38 LPA";
        students[1].currentCompany.joinDate = "Aug 2024";
        students[1].currentCompany.type = "full-time";
        students[1].pastCompanies = Arrays.asList();
        students[1].interviewExperiences = Arrays.asList(
            createInterviewExperience("Amazon", Arrays.asList(
                createInterviewRound("Online Assessment", "2 coding problems + work simulation.", "Amazon focuses on leadership principles."),
                createInterviewRound("Technical Round", "Coding + system design basics.", "Know your projects well."),
                createInterviewRound("Bar Raiser", "Deep behavioral + technical mix.", "Prepare STAR stories for all LPs.")
            ), "Amazon heavily weighs leadership principles. Prepare stories for each one.", "Medium", 4)
        );
        students[1].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2020-2024", "8.8 CGPA"),
            createEducationData("12th CBSE", "Kendriya Vidyalaya", "2020", "93.2%")
        );
        students[1].skills = Arrays.asList("C++", "Java", "AWS", "React", "Node.js");
        students[1].linkedin = "linkedin.com/in/priyasharma";
        students[1].bio = "Full-stack developer passionate about cloud computing and scalable systems.";

        // Student 3: Rahul Verma
        students[2] = new StudentData();
        students[2].name = "Rahul Verma";
        students[2].email = "rahul.verma@college.edu";
        students[2].phone = "+91 98765 43212";
        students[2].avatar = "";
        students[2].branch = "Electronics";
        students[2].batch = "2024";
        students[2].status = "placed";
        students[2].currentCompany = new CompanyData();
        students[2].currentCompany.name = "Microsoft";
        students[2].currentCompany.role = "Software Engineer";
        students[2].currentCompany.packageValue = "₹42 LPA";
        students[2].currentCompany.joinDate = "Jul 2024";
        students[2].currentCompany.type = "full-time";
        students[2].pastCompanies = Arrays.asList(
            createCompanyData("Adobe", "Research Intern", "₹60K/month", "Jan 2023", "Apr 2023", "internship", "4 months")
        );
        students[2].interviewExperiences = Arrays.asList(
            createInterviewExperience("Microsoft", Arrays.asList(
                createInterviewRound("Online Test", "3 coding problems on Codility.", "Focus on efficiency."),
                createInterviewRound("Group Fly Round", "Solve a problem on paper in 45 min.", "Write clean, structured code."),
                createInterviewRound("Technical Interview", "Deep dive into DSA and system design.", "Practice trees and graphs.")
            ), "Microsoft values clean code and clear communication.", "Hard", 4)
        );
        students[2].education = Arrays.asList(
            createEducationData("B.Tech Electronics", "IIT Delhi", "2020-2024", "9.0 CGPA")
        );
        students[2].skills = Arrays.asList("Python", "C++", "Azure", "Machine Learning", "Signal Processing");
        students[2].bio = "Electronics engineer turned software developer with a keen interest in AI/ML.";

        // Student 4: Sneha Patel
        students[3] = new StudentData();
        students[3].name = "Sneha Patel";
        students[3].email = "sneha.patel@college.edu";
        students[3].phone = "+91 98765 43213";
        students[3].avatar = "";
        students[3].branch = "Computer Science";
        students[3].batch = "2024";
        students[3].status = "internship";
        students[3].currentCompany = new CompanyData();
        students[3].currentCompany.name = "Flipkart";
        students[3].currentCompany.role = "SDE Intern";
        students[3].currentCompany.packageValue = "₹75K/month";
        students[3].currentCompany.joinDate = "Jan 2024";
        students[3].currentCompany.type = "internship";
        students[3].currentCompany.duration = "6 months";
        students[3].pastCompanies = Arrays.asList();
        students[3].interviewExperiences = Arrays.asList(
            createInterviewExperience("Flipkart", Arrays.asList(
                createInterviewRound("Coding Test", "3 problems on HackerEarth.", "Time management is key."),
                createInterviewRound("Technical Interview", "DSA + DBMS concepts.", "Revise SQL queries and normalization.")
            ), "Flipkart values practical problem-solving skills.", "Medium", 4)
        );
        students[3].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2020-2024", "8.5 CGPA")
        );
        students[3].skills = Arrays.asList("Java", "Spring Boot", "React", "MySQL", "Docker");
        students[3].bio = "Backend developer with a passion for building scalable microservices.";

        // Student 5: Vikram Singh
        students[4] = new StudentData();
        students[4].name = "Vikram Singh";
        students[4].email = "vikram.singh@college.edu";
        students[4].phone = "+91 98765 43214";
        students[4].avatar = "";
        students[4].branch = "Mechanical";
        students[4].batch = "2024";
        students[4].status = "unplaced";
        students[4].pastCompanies = Arrays.asList();
        students[4].interviewExperiences = Arrays.asList();
        students[4].education = Arrays.asList(
            createEducationData("B.Tech Mechanical", "IIT Delhi", "2020-2024", "7.8 CGPA")
        );
        students[4].skills = Arrays.asList("AutoCAD", "SolidWorks", "Python", "MATLAB");
        students[4].bio = "Mechanical engineering student exploring opportunities in core and IT sectors.";

        // Student 6: Ananya Gupta
        students[5] = new StudentData();
        students[5].name = "Ananya Gupta";
        students[5].email = "ananya.gupta@college.edu";
        students[5].phone = "+91 98765 43215";
        students[5].avatar = "";
        students[5].branch = "Computer Science";
        students[5].batch = "2024";
        students[5].status = "placed";
        students[5].currentCompany = new CompanyData();
        students[5].currentCompany.name = "Goldman Sachs";
        students[5].currentCompany.role = "Analyst";
        students[5].currentCompany.packageValue = "₹35 LPA";
        students[5].currentCompany.joinDate = "Jul 2024";
        students[5].currentCompany.type = "full-time";
        students[5].pastCompanies = Arrays.asList(
            createCompanyData("Morgan Stanley", "Summer Analyst", "₹1.5L/month", "May 2023", "Jul 2023", "internship", "2 months")
        );
        students[5].interviewExperiences = Arrays.asList(
            createInterviewExperience("Goldman Sachs", Arrays.asList(
                createInterviewRound("HackerRank Test", "5 MCQs + 2 coding problems.", "Focus on core CS concepts."),
                createInterviewRound("Technical Round", "DSA, DBMS, and OOPs.", "Be thorough with DBMS normalization."),
                createInterviewRound("HR Round", "Why finance? Career goals.", "Research the company well.")
            ), "Goldman values strong fundamentals and a genuine interest in finance.", "Medium", 4)
        );
        students[5].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2020-2024", "9.1 CGPA")
        );
        students[5].skills = Arrays.asList("Java", "Python", "SQL", "Data Analysis", "Financial Modeling");
        students[5].linkedin = "linkedin.com/in/ananyagupta";
        students[5].bio = "Aspiring quant with a blend of CS and finance expertise.";

        // Student 7: Karan Joshi
        students[6] = new StudentData();
        students[6].name = "Karan Joshi";
        students[6].email = "karan.joshi@college.edu";
        students[6].phone = "+91 98765 43216";
        students[6].avatar = "";
        students[6].branch = "Information Technology";
        students[6].batch = "2024";
        students[6].status = "placed";
        students[6].currentCompany = new CompanyData();
        students[6].currentCompany.name = "Infosys";
        students[6].currentCompany.role = "Systems Engineer";
        students[6].currentCompany.packageValue = "₹8 LPA";
        students[6].currentCompany.joinDate = "Sep 2024";
        students[6].currentCompany.type = "full-time";
        students[6].pastCompanies = Arrays.asList();
        students[6].interviewExperiences = Arrays.asList(
            createInterviewExperience("Infosys", Arrays.asList(
                createInterviewRound("InfyTQ Test", "Aptitude + coding on InfyTQ platform.", "Practice aptitude regularly."),
                createInterviewRound("Interview", "HR + light technical questions.", "Be confident and clear.")
            ), "Infosys process is straightforward. Focus on aptitude and communication.", "Easy", 3)
        );
        students[6].education = Arrays.asList(
            createEducationData("B.Tech IT", "IIT Delhi", "2020-2024", "8.2 CGPA")
        );
        students[6].skills = Arrays.asList("Java", "SQL", "HTML/CSS", "JavaScript");
        students[6].bio = "IT enthusiast looking to grow in enterprise software development.";

        // Student 8: Meera Nair
        students[7] = new StudentData();
        students[7].name = "Meera Nair";
        students[7].email = "meera.nair@college.edu";
        students[7].phone = "+91 98765 43217";
        students[7].avatar = "";
        students[7].branch = "Computer Science";
        students[7].batch = "2024";
        students[7].status = "placed";
        students[7].currentCompany = new CompanyData();
        students[7].currentCompany.name = "Adobe";
        students[7].currentCompany.role = "Member of Technical Staff";
        students[7].currentCompany.packageValue = "₹40 LPA";
        students[7].currentCompany.joinDate = "Jul 2024";
        students[7].currentCompany.type = "full-time";
        students[7].pastCompanies = Arrays.asList();
        students[7].interviewExperiences = Arrays.asList(
            createInterviewExperience("Adobe", Arrays.asList(
                createInterviewRound("Online Test", "Aptitude + 3 coding problems.", "Adobe loves creative problem solvers."),
                createInterviewRound("Technical Round 1", "DSA focus with live coding.", "Think about edge cases."),
                createInterviewRound("Technical Round 2", "System design + projects discussion.", "Know your projects inside out.")
            ), "Adobe values creativity and innovation. Show your passion for building products.", "Hard", 5)
        );
        students[7].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2020-2024", "9.4 CGPA")
        );
        students[7].skills = Arrays.asList("C++", "Python", "React", "TypeScript", "UI/UX Design");
        students[7].linkedin = "linkedin.com/in/meeranair";
        students[7].github = "github.com/meeranair";
        students[7].bio = "Creative technologist passionate about building beautiful, user-centric products.";

        // Student 9: Aditya Kumar
        students[8] = new StudentData();
        students[8].name = "Aditya Kumar";
        students[8].email = "aditya.kumar@college.edu";
        students[8].phone = "+91 98765 43218";
        students[8].avatar = "";
        students[8].branch = "Electrical";
        students[8].batch = "2024";
        students[8].status = "unplaced";
        students[8].pastCompanies = Arrays.asList();
        students[8].interviewExperiences = Arrays.asList();
        students[8].education = Arrays.asList(
            createEducationData("B.Tech Electrical", "IIT Delhi", "2020-2024", "7.5 CGPA")
        );
        students[8].skills = Arrays.asList("MATLAB", "Python", "Circuit Design", "Embedded Systems");
        students[8].bio = "Electrical engineering student with interest in embedded systems and IoT.";

        // Student 10: Riya Kapoor
        students[9] = new StudentData();
        students[9].name = "Riya Kapoor";
        students[9].email = "riya.kapoor@college.edu";
        students[9].phone = "+91 98765 43219";
        students[9].avatar = "";
        students[9].branch = "Computer Science";
        students[9].batch = "2023";
        students[9].status = "placed";
        students[9].currentCompany = new CompanyData();
        students[9].currentCompany.name = "Uber";
        students[9].currentCompany.role = "Software Engineer";
        students[9].currentCompany.packageValue = "₹48 LPA";
        students[9].currentCompany.joinDate = "Aug 2023";
        students[9].currentCompany.type = "full-time";
        students[9].pastCompanies = Arrays.asList(
            createCompanyData("Google", "STEP Intern", "₹1.2L/month", "May 2022", "Jul 2022", "internship", "3 months")
        );
        students[9].interviewExperiences = Arrays.asList(
            createInterviewExperience("Uber", Arrays.asList(
                createInterviewRound("Phone Screen", "1 medium coding problem.", "Practice string and array problems."),
                createInterviewRound("Onsite Round 1", "System design for ride-matching.", "Study real-time systems."),
                createInterviewRound("Onsite Round 2", "DSA + behavioral.", "Be structured in approach.")
            ), "Uber values practical engineering skills. Think about scale and reliability.", "Hard", 5)
        );
        students[9].education = Arrays.asList(
            createEducationData("B.Tech Computer Science", "IIT Delhi", "2019-2023", "9.3 CGPA")
        );
        students[9].skills = Arrays.asList("Go", "Python", "Kubernetes", "System Design", "Distributed Systems");
        students[9].linkedin = "linkedin.com/in/riyakapoor";
        students[9].bio = "Systems engineer with expertise in distributed computing and microservices.";

        return students;
    }

    private static CompanyData createCompanyData(String name, String role, String packageValue, String joinDate, String endDate, String type, String duration) {
        CompanyData data = new CompanyData();
        data.name = name;
        data.role = role;
        data.packageValue = packageValue;
        data.joinDate = joinDate;
        data.endDate = endDate;
        data.type = type;
        data.duration = duration;
        return data;
    }

    private static InterviewRoundData createInterviewRound(String name, String description, String tips) {
        InterviewRoundData round = new InterviewRoundData();
        round.name = name;
        round.description = description;
        round.tips = tips;
        return round;
    }

    private static InterviewExperienceData createInterviewExperience(String company, List<InterviewRoundData> rounds, String overallTips, String difficulty, int rating) {
        InterviewExperienceData data = new InterviewExperienceData();
        data.company = company;
        data.rounds = rounds;
        data.overallTips = overallTips;
        data.difficulty = difficulty;
        data.rating = rating;
        return data;
    }

    private static EducationData createEducationData(String degree, String institution, String year, String grade) {
        EducationData data = new EducationData();
        data.degree = degree;
        data.institution = institution;
        data.year = year;
        data.grade = grade;
        return data;
    }
}
