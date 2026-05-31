📚 The Grand Library System
A Java desktop application for managing a library — books, members, and loan circulation — built with Java Swing and an OOP architecture.

✨ Features

Book Management — Add, edit, and delete Physical Books and E-Books
Member Management — Register and remove library members
Circulation — Borrow and return books with automatic due-date tracking
Dashboard — Live search across the book catalog with status indicators
Loan History — Tracks active and returned loans per member
Bilingual UI — Switch between English and Arabic at runtime
Data Persistence — All data is saved to local files


🏗️ Project Structure
libraryApp/
├── Book.java             # Abstract base class for all book types
├── PhysicalBook.java     # Physical book (14-day loan period)
├── EBook.java            # E-Book with download URL & file size (7-day loan)
├── DigitalMedia.java     # Digital media component (URL, size)
├── IMedia.java           # Interface for digital content
├── Member.java           # Library member model
├── LoanRecord.java       # Loan/borrowing history record
├── LibraryManagement.java# Singleton — core business logic
├── Login.java            # Login screen
└── GUI.java              # Main Swing UI (CardLayout, 4 panels)

🚀 Getting Started
Requirements: Java 11 or later
bash# Compile
javac libraryApp/*.java

# Run
java libraryApp.GUI

🖥️ UI Overview
PanelDescriptionDashboardBook table with live search and statsManage BooksAdd / edit / delete books (toggle E-Book fields)CirculationBorrow and return books by IDMembersRegister members, view active loans

🧱 Design Patterns Used

Singleton — LibraryManagement ensures one instance of the library
Abstract Class + Inheritance — Book → PhysicalBook / EBook
Interface — IMedia for digital content contract
Composition — EBook uses DigitalMedia component


📄 License
This project is for educational purposes.
