# Library Management System ReadMe

Hey there! 👋
## Objective:
The objective of this project is to design and implement a Library Management System in Java. The system allows users to perform various operations such as adding books, searching for books by title or author, registering new members, checking out books, and returning books.

## Design Decisions:
1. **Architecture**: I chose a simple object-oriented architecture for the Library Management System. This architecture provides modularity, encapsulation, and ease of understanding.
2. **Data Storage**: I utilized ArrayLists to store books and members, offering dynamic sizing and efficient data manipulation operations.
3. **Input Handling**: Scanner was employed for user input handling due to its simplicity and ease of use.
4. **Error Handling**: Exception handling was implemented to deal with invalid inputs and ensure the robustness of the system.

## Objectives Addressed:
1. **Add Book**:
   - Users can add books by providing the title, author, and ISBN. This functionality allows for the expansion of the library's collection.
   
2. **Search Books**:
   - Users can search for books by title or author using keywords. This feature enhances user experience by enabling efficient book retrieval.

3. **Register Member**:
   - Users can register new members by entering their name and email. This functionality facilitates the management of library memberships.

4. **Checkout Book**:
   - Members can borrow available books by selecting the desired book and providing their email. This functionality enables members to borrow books for reading.

5. **Return Book**:
   - Members can return borrowed books by providing the book's ISBN. This feature ensures proper book inventory management and member accountability.

6. **Background Tasks**:
   - The system implements two background tasks using threads:
     - **Notification Task**: Periodically checks for overdue books and notifies members about their status.
     - **Fine Calculation Task**: Periodically calculates overdue fines for borrowed books.

7. **Date and Time Utilities**:
   - The system utilizes Java's LocalDate class to manage borrowed and due dates for books, enabling accurate tracking of book borrowing periods.

8. **Fine Calculation**:
   - The fine calculation task utilizes the ChronoUnit class to calculate the number of days a book is overdue, allowing for precise fine calculation based on overdue duration.

9. **JSON Data Persistence**:
   - Data for books and members are stored in JSON files using Java's JSON.simple library, ensuring data persistence across system restarts.

10. **Streams**:
    - Streams are used for reading and writing data to JSON files, providing efficient and concise data manipulation operations.

11. **Error Handling**:
    - Proper error handling is implemented throughout the system to handle exceptions such as invalid inputs and file I/O errors, ensuring the reliability and stability of the application.


Feel free to explore the code, make improvements, or just use it as a reference for your own projects. If you have any questions or feedback, don't hesitate to reach out!

Happy coding! 🚀
