# School Teaching & Syllabus Tracking System

## Documentation

Project documentation and design files can be viewed below:


- 📄 [System Design Document](Design..pdf)
- 📄 [Development Document](Development..pdf)

A role-based School Management System developed to improve teaching organization, monitor syllabus coverage, and enhance communication between the Head of School, teachers, and students.

This system centralizes teaching planning, task allocation, and feedback collection into one platform, helping schools ensure that lessons are completed on schedule and that student learning outcomes are continuously evaluated.

---

## Overview

Many schools face challenges in tracking syllabus completion and monitoring teaching progress effectively. Teachers may struggle with daily planning, while administrators lack a centralized way to monitor lesson delivery and collect feedback from students.

The School Management System addresses this problem by:

- Allowing the Head of School to assign daily teaching tasks and deadlines.
- Enabling teachers to submit daily checklists and reflections.
- Allowing students to provide feedback on lessons.
- Providing administrators with a centralized view of teaching progress and feedback.

The system promotes accountability, organization, and improved communication within the school environment.

---

## Features

### User Management
- Role-based authentication system.
- Separate portals for:
  - Head of School
  - Teachers
  - Students
- Secure login with encrypted password storage.
- User data stored securely in a database.

### Head of School Portal
- Assign teaching tasks to teachers.
- Add subject, class, day, and deadlines.
- Monitor teacher progress and syllabus coverage.
- View teacher feedback submissions.
- View student reflections.
- Send emails to teachers based on feedback and progress.

### Teacher Portal
- View assigned teaching tasks.
- Submit daily checklist and reflections.
- Provide feedback on lesson preparation and delivery.
- Report syllabus coverage status.

### Student Portal
- Submit lesson reflections.
- Evaluate teaching clarity and engagement.
- Provide additional comments for evaluation.

### Database Management
- Centralized storage using SQLite.
- Stores:
  - User signups
  - Teacher task details
  - Teacher feedback
  - Student feedback

---

## System Architecture

### Technologies Used
- **Programming Language:** Java
- **Paradigm:** Object-Oriented Programming (OOP)
- **Database:** SQLite
- **Database Connectivity:** JDBC

### OOP Concepts Applied
- Encapsulation for secure data handling.
- Inheritance for shared user behaviors.
- Polymorphism for role-based functionality.
- Abstraction for managing system complexity.

---

## System Workflow

1. Users create accounts and select their role.
2. The Head of School assigns teaching tasks to teachers.
3. Teachers complete lessons and submit feedback.
4. Students submit reflections on lessons.
5. The Head of School reviews feedback and monitors progress.
6. Emails are sent to teachers when necessary.

---

## Database Structure

The system uses one SQLite database containing four main tables:

- **Signup Table**
  - Stores user information and roles.

- **Teacher Details Table**
  - Stores assigned teaching tasks and deadlines.

- **Teacher Feedback Table**
  - Stores teacher reflections and progress reports.

- **Student Feedback Table**
  - Stores student evaluations and comments.


## Installation

1. Clone the repository:

```bash
git clone https://github.com/mikemaeda/school-management-system.git
