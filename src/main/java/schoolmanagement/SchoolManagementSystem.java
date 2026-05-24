package schoolmanagement;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.DashboardStats;
import schoolmanagement.model.Task;
import schoolmanagement.model.TaskItem;
import schoolmanagement.model.User;
import schoolmanagement.model.UserItem;
import schoolmanagement.service.CsvExporter;
import schoolmanagement.service.EmailService;
import schoolmanagement.service.SchoolService;
import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class SchoolManagementSystem extends JFrame {
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color MUTED = new Color(107, 114, 128);

    private final SchoolService schoolService = new SchoolService();
    private final EmailService emailService = new EmailService();
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel root = new JPanel(rootLayout);

    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    private JTextField registerFirstNameField;
    private JTextField registerLastNameField;
    private JTextField registerIdField;
    private JTextField registerEmailField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;
    private JComboBox<String> registerRoleBox;

    private User currentUser;
    private JTabbedPane appTabs;
    private JLabel appTitleLabel;
    private JLabel appSubtitleLabel;

    private JTextField taskSubjectField;
    private JTextField taskClassField;
    private JComboBox<String> taskDayBox;
    private JComboBox<UserItem> taskTeacherBox;
    private JTextField taskDeadlineField;
    private JTextArea taskDetailsArea;

    private JTable overviewTable;
    private JTable usersTable;
    private JTable allTasksTable;
    private JTable myTasksTable;
    private JTable teacherFeedbackTable;
    private JTable studentFeedbackTable;

    private JComboBox<TaskItem> teacherTaskBox;
    private JComboBox<String> teacherCoverageBox;
    private JSlider preparednessSlider;
    private JSlider deliverySlider;
    private JSlider enjoymentSlider;
    private JTextArea teacherNotesArea;

    private JTextField studentSubjectField;
    private JComboBox<UserItem> studentTeacherBox;
    private JSlider claritySlider;
    private JSlider engagementSlider;
    private JSlider comfortSlider;
    private JSlider pacingSlider;
    private JSlider ratingSlider;
    private JTextArea studentCommentsArea;

    public SchoolManagementSystem() {
        super("School Teaching & Syllabus Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);

        root.add(createLoginPanel(), "login");
        root.add(createRegisterPanel(), "register");
        setContentPane(root);
        showLogin();
    }

    private JPanel createLoginPanel() {
        JPanel page = pagePanel();
        JPanel card = authCard("School Management", "Track teaching tasks, syllabus progress, and student feedback.");

        loginEmailField = new JTextField(DBConnector.DEFAULT_ADMIN_EMAIL, 24);
        loginPasswordField = new JPasswordField(24);

        addFormRow(card, "Email", loginEmailField, 2);
        addFormRow(card, "Password", loginPasswordField, 3);

        JButton loginButton = primaryButton("Sign In");
        loginButton.addActionListener(event -> login());
        JButton createButton = secondaryButton("Create Account");
        createButton.addActionListener(event -> showRegister());

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(loginButton);
        actions.add(createButton);
        addFullWidth(card, actions, 4);

        JLabel demo = smallText("First run head account: " + DBConnector.DEFAULT_ADMIN_EMAIL + " / " + DBConnector.DEFAULT_ADMIN_PASSWORD);
        addFullWidth(card, demo, 5);

        page.add(card, centeredConstraints());
        return page;
    }

    private JPanel createRegisterPanel() {
        JPanel page = pagePanel();
        JPanel card = authCard("Create Account", "Register as Head of School, Teacher, or Student.");

        registerFirstNameField = new JTextField(24);
        registerLastNameField = new JTextField(24);
        registerIdField = new JTextField(24);
        registerEmailField = new JTextField(24);
        registerPasswordField = new JPasswordField(24);
        registerConfirmPasswordField = new JPasswordField(24);
        registerRoleBox = new JComboBox<>(new String[] {"Head of School", "Teacher", "Student"});

        addFormRow(card, "First Name", registerFirstNameField, 2);
        addFormRow(card, "Last Name", registerLastNameField, 3);
        addFormRow(card, "School ID", registerIdField, 4);
        addFormRow(card, "Email", registerEmailField, 5);
        addFormRow(card, "Password", registerPasswordField, 6);
        addFormRow(card, "Confirm Password", registerConfirmPasswordField, 7);
        addFormRow(card, "Role", registerRoleBox, 8);

        JButton registerButton = primaryButton("Create Account");
        registerButton.addActionListener(event -> register());
        JButton backButton = secondaryButton("Back to Login");
        backButton.addActionListener(event -> showLogin());

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(registerButton);
        actions.add(backButton);
        addFullWidth(card, actions, 9);

        page.add(card, centeredConstraints());
        return page;
    }

    private JPanel createAppPanel() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        header.setBackground(CARD);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        appTitleLabel = new JLabel("School Management");
        appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appTitleLabel.setForeground(TEXT);
        appSubtitleLabel = smallText("");
        titlePanel.add(appTitleLabel);
        titlePanel.add(appSubtitleLabel);

        JButton logoutButton = secondaryButton("Logout");
        logoutButton.addActionListener(event -> logout());

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(logoutButton, BorderLayout.EAST);

        appTabs = new JTabbedPane();
        appTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appTabs.add("Overview", createOverviewTab());

        if (isHead()) {
            appTabs.add("Assign Tasks", createAssignTaskTab());
            appTabs.add("All Tasks", createAllTasksTab());
            appTabs.add("Users", createUsersTab());
            appTabs.add("Teacher Feedback", createTeacherFeedbackTab());
            appTabs.add("Student Feedback", createStudentFeedbackTableTab());
            appTabs.add("Email Teachers", createEmailTab());
            appTabs.add("Reports", createReportsTab());
        } else if (isTeacher()) {
            appTabs.add("My Tasks", createMyTasksTab());
            appTabs.add("Submit Progress", createTeacherProgressTab());
        } else {
            appTabs.add("Submit Reflection", createStudentReflectionTab());
        }
        appTabs.add("Account", createAccountTab());

        page.add(header, BorderLayout.NORTH);
        page.add(appTabs, BorderLayout.CENTER);
        return page;
    }

    private JPanel createOverviewTab() {
        JPanel panel = contentPanel();
        overviewTable = table(new String[] {"Metric", "Value"});
        panel.add(new JScrollPane(overviewTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAssignTaskTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        taskSubjectField = new JTextField(24);
        taskClassField = new JTextField(24);
        taskDayBox = new JComboBox<>(new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        taskTeacherBox = new JComboBox<>();
        taskDeadlineField = new JTextField(24);
        taskDetailsArea = new JTextArea(5, 24);
        taskDetailsArea.setLineWrap(true);
        taskDetailsArea.setWrapStyleWord(true);

        addFormRow(form, "Subject", taskSubjectField, 0);
        addFormRow(form, "Class", taskClassField, 1);
        addFormRow(form, "Day", taskDayBox, 2);
        addFormRow(form, "Teacher", taskTeacherBox, 3);
        addFormRow(form, "Deadline", taskDeadlineField, 4);
        addFormRow(form, "Task Details", new JScrollPane(taskDetailsArea), 5);

        JButton assignButton = primaryButton("Assign Task");
        assignButton.addActionListener(event -> assignTask());
        addFullWidth(form, assignButton, 6);

        return panel;
    }

    private JPanel createAllTasksTab() {
        JPanel panel = contentPanel();
        allTasksTable = table(new String[] {"ID", "Subject", "Class", "Day", "Teacher", "Deadline", "Status"});
        panel.add(new JScrollPane(allTasksTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 12, 0));
        actions.setOpaque(false);
        JButton refreshButton = secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshAll());
        JButton inProgressButton = secondaryButton("Mark In Progress");
        inProgressButton.addActionListener(event -> updateSelectedTaskStatus(allTasksTable, "In Progress"));
        JButton completeButton = primaryButton("Mark Completed");
        completeButton.addActionListener(event -> updateSelectedTaskStatus(allTasksTable, "Completed"));
        actions.add(refreshButton);
        actions.add(inProgressButton);
        actions.add(completeButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUsersTab() {
        JPanel panel = contentPanel();
        usersTable = table(new String[] {"ID", "Name", "School ID", "Email", "Role"});
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeacherFeedbackTab() {
        JPanel panel = contentPanel();
        teacherFeedbackTable = table(new String[] {"Teacher", "Task", "Coverage", "Prepared", "Delivery", "Enjoyment", "Notes", "Date"});
        panel.add(new JScrollPane(teacherFeedbackTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentFeedbackTableTab() {
        JPanel panel = contentPanel();
        studentFeedbackTable = table(new String[] {"Student", "Teacher", "Subject", "Rating", "Clarity", "Engagement", "Comfort", "Pacing", "Comments", "Date"});
        panel.add(new JScrollPane(studentFeedbackTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyTasksTab() {
        JPanel panel = contentPanel();
        myTasksTable = table(new String[] {"ID", "Subject", "Class", "Day", "Deadline", "Status", "Details"});
        panel.add(new JScrollPane(myTasksTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 12, 0));
        actions.setOpaque(false);
        JButton refreshButton = secondaryButton("Refresh");
        refreshButton.addActionListener(event -> refreshAll());
        JButton inProgressButton = secondaryButton("Mark In Progress");
        inProgressButton.addActionListener(event -> updateSelectedTaskStatus(myTasksTable, "In Progress"));
        JButton completeButton = primaryButton("Mark Completed");
        completeButton.addActionListener(event -> updateSelectedTaskStatus(myTasksTable, "Completed"));
        actions.add(refreshButton);
        actions.add(inProgressButton);
        actions.add(completeButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTeacherProgressTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        teacherTaskBox = new JComboBox<>();
        teacherCoverageBox = new JComboBox<>(new String[] {"Not started", "Started", "Halfway complete", "Mostly complete", "Completed"});
        preparednessSlider = slider();
        deliverySlider = slider();
        enjoymentSlider = slider();
        teacherNotesArea = new JTextArea(5, 24);
        teacherNotesArea.setLineWrap(true);
        teacherNotesArea.setWrapStyleWord(true);

        addFormRow(form, "Task", teacherTaskBox, 0);
        addFormRow(form, "Coverage", teacherCoverageBox, 1);
        addFormRow(form, "Preparedness", preparednessSlider, 2);
        addFormRow(form, "Delivery", deliverySlider, 3);
        addFormRow(form, "Student Enjoyment", enjoymentSlider, 4);
        addFormRow(form, "Reflection Notes", new JScrollPane(teacherNotesArea), 5);

        JButton submitButton = primaryButton("Submit Progress");
        submitButton.addActionListener(event -> submitTeacherProgress());
        addFullWidth(form, submitButton, 6);
        return panel;
    }

    private JPanel createStudentReflectionTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        studentTeacherBox = new JComboBox<>();
        studentSubjectField = new JTextField(24);
        claritySlider = slider();
        engagementSlider = slider();
        comfortSlider = slider();
        pacingSlider = slider();
        ratingSlider = slider();
        studentCommentsArea = new JTextArea(5, 24);
        studentCommentsArea.setLineWrap(true);
        studentCommentsArea.setWrapStyleWord(true);

        addFormRow(form, "Teacher", studentTeacherBox, 0);
        addFormRow(form, "Subject", studentSubjectField, 1);
        addFormRow(form, "Clarity", claritySlider, 2);
        addFormRow(form, "Engagement", engagementSlider, 3);
        addFormRow(form, "Comfort", comfortSlider, 4);
        addFormRow(form, "Pacing", pacingSlider, 5);
        addFormRow(form, "Overall Rating", ratingSlider, 6);
        addFormRow(form, "Comments", new JScrollPane(studentCommentsArea), 7);

        JButton submitButton = primaryButton("Submit Reflection");
        submitButton.addActionListener(event -> submitStudentReflection());
        addFullWidth(form, submitButton, 8);
        return panel;
    }

    private JPanel createEmailTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        JTextField subjectField = new JTextField(24);
        JTextArea bodyArea = new JTextArea(8, 24);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JLabel statusLabel = smallText(emailService.configurationMessage());
        addFormRow(form, "Subject", subjectField, 0);
        addFormRow(form, "Message", new JScrollPane(bodyArea), 1);
        addFullWidth(form, statusLabel, 2);

        JButton sendButton = primaryButton("Send to Teachers");
        sendButton.addActionListener(event -> sendTeacherEmails(subjectField.getText(), bodyArea.getText()));
        addFullWidth(form, sendButton, 3);
        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        JLabel title = new JLabel("Export CSV reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        addFullWidth(form, title, 0);
        addFullWidth(form, smallText("Exports use the latest table data, including current sorting."), 1);

        JPanel actions = new JPanel(new GridLayout(2, 2, 12, 12));
        actions.setOpaque(false);
        JButton usersButton = secondaryButton("Export Users");
        usersButton.addActionListener(event -> exportTable(usersTable, "users.csv"));
        JButton tasksButton = secondaryButton("Export Tasks");
        tasksButton.addActionListener(event -> exportTable(allTasksTable, "tasks.csv"));
        JButton teacherFeedbackButton = secondaryButton("Export Teacher Feedback");
        teacherFeedbackButton.addActionListener(event -> exportTable(teacherFeedbackTable, "teacher-feedback.csv"));
        JButton studentFeedbackButton = secondaryButton("Export Student Feedback");
        studentFeedbackButton.addActionListener(event -> exportTable(studentFeedbackTable, "student-feedback.csv"));
        actions.add(usersButton);
        actions.add(tasksButton);
        actions.add(teacherFeedbackButton);
        actions.add(studentFeedbackButton);
        addFullWidth(form, actions, 2);
        return panel;
    }

    private JPanel createAccountTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel(new GridBagLayout());
        panel.add(form, BorderLayout.NORTH);

        JLabel title = new JLabel("Account security");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        addFullWidth(form, title, 0);
        addFullWidth(form, smallText("Signed in as " + currentUser.email + " (" + currentUser.role + ")."), 1);

        JButton passwordButton = primaryButton("Change Password");
        passwordButton.addActionListener(event -> promptPasswordChange(false));
        addFullWidth(form, passwordButton, 2);
        return panel;
    }

    private void login() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (email.isBlank() || password.isBlank()) {
            showError("Please enter both email and password.");
            return;
        }

        try {
            User user = authenticate(email, password);
            if (user == null) {
                showError("Invalid email or password.");
                return;
            }
            currentUser = user;
            showApp();
            if (schoolService.needsFirstRunPasswordChange(currentUser)) {
                promptPasswordChange(true);
            }
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Login failed: " + ex.getMessage());
        }
    }

    private void register() {
        String firstName = registerFirstNameField.getText().trim();
        String lastName = registerLastNameField.getText().trim();
        String idNo = registerIdField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(registerConfirmPasswordField.getPassword());
        String role = (String) registerRoleBox.getSelectedItem();

        if (firstName.isBlank() || lastName.isBlank() || idNo.isBlank() || email.isBlank() || password.isBlank()) {
            showError("All fields are required.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (password.length() < 8) {
            showError("Password must be at least 8 characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            createUser(firstName, lastName, idNo, email, password, role);
            JOptionPane.showMessageDialog(this, "Account created. You can sign in now.");
            loginEmailField.setText(email);
            loginPasswordField.setText("");
            clearRegisterForm();
            showLogin();
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not create account: " + ex.getMessage());
        }
    }

    private void assignTask() {
        UserItem teacher = (UserItem) taskTeacherBox.getSelectedItem();
        String subject = taskSubjectField.getText().trim();
        String className = taskClassField.getText().trim();
        String day = (String) taskDayBox.getSelectedItem();
        String deadline = taskDeadlineField.getText().trim();
        String details = taskDetailsArea.getText().trim();

        if (teacher == null) {
            showError("Please create or select a teacher first.");
            return;
        }
        if (subject.isBlank() || className.isBlank() || details.isBlank()) {
            showError("Subject, class, and task details are required.");
            return;
        }

        try {
            schoolService.assignTask(subject, className, day, teacher.user.idNo, details, deadline);
            clearTaskForm();
            refreshAll();
            JOptionPane.showMessageDialog(this, "Task assigned to " + teacher + ".");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not assign task: " + ex.getMessage());
        }
    }

    private void submitTeacherProgress() {
        TaskItem task = (TaskItem) teacherTaskBox.getSelectedItem();
        if (task == null) {
            showError("You do not have any assigned tasks yet.");
            return;
        }

        try {
            schoolService.submitTeacherProgress(
                currentUser,
                task.task.id,
                (String) teacherCoverageBox.getSelectedItem(),
                teacherNotesArea.getText(),
                preparednessSlider.getValue(),
                deliverySlider.getValue(),
                enjoymentSlider.getValue()
            );
            teacherNotesArea.setText("");
            refreshAll();
            JOptionPane.showMessageDialog(this, "Progress submitted.");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not submit progress: " + ex.getMessage());
        }
    }

    private void submitStudentReflection() {
        UserItem teacher = (UserItem) studentTeacherBox.getSelectedItem();
        String subject = studentSubjectField.getText().trim();

        if (subject.isBlank()) {
            showError("Subject is required.");
            return;
        }

        try {
            schoolService.submitStudentReflection(
                currentUser,
                teacher == null ? null : teacher.user.idNo,
                subject,
                claritySlider.getValue(),
                engagementSlider.getValue(),
                comfortSlider.getValue(),
                pacingSlider.getValue(),
                ratingSlider.getValue(),
                studentCommentsArea.getText()
            );
            studentSubjectField.setText("");
            studentCommentsArea.setText("");
            refreshAll();
            JOptionPane.showMessageDialog(this, "Reflection submitted. Thank you.");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not submit reflection: " + ex.getMessage());
        }
    }

    private void updateSelectedTaskStatus(JTable table, String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select a task first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int taskId = Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());
        try {
            updateTaskStatus(taskId, status);
            refreshAll();
        } catch (SQLException ex) {
            showError("Could not update task: " + ex.getMessage());
        }
    }

    private void updateTaskStatus(int taskId, String status) throws SQLException {
        schoolService.updateTaskStatus(taskId, status);
    }

    private void sendTeacherEmails(String subject, String body) {
        if (subject.isBlank() || body.isBlank()) {
            showError("Subject and message are required.");
            return;
        }
        if (!emailService.isConfigured()) {
            showError(emailService.configurationMessage());
            return;
        }

        try {
            List<User> teachers = getUsersByRole("Teacher");
            if (teachers.isEmpty()) {
                showError("There are no teacher accounts yet.");
                return;
            }
            for (User teacher : teachers) {
                emailService.send(teacher.email, subject, body);
            }
            JOptionPane.showMessageDialog(this, "Email sent to " + teachers.size() + " teacher(s).");
        } catch (SQLException | MessagingException | IllegalArgumentException ex) {
            showError("Could not send emails: " + ex.getMessage());
        }
    }

    private void refreshAll() {
        if (currentUser == null) {
            return;
        }

        appTitleLabel.setText("Welcome, " + currentUser.firstName + " " + currentUser.lastName);
        appSubtitleLabel.setText(currentUser.role + " | " + currentUser.email);

        try {
            loadOverview();
            loadTeacherOptions();

            if (isHead()) {
                loadUsersTable();
                loadAllTasksTable();
                loadTeacherFeedbackTable();
                loadStudentFeedbackTable();
            }
            if (isTeacher()) {
                loadMyTasksTable();
                loadTeacherTaskOptions();
            }
            if (isStudent()) {
                loadStudentTeacherOptions();
            }
        } catch (SQLException ex) {
            showError("Could not refresh data: " + ex.getMessage());
        }
    }

    private void loadOverview() throws SQLException {
        DefaultTableModel model = model(overviewTable);
        model.setRowCount(0);
        DashboardStats stats = schoolService.getDashboardStats();
        model.addRow(new Object[] {"Signed-in role", currentUser.role});
        model.addRow(new Object[] {"Users", stats.users});
        model.addRow(new Object[] {"Teachers", stats.teachers});
        model.addRow(new Object[] {"Students", stats.students});
        model.addRow(new Object[] {"Teaching tasks", stats.tasks});
        model.addRow(new Object[] {"Open tasks", stats.openTasks});
        model.addRow(new Object[] {"Completed tasks", stats.completedTasks});
        model.addRow(new Object[] {"Teacher progress reports", stats.teacherFeedback});
        model.addRow(new Object[] {"Student reflections", stats.studentFeedback});
        model.addRow(new Object[] {"First-run admin email", DBConnector.DEFAULT_ADMIN_EMAIL});
    }

    private void loadUsersTable() throws SQLException {
        DefaultTableModel model = model(usersTable);
        model.setRowCount(0);
        for (User user : getUsers()) {
            model.addRow(new Object[] {user.id, user.firstName + " " + user.lastName, user.idNo, user.email, user.role});
        }
    }

    private void loadAllTasksTable() throws SQLException {
        DefaultTableModel model = model(allTasksTable);
        model.setRowCount(0);
        for (Task task : getTasks(null)) {
            model.addRow(new Object[] {task.id, task.subject, task.className, task.day, task.teacherName, task.deadline, task.status});
        }
    }

    private void loadMyTasksTable() throws SQLException {
        DefaultTableModel model = model(myTasksTable);
        model.setRowCount(0);
        for (Task task : getTasks(currentUser.idNo)) {
            model.addRow(new Object[] {task.id, task.subject, task.className, task.day, task.deadline, task.status, task.details});
        }
    }

    private void loadTeacherFeedbackTable() throws SQLException {
        DefaultTableModel model = model(teacherFeedbackTable);
        model.setRowCount(0);

        for (Object[] row : schoolService.getTeacherFeedbackRows()) {
            model.addRow(row);
        }
    }

    private void loadStudentFeedbackTable() throws SQLException {
        DefaultTableModel model = model(studentFeedbackTable);
        model.setRowCount(0);

        for (Object[] row : schoolService.getStudentFeedbackRows()) {
            model.addRow(row);
        }
    }

    private void loadTeacherOptions() throws SQLException {
        if (taskTeacherBox == null) {
            return;
        }
        DefaultComboBoxModel<UserItem> model = new DefaultComboBoxModel<>();
        for (User teacher : getUsersByRole("Teacher")) {
            model.addElement(new UserItem(teacher));
        }
        taskTeacherBox.setModel(model);
    }

    private void loadStudentTeacherOptions() throws SQLException {
        if (studentTeacherBox == null) {
            return;
        }
        DefaultComboBoxModel<UserItem> model = new DefaultComboBoxModel<>();
        for (User teacher : getUsersByRole("Teacher")) {
            model.addElement(new UserItem(teacher));
        }
        studentTeacherBox.setModel(model);
    }

    private void loadTeacherTaskOptions() throws SQLException {
        if (teacherTaskBox == null) {
            return;
        }
        DefaultComboBoxModel<TaskItem> model = new DefaultComboBoxModel<>();
        for (Task task : getTasks(currentUser.idNo)) {
            model.addElement(new TaskItem(task));
        }
        teacherTaskBox.setModel(model);
    }

    private User authenticate(String email, String password) throws SQLException {
        return schoolService.authenticate(email, password);
    }

    private void createUser(String firstName, String lastName, String idNo, String email, String password, String role) throws SQLException {
        schoolService.createUser(firstName, lastName, idNo, email, password, role);
    }

    private List<User> getUsers() throws SQLException {
        return schoolService.getUsers();
    }

    private List<User> getUsersByRole(String role) throws SQLException {
        return schoolService.getUsersByRole(role);
    }

    private List<Task> getTasks(String teacherIdNo) throws SQLException {
        return schoolService.getTasks(teacherIdNo);
    }

    private boolean promptPasswordChange(boolean required) {
        JPasswordField newPasswordField = new JPasswordField(22);
        JPasswordField confirmPasswordField = new JPasswordField(22);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        addFormRow(panel, "New Password", newPasswordField, 0);
        addFormRow(panel, "Confirm Password", confirmPasswordField, 1);
        addFullWidth(panel, smallText("Use at least 8 characters with one letter and one number."), 2);

        while (true) {
            int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                required ? "Change Default Admin Password" : "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (option != JOptionPane.OK_OPTION) {
                if (required) {
                    JOptionPane.showMessageDialog(this, "The default admin password must be changed before using the app.");
                    logout();
                }
                return false;
            }

            String password = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match.");
                continue;
            }

            try {
                currentUser = schoolService.changePassword(currentUser, password);
                refreshAll();
                JOptionPane.showMessageDialog(this, "Password updated.");
                return true;
            } catch (SQLException | IllegalArgumentException ex) {
                showError("Could not update password: " + ex.getMessage());
            }
        }
    }

    private void exportTable(JTable table, String defaultFileName) {
        if (table == null) {
            showError("This report is not available yet.");
            return;
        }
        if (table.getRowCount() == 0) {
            showError("There is no data to export yet.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export " + defaultFileName);
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }

        try {
            Path path = file.toPath();
            CsvExporter.writeTable(table, path);
            JOptionPane.showMessageDialog(this, "Exported " + table.getRowCount() + " row(s) to " + file.getName() + ".");
        } catch (IOException ex) {
            showError("Could not export CSV: " + ex.getMessage());
        }
    }

    private void showLogin() {
        rootLayout.show(root, "login");
    }

    private void showRegister() {
        rootLayout.show(root, "register");
    }

    private void showApp() {
        if (root.getComponentCount() > 2) {
            root.remove(2);
        }
        root.add(createAppPanel(), "app");
        rootLayout.show(root, "app");
        refreshAll();
    }

    private void logout() {
        currentUser = null;
        loginPasswordField.setText("");
        showLogin();
    }

    private boolean isHead() {
        return currentUser != null && "Head of School".equals(currentUser.role);
    }

    private boolean isTeacher() {
        return currentUser != null && "Teacher".equals(currentUser.role);
    }

    private boolean isStudent() {
        return currentUser != null && "Student".equals(currentUser.role);
    }

    private void clearRegisterForm() {
        registerFirstNameField.setText("");
        registerLastNameField.setText("");
        registerIdField.setText("");
        registerEmailField.setText("");
        registerPasswordField.setText("");
        registerConfirmPasswordField.setText("");
        registerRoleBox.setSelectedIndex(0);
    }

    private void clearTaskForm() {
        taskSubjectField.setText("");
        taskClassField.setText("");
        taskDeadlineField.setText("");
        taskDetailsArea.setText("");
        taskDayBox.setSelectedIndex(0);
    }

    private JPanel pagePanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BACKGROUND);
        return page;
    }

    private GridBagConstraints centeredConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        return constraints;
    }

    private JPanel authCard(String title, String subtitle) {
        JPanel card = cardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(520, 520));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT);
        addFullWidth(card, titleLabel, 0);

        JLabel subtitleLabel = smallText(subtitle);
        addFullWidth(card, subtitleLabel, 1);
        return card;
    }

    private JPanel contentPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        panel.setBackground(BACKGROUND);
        return panel;
    }

    private JPanel cardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        return panel;
    }

    private void addFormRow(JPanel panel, String label, Component field, int row) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(8, 0, 8, 14);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComponent.setForeground(TEXT);
        panel.add(labelComponent, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(8, 0, 8, 0);
        panel.add(field, fieldConstraints);
    }

    private void addFullWidth(JPanel panel, Component component, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8, 0, 8, 0);
        panel.add(component, constraints);
    }

    private JLabel smallText(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(243, 244, 246));
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    private JTable table(String[] columns) {
        JTable table = new JTable(new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setGridColor(new Color(229, 231, 235));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                    component.setForeground(TEXT);
                }
                return component;
            }
        });
        return table;
    }

    private DefaultTableModel model(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    private JSlider slider() {
        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 1, 5, 3);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setOpaque(false);
        return slider;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "School Management", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use default look and feel if the system one is unavailable.
            }
            SchoolManagementSystem app = new SchoolManagementSystem();
            app.setVisible(true);
        });
    }

}
