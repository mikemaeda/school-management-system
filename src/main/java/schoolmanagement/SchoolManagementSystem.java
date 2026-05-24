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
import schoolmanagement.db.DBConnector;
import schoolmanagement.model.DashboardStats;
import schoolmanagement.model.Task;
import schoolmanagement.model.TaskItem;
import schoolmanagement.model.User;
import schoolmanagement.model.UserItem;
import schoolmanagement.service.CsvExporter;
import schoolmanagement.service.EmailService;
import schoolmanagement.service.SchoolService;

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

    private User currentUser;
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField idField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleBox;
    private JLabel titleLabel;
    private JLabel subtitleLabel;

    private JTable overviewTable;
    private JTable usersTable;
    private JTable allTasksTable;
    private JTable myTasksTable;
    private JTable teacherFeedbackTable;
    private JTable studentFeedbackTable;
    private JComboBox<UserItem> taskTeacherBox;
    private JTextField taskSubjectField;
    private JTextField taskClassField;
    private JComboBox<String> taskDayBox;
    private JTextField taskDeadlineField;
    private JTextArea taskDetailsArea;
    private JComboBox<TaskItem> progressTaskBox;
    private JComboBox<String> coverageBox;
    private JSlider preparednessSlider;
    private JSlider deliverySlider;
    private JSlider enjoymentSlider;
    private JTextArea progressNotesArea;
    private JComboBox<UserItem> reflectionTeacherBox;
    private JTextField reflectionSubjectField;
    private JSlider claritySlider;
    private JSlider engagementSlider;
    private JSlider comfortSlider;
    private JSlider pacingSlider;
    private JSlider ratingSlider;
    private JTextArea reflectionCommentsArea;

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
        JPanel card = authCard("School Management", "Track teaching tasks, syllabus progress, and feedback.");
        loginEmailField = new JTextField(DBConnector.DEFAULT_ADMIN_EMAIL, 24);
        loginPasswordField = new JPasswordField(24);
        addRow(card, "Email", loginEmailField, 2);
        addRow(card, "Password", loginPasswordField, 3);

        JPanel actions = buttonRow(2);
        JButton signIn = primaryButton("Sign In");
        signIn.addActionListener(event -> login());
        JButton create = secondaryButton("Create Account");
        create.addActionListener(event -> showRegister());
        actions.add(signIn);
        actions.add(create);
        addFull(card, actions, 4);
        addFull(card, smallText("First run head account: " + DBConnector.DEFAULT_ADMIN_EMAIL + " / " + DBConnector.DEFAULT_ADMIN_PASSWORD), 5);
        page.add(card, centered());
        return page;
    }

    private JPanel createRegisterPanel() {
        JPanel page = pagePanel();
        JPanel card = authCard("Create Account", "Register as Head of School, Teacher, or Student.");
        firstNameField = new JTextField(24);
        lastNameField = new JTextField(24);
        idField = new JTextField(24);
        emailField = new JTextField(24);
        passwordField = new JPasswordField(24);
        confirmPasswordField = new JPasswordField(24);
        roleBox = new JComboBox<>(new String[] {SchoolService.ROLE_HEAD, SchoolService.ROLE_TEACHER, SchoolService.ROLE_STUDENT});
        addRow(card, "First Name", firstNameField, 2);
        addRow(card, "Last Name", lastNameField, 3);
        addRow(card, "School ID", idField, 4);
        addRow(card, "Email", emailField, 5);
        addRow(card, "Password", passwordField, 6);
        addRow(card, "Confirm", confirmPasswordField, 7);
        addRow(card, "Role", roleBox, 8);

        JPanel actions = buttonRow(2);
        JButton register = primaryButton("Create Account");
        register.addActionListener(event -> register());
        JButton back = secondaryButton("Back to Login");
        back.addActionListener(event -> showLogin());
        actions.add(register);
        actions.add(back);
        addFull(card, actions, 9);
        page.add(card, centered());
        return page;
    }

    private JPanel createAppPanel() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        header.setBackground(CARD);
        JPanel copy = new JPanel(new GridLayout(2, 1));
        copy.setOpaque(false);
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT);
        subtitleLabel = smallText("");
        copy.add(titleLabel);
        copy.add(subtitleLabel);
        JButton logout = secondaryButton("Logout");
        logout.addActionListener(event -> logout());
        header.add(copy, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.add("Overview", createOverviewTab());
        if (isHead()) {
            tabs.add("Assign Tasks", createAssignTaskTab());
            tabs.add("All Tasks", createAllTasksTab());
            tabs.add("Users", createUsersTab());
            tabs.add("Teacher Feedback", createTeacherFeedbackTab());
            tabs.add("Student Feedback", createStudentFeedbackTab());
            tabs.add("Email Teachers", createEmailTab());
            tabs.add("Reports", createReportsTab());
        } else if (isTeacher()) {
            tabs.add("My Tasks", createMyTasksTab());
            tabs.add("Submit Progress", createProgressTab());
        } else {
            tabs.add("Submit Reflection", createReflectionTab());
        }
        tabs.add("Account", createAccountTab());
        page.add(header, BorderLayout.NORTH);
        page.add(tabs, BorderLayout.CENTER);
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
        JPanel form = cardPanel();
        taskTeacherBox = new JComboBox<>();
        taskSubjectField = new JTextField(24);
        taskClassField = new JTextField(24);
        taskDayBox = new JComboBox<>(new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        taskDeadlineField = new JTextField(24);
        taskDetailsArea = textArea(5);
        addRow(form, "Subject", taskSubjectField, 0);
        addRow(form, "Class", taskClassField, 1);
        addRow(form, "Day", taskDayBox, 2);
        addRow(form, "Teacher", taskTeacherBox, 3);
        addRow(form, "Deadline", taskDeadlineField, 4);
        addRow(form, "Details", new JScrollPane(taskDetailsArea), 5);
        JButton assign = primaryButton("Assign Task");
        assign.addActionListener(event -> assignTask());
        addFull(form, assign, 6);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAllTasksTab() {
        JPanel panel = contentPanel();
        allTasksTable = table(new String[] {"ID", "Subject", "Class", "Day", "Teacher", "Deadline", "Status"});
        panel.add(new JScrollPane(allTasksTable), BorderLayout.CENTER);
        panel.add(taskActions(allTasksTable), BorderLayout.SOUTH);
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

    private JPanel createStudentFeedbackTab() {
        JPanel panel = contentPanel();
        studentFeedbackTable = table(new String[] {"Student", "Teacher", "Subject", "Rating", "Clarity", "Engagement", "Comfort", "Pacing", "Comments", "Date"});
        panel.add(new JScrollPane(studentFeedbackTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMyTasksTab() {
        JPanel panel = contentPanel();
        myTasksTable = table(new String[] {"ID", "Subject", "Class", "Day", "Deadline", "Status", "Details"});
        panel.add(new JScrollPane(myTasksTable), BorderLayout.CENTER);
        panel.add(taskActions(myTasksTable), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProgressTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel();
        progressTaskBox = new JComboBox<>();
        coverageBox = new JComboBox<>(new String[] {"Not started", "Started", "Halfway complete", "Mostly complete", "Completed"});
        preparednessSlider = slider();
        deliverySlider = slider();
        enjoymentSlider = slider();
        progressNotesArea = textArea(5);
        addRow(form, "Task", progressTaskBox, 0);
        addRow(form, "Coverage", coverageBox, 1);
        addRow(form, "Preparedness", preparednessSlider, 2);
        addRow(form, "Delivery", deliverySlider, 3);
        addRow(form, "Enjoyment", enjoymentSlider, 4);
        addRow(form, "Notes", new JScrollPane(progressNotesArea), 5);
        JButton submit = primaryButton("Submit Progress");
        submit.addActionListener(event -> submitProgress());
        addFull(form, submit, 6);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createReflectionTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel();
        reflectionTeacherBox = new JComboBox<>();
        reflectionSubjectField = new JTextField(24);
        claritySlider = slider();
        engagementSlider = slider();
        comfortSlider = slider();
        pacingSlider = slider();
        ratingSlider = slider();
        reflectionCommentsArea = textArea(5);
        addRow(form, "Teacher", reflectionTeacherBox, 0);
        addRow(form, "Subject", reflectionSubjectField, 1);
        addRow(form, "Clarity", claritySlider, 2);
        addRow(form, "Engagement", engagementSlider, 3);
        addRow(form, "Comfort", comfortSlider, 4);
        addRow(form, "Pacing", pacingSlider, 5);
        addRow(form, "Rating", ratingSlider, 6);
        addRow(form, "Comments", new JScrollPane(reflectionCommentsArea), 7);
        JButton submit = primaryButton("Submit Reflection");
        submit.addActionListener(event -> submitReflection());
        addFull(form, submit, 8);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createEmailTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel();
        JTextField subject = new JTextField(24);
        JTextArea body = textArea(8);
        addRow(form, "Subject", subject, 0);
        addRow(form, "Message", new JScrollPane(body), 1);
        addFull(form, smallText(emailService.configurationMessage()), 2);
        JButton send = primaryButton("Send to Teachers");
        send.addActionListener(event -> sendTeachers(subject.getText(), body.getText()));
        addFull(form, send, 3);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel();
        JLabel title = new JLabel("Export CSV reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);
        addFull(form, title, 0);
        JPanel buttons = buttonRow(2);
        JButton users = secondaryButton("Export Users");
        users.addActionListener(event -> exportTable(usersTable, "users.csv"));
        JButton tasks = secondaryButton("Export Tasks");
        tasks.addActionListener(event -> exportTable(allTasksTable, "tasks.csv"));
        JButton teacherFeedback = secondaryButton("Export Teacher Feedback");
        teacherFeedback.addActionListener(event -> exportTable(teacherFeedbackTable, "teacher-feedback.csv"));
        JButton studentFeedback = secondaryButton("Export Student Feedback");
        studentFeedback.addActionListener(event -> exportTable(studentFeedbackTable, "student-feedback.csv"));
        buttons.add(users);
        buttons.add(tasks);
        buttons.add(teacherFeedback);
        buttons.add(studentFeedback);
        addFull(form, buttons, 1);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAccountTab() {
        JPanel panel = contentPanel();
        JPanel form = cardPanel();
        addFull(form, smallText("Signed in as " + currentUser.email + " (" + currentUser.role + ")."), 0);
        JButton password = primaryButton("Change Password");
        password.addActionListener(event -> promptPasswordChange(false));
        addFull(form, password, 1);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private void login() {
        try {
            User user = schoolService.authenticate(loginEmailField.getText().trim(), new String(loginPasswordField.getPassword()));
            if (user == null) {
                showError("Invalid email or password.");
                return;
            }
            currentUser = user;
            showApp();
            if (schoolService.needsFirstRunPasswordChange(currentUser)) {
                promptPasswordChange(true);
            }
        } catch (SQLException ex) {
            showError("Login failed: " + ex.getMessage());
        }
    }

    private void register() {
        String password = new String(passwordField.getPassword());
        if (!password.equals(new String(confirmPasswordField.getPassword()))) {
            showError("Passwords do not match.");
            return;
        }
        try {
            schoolService.createUser(firstNameField.getText(), lastNameField.getText(), idField.getText(), emailField.getText(), password, (String) roleBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Account created. You can sign in now.");
            loginEmailField.setText(emailField.getText().trim());
            clearRegisterForm();
            showLogin();
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not create account: " + ex.getMessage());
        }
    }

    private void assignTask() {
        UserItem teacher = (UserItem) taskTeacherBox.getSelectedItem();
        if (teacher == null) {
            showError("Please create or select a teacher first.");
            return;
        }
        try {
            schoolService.assignTask(taskSubjectField.getText(), taskClassField.getText(), (String) taskDayBox.getSelectedItem(), teacher.user.idNo, taskDetailsArea.getText(), taskDeadlineField.getText());
            clearTaskForm();
            refreshAll();
            JOptionPane.showMessageDialog(this, "Task assigned to " + teacher + ".");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not assign task: " + ex.getMessage());
        }
    }

    private void submitProgress() {
        TaskItem item = (TaskItem) progressTaskBox.getSelectedItem();
        if (item == null) {
            showError("You do not have any assigned tasks yet.");
            return;
        }
        try {
            schoolService.submitTeacherProgress(currentUser, item.task.id, (String) coverageBox.getSelectedItem(), progressNotesArea.getText(), preparednessSlider.getValue(), deliverySlider.getValue(), enjoymentSlider.getValue());
            progressNotesArea.setText("");
            refreshAll();
            JOptionPane.showMessageDialog(this, "Progress submitted.");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not submit progress: " + ex.getMessage());
        }
    }

    private void submitReflection() {
        UserItem teacher = (UserItem) reflectionTeacherBox.getSelectedItem();
        try {
            schoolService.submitStudentReflection(currentUser, teacher == null ? null : teacher.user.idNo, reflectionSubjectField.getText(), claritySlider.getValue(), engagementSlider.getValue(), comfortSlider.getValue(), pacingSlider.getValue(), ratingSlider.getValue(), reflectionCommentsArea.getText());
            reflectionSubjectField.setText("");
            reflectionCommentsArea.setText("");
            refreshAll();
            JOptionPane.showMessageDialog(this, "Reflection submitted. Thank you.");
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not submit reflection: " + ex.getMessage());
        }
    }

    private void updateSelectedTaskStatus(JTable table, String status) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showError("Select a task first.");
            return;
        }
        int id = Integer.parseInt(table.getModel().getValueAt(table.convertRowIndexToModel(selected), 0).toString());
        try {
            schoolService.updateTaskStatus(id, status);
            refreshAll();
        } catch (SQLException ex) {
            showError("Could not update task: " + ex.getMessage());
        }
    }

    private void sendTeachers(String subject, String body) {
        if (subject.isBlank() || body.isBlank()) {
            showError("Subject and message are required.");
            return;
        }
        if (!emailService.isConfigured()) {
            showError(emailService.configurationMessage());
            return;
        }
        try {
            List<User> teachers = schoolService.getUsersByRole(SchoolService.ROLE_TEACHER);
            for (User teacher : teachers) {
                emailService.send(teacher.email, subject, body);
            }
            JOptionPane.showMessageDialog(this, "Email sent to " + teachers.size() + " teacher(s).");
        } catch (SQLException | MessagingException ex) {
            showError("Could not send emails: " + ex.getMessage());
        }
    }

    private void refreshAll() {
        if (currentUser == null) {
            return;
        }
        titleLabel.setText("Welcome, " + currentUser.fullName());
        subtitleLabel.setText(currentUser.role + " | " + currentUser.email);
        try {
            loadOverview();
            loadTeacherOptions();
            if (isHead()) {
                loadUsers();
                loadAllTasks();
                loadTeacherFeedback();
                loadStudentFeedback();
            }
            if (isTeacher()) {
                loadMyTasks();
                loadProgressTasks();
            }
            if (isStudent()) {
                loadReflectionTeachers();
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
    }

    private void loadUsers() throws SQLException {
        DefaultTableModel model = model(usersTable);
        model.setRowCount(0);
        for (User user : schoolService.getUsers()) {
            model.addRow(new Object[] {user.id, user.fullName(), user.idNo, user.email, user.role});
        }
    }

    private void loadAllTasks() throws SQLException {
        DefaultTableModel model = model(allTasksTable);
        model.setRowCount(0);
        for (Task task : schoolService.getTasks(null)) {
            model.addRow(new Object[] {task.id, task.subject, task.className, task.day, task.teacherName, task.deadline, task.status});
        }
    }

    private void loadMyTasks() throws SQLException {
        DefaultTableModel model = model(myTasksTable);
        model.setRowCount(0);
        for (Task task : schoolService.getTasks(currentUser.idNo)) {
            model.addRow(new Object[] {task.id, task.subject, task.className, task.day, task.deadline, task.status, task.details});
        }
    }

    private void loadTeacherFeedback() throws SQLException {
        DefaultTableModel model = model(teacherFeedbackTable);
        model.setRowCount(0);
        for (Object[] row : schoolService.getTeacherFeedbackRows()) {
            model.addRow(row);
        }
    }

    private void loadStudentFeedback() throws SQLException {
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
        for (User teacher : schoolService.getUsersByRole(SchoolService.ROLE_TEACHER)) {
            model.addElement(new UserItem(teacher));
        }
        taskTeacherBox.setModel(model);
    }

    private void loadProgressTasks() throws SQLException {
        if (progressTaskBox == null) {
            return;
        }
        DefaultComboBoxModel<TaskItem> model = new DefaultComboBoxModel<>();
        for (Task task : schoolService.getTasks(currentUser.idNo)) {
            model.addElement(new TaskItem(task));
        }
        progressTaskBox.setModel(model);
    }

    private void loadReflectionTeachers() throws SQLException {
        if (reflectionTeacherBox == null) {
            return;
        }
        DefaultComboBoxModel<UserItem> model = new DefaultComboBoxModel<>();
        for (User teacher : schoolService.getUsersByRole(SchoolService.ROLE_TEACHER)) {
            model.addElement(new UserItem(teacher));
        }
        reflectionTeacherBox.setModel(model);
    }

    private boolean promptPasswordChange(boolean required) {
        JPasswordField newPassword = new JPasswordField(22);
        JPasswordField confirm = new JPasswordField(22);
        JPanel panel = cardPanel();
        addRow(panel, "New Password", newPassword, 0);
        addRow(panel, "Confirm", confirm, 1);
        addFull(panel, smallText("Use at least 8 characters with one letter and one number."), 2);
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, required ? "Change Default Admin Password" : "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) {
                if (required) {
                    JOptionPane.showMessageDialog(this, "The default admin password must be changed before using the app.");
                    logout();
                }
                return false;
            }
            String password = new String(newPassword.getPassword());
            if (!password.equals(new String(confirm.getPassword()))) {
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
        if (table == null || table.getRowCount() == 0) {
            showError("There is no data to export yet.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export " + defaultFileName);
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
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

    private void showApp() {
        if (root.getComponentCount() > 2) {
            root.remove(2);
        }
        root.add(createAppPanel(), "app");
        rootLayout.show(root, "app");
        refreshAll();
    }

    private void showLogin() {
        rootLayout.show(root, "login");
    }

    private void showRegister() {
        rootLayout.show(root, "register");
    }

    private void logout() {
        currentUser = null;
        loginPasswordField.setText("");
        showLogin();
    }

    private boolean isHead() {
        return currentUser != null && SchoolService.ROLE_HEAD.equals(currentUser.role);
    }

    private boolean isTeacher() {
        return currentUser != null && SchoolService.ROLE_TEACHER.equals(currentUser.role);
    }

    private boolean isStudent() {
        return currentUser != null && SchoolService.ROLE_STUDENT.equals(currentUser.role);
    }

    private void clearRegisterForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        idField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleBox.setSelectedIndex(0);
    }

    private void clearTaskForm() {
        taskSubjectField.setText("");
        taskClassField.setText("");
        taskDeadlineField.setText("");
        taskDetailsArea.setText("");
        taskDayBox.setSelectedIndex(0);
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        return panel;
    }

    private JPanel authCard(String title, String subtitle) {
        JPanel card = cardPanel();
        card.setPreferredSize(new Dimension(520, 520));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(TEXT);
        addFull(card, heading, 0);
        addFull(card, smallText(subtitle), 1);
        return card;
    }

    private JPanel contentPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        panel.setBackground(BACKGROUND);
        return panel;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)), BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        return panel;
    }

    private JPanel buttonRow(int columns) {
        JPanel panel = new JPanel(new GridLayout(0, columns, 12, 12));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel taskActions(JTable table) {
        JPanel actions = buttonRow(3);
        JButton refresh = secondaryButton("Refresh");
        refresh.addActionListener(event -> refreshAll());
        JButton progress = secondaryButton("Mark In Progress");
        progress.addActionListener(event -> updateSelectedTaskStatus(table, "In Progress"));
        JButton complete = primaryButton("Mark Completed");
        complete.addActionListener(event -> updateSelectedTaskStatus(table, "Completed"));
        actions.add(refresh);
        actions.add(progress);
        actions.add(complete);
        return actions;
    }

    private void addRow(JPanel panel, String label, Component field, int row) {
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.gridy = row;
        left.anchor = GridBagConstraints.WEST;
        left.insets = new Insets(8, 0, 8, 14);
        JLabel component = new JLabel(label);
        component.setFont(new Font("Segoe UI", Font.BOLD, 13));
        component.setForeground(TEXT);
        panel.add(component, left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1;
        right.gridy = row;
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        right.insets = new Insets(8, 0, 8, 0);
        panel.add(field, right);
    }

    private void addFull(JPanel panel, Component component, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8, 0, 8, 0);
        panel.add(component, constraints);
    }

    private GridBagConstraints centered() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        return constraints;
    }

    private JLabel smallText(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JTextArea textArea(int rows) {
        JTextArea area = new JTextArea(rows, 24);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
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
            new SchoolManagementSystem().setVisible(true);
        });
    }
}
