CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    id_no TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    password_changed INTEGER NOT NULL DEFAULT 1,
    role TEXT NOT NULL CHECK (role IN ('Head of School', 'Teacher', 'Student')),
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject TEXT NOT NULL,
    class_name TEXT NOT NULL,
    day TEXT NOT NULL,
    teacher_id_no TEXT NOT NULL,
    details TEXT NOT NULL,
    deadline TEXT,
    status TEXT NOT NULL DEFAULT 'Assigned',
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id_no) REFERENCES users(id_no)
);

CREATE TABLE IF NOT EXISTS teacher_feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    teacher_id_no TEXT NOT NULL,
    task_id INTEGER,
    coverage TEXT NOT NULL,
    notes TEXT,
    preparedness INTEGER NOT NULL,
    delivery INTEGER NOT NULL,
    enjoyment INTEGER NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id_no) REFERENCES users(id_no),
    FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS student_feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id_no TEXT NOT NULL,
    teacher_id_no TEXT,
    subject TEXT NOT NULL,
    clarity INTEGER NOT NULL,
    engagement INTEGER NOT NULL,
    comfort INTEGER NOT NULL,
    pacing INTEGER NOT NULL,
    rating INTEGER NOT NULL,
    comments TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id_no) REFERENCES users(id_no)
);
