import java.util.*;

// user info
class User {
    private int id;
    private String name;
    private String role;
    private String email;

    public User(int id, String name, String role, String email) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }

    public void display() {
        System.out.println("User[ID=" + id + ", Name=" + name + ", Role=" + role + ", Email=" + email + "]");
    }
}

// manager is also a user
class Manager extends User {
    public Manager(int id, String name, String email) {
        super(id, name, "Manager", email);
    }

    @Override
    public void display() {
        System.out.println("Manager: " + getName() + " (Email: " + getEmail() + ")");
    }

    public void approveIssue(Issue issue) {
        System.out.println("Manager " + getName() + " approved issue: " + issue.getTitle());
    }
}

// base issue class
class Issue {
    private int issueId;
    private String title;
    private String description;
    private String severity;
    private String status;
    private User assignee;

    public Issue(int issueId, String title, String description, String severity) {
        this.issueId = issueId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = "NEW";
    }

    public int getIssueId() { return issueId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSeverity() { return severity; }
    public String getStatus() { return status; }
    public User getAssignee() { return assignee; }

    public void assignTo(User user) {
        this.assignee = user;
        System.out.println("Issue '" + title + "' assigned to " + user.getName());
    }

    public void changeStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Issue '" + title + "' moved to " + status);
    }

    public void display() {
        System.out.println("Issue#" + issueId + ": " + title + " [" + severity + "] - " + status);
    }
}

// bug is a type of issue
class Bug extends Issue {
    private boolean isCritical;

    public Bug(int id, String title, String desc, String severity, boolean isCritical) {
        super(id, title, desc, severity);
        this.isCritical = isCritical;
    }

    @Override
    public void display() {
        System.out.println("Bug: " + getTitle() + " | Critical=" + isCritical + " | Status=" + getStatus());
    }
}

// project holds issues + users
class Project {
    private int projectId;
    private String name;
    private String repoUrl;
    private List<Issue> backlog;
    private List<User> team;

    public Project(int projectId, String name, String repoUrl) {
        this.projectId = projectId;
        this.name = name;
        this.repoUrl = repoUrl;
        this.backlog = new ArrayList<>();
        this.team = new ArrayList<>();
    }

    public void addUser(User u) { team.add(u); }
    public void addIssue(Issue i) { backlog.add(i); }

    public void listIssuesBySeverity(String severity) {
        System.out.println("\n=== Issues with severity: " + severity + " ===");
        for (Issue i : backlog) {
            if (i.getSeverity().equalsIgnoreCase(severity)) {
                i.display();
            }
        }
    }

    public void dashboard() {
        System.out.println("\n---- Project Dashboard ----");
        System.out.println("Project ID: " + projectId + ", Name: " + name + ", Repo: " + repoUrl);
        for (Issue i : backlog) {
            i.display();
        }
    }
}

// service to manage issues
class TrackerService {
    public Issue createIssue(int id, String title, String desc, String severity) {
        return new Issue(id, title, desc, severity);
    }

    public Issue createIssue(int id, String title, String desc, String severity, String tag) {
        return new Issue(id, title, desc + " [Tag=" + tag + "]", severity);
    }

    public void assign(Issue issue, User user) {
        issue.assignTo(user);
    }

    public void updateStatus(Issue issue, String status) {
        issue.changeStatus(status);
    }
} // ✅ closes TrackerService properly


// main class to run everything
public class TrackerAppMain {
    public static void main(String[] args) {
        User dev = new User(101, "Alice", "Dev", "alice@company.com");
        User qa = new User(102, "Bob", "QA", "bob@company.com");
        Manager mgr = new Manager(103, "Charlie", "charlie@company.com");

        Project proj = new Project(1, "BugTracker", "https://github.com/bugtracker");
        proj.addUser(dev);
        proj.addUser(qa);
        proj.addUser(mgr);

        TrackerService service = new TrackerService();

        Issue i1 = service.createIssue(1, "Login Bug", "Login fails intermittently", "High");
        Issue i2 = service.createIssue(2, "Crash on Save", "App crashes when saving", "Medium", "UI");
        Bug i3 = new Bug(3, "Data Loss", "File not saving properly", "Critical", true);

        proj.addIssue(i1);
        proj.addIssue(i2);
        proj.addIssue(i3);

        service.assign(i1, dev);
        service.assign(i2, qa);
        service.assign(i3, dev);

        service.updateStatus(i1, "IN_PROGRESS");
        service.updateStatus(i1, "RESOLVED");
        service.updateStatus(i2, "IN_PROGRESS");
        service.updateStatus(i3, "CLOSED");

        mgr.approveIssue(i1);

        proj.dashboard();
        proj.listIssuesBySeverity("High");
        proj.listIssuesBySeverity("Critical");
    }
}