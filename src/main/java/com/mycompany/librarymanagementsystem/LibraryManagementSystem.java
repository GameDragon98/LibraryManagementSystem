package com.mycompany.librarymanagementsystem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;



public class LibraryManagementSystem // Parent class/superclass 
{
    private List<Book> books; 
    private List<Member> members; 
    private Scanner input;
    private Thread notificationThread;
    private Thread fineCalculationThread;
    private final String jsonBooksFile = "books.json";
    private final String jsonMembersFile = "members.json";
    
    // Constructor to initialize LibraryManagementSystem object
    public LibraryManagementSystem() 
    {
        books = new ArrayList<>(); 
        members = new ArrayList<>();
        input = new Scanner(System.in); 
        notificationThread = new Thread(new booksNotificationTask());
        fineCalculationThread = new Thread(new fineCalculationTask());
    } 

    // Method to add a new book to the library
    public void addBook()
    {
        System.out.print("Enter book title: ");
        String title = input.nextLine();
        System.out.print("Enter book author: ");
        String author = input.nextLine();
        System.out.print("Enter book ISBN: ");
        String isbn = input.nextLine();
        Book newBook = new Book(title, author, isbn);
        books.add(newBook);
        System.out.println("Book added successfully!");
    }
    
    // Method to search for books based on a keyword
    public void searchBooks() 
    {
        System.out.print("Enter one of the following keywords(Title/Author/ISBN) to search for books: ");
        String keyword = input.nextLine().toLowerCase();
        List<Book> searchResults = new ArrayList<>();
        for (Book libraryBook : books)
        {
            if (libraryBook.getTitle().toLowerCase().contains(keyword) || libraryBook.getAuthor().toLowerCase().contains(keyword))
            {
                searchResults.add(libraryBook);
            }
        }
        if (searchResults.isEmpty()) 
        {
            System.out.println("No matching books found.");
        }
        else 
        {
            System.out.println("Search results:");
            for (int i = 0; i < searchResults.size(); i++) 
            {
                Book book = searchResults.get(i);
                System.out.println((i + 1) + ". " + book); 
            }
        }
    }
    
    // Method to register a new member
    public void registerMember() 
    {
        do{
            System.out.print("Enter member name: ");
            String name = input.nextLine();
            System.out.print("Enter member email: ");
            String email = input.nextLine();
            try 
            {
                members.add(new Member(name, email)); 
                System.out.println("Member added successfully!");
                break;
            } 
            catch (IllegalArgumentException e) 
            {
                System.out.println(e.getMessage());
            }
        }
        while(true);
    }
    
    
    // Method to allow a member to checkout a book
    public void checkoutBook() 
    {
        if (books.isEmpty()) 
        {
            System.out.println("No books available to checkout.");
            return;
        }

        List<Book> availableBooks = new ArrayList<>();
        for (Book libraryBook : books) 
        {
            if (libraryBook.isAvailable()) 
            {
                availableBooks.add(libraryBook);
            }
        }

        if (availableBooks.isEmpty()) 
        {
            System.out.println("No books available to checkout.");
            return;
        }

        System.out.println("Available books:");
        for (int i = 0; i < availableBooks.size(); i++) 
        {
            Book libraryBook = availableBooks.get(i);
            System.out.println((i + 1) + ". " + libraryBook);
        }

        int selectedIndex;
        do
        {
            System.out.print("Enter the index of the book to checkout: ");
            String inputIndex = input.nextLine();
            try 
            {
                selectedIndex = Integer.parseInt(inputIndex);
                if (selectedIndex < 1 || selectedIndex > availableBooks.size()) 
                {
                    System.out.println("Invalid index! Please enter a number between 1 and " + availableBooks.size() + ".");
                } 
                else 
                {
                    break;
                }
            } 
            catch (NumberFormatException e) 
            {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
        while (true); 

        Book selectedBook = availableBooks.get(selectedIndex - 1);
        System.out.print("Enter member email: ");
        String email = input.nextLine();
        Member borrowingMember = null;
        for (Member member : members) 
        {
            if (member.getEmail().equals(email)) 
            {
                borrowingMember = member;
                break; 
            }
        }

        if (borrowingMember != null) 
        {
            try {
                    if (selectedBook.isAvailable()) 
                    {
                        borrowingMember.borrowBook(selectedBook); 
                        selectedBook.setBorrowedDate(LocalDate.now());
                        System.out.println("Book checked out successfully!");
                    } 
                    else 
                    {
                        System.out.println("Selected book is not available for borrowing.");
                    }
                } 
                catch (IllegalStateException e) 
                {
                    System.out.println(e.getMessage());
                }
        } 
        else 
        {
            System.out.println("Member not found!");
        }
    }
    
    // Method to allow a member to return a borrowed book
    public void returnBook() 
    {
        if (books.isEmpty()) 
        {
            System.out.println("No books to return.");
            return;
        }

        System.out.print("Enter member email: ");
        String email = input.nextLine();

        Member borrowingMember = findMemberByEmail(email);
        if (borrowingMember == null) 
        {
            System.out.println("Member not found!");
            return;
        }

        System.out.print("Enter book ISBN to return: ");
        String isbn = input.nextLine();

        boolean bookFound = false;
        for (Book libraryBook : borrowingMember.getBorrowedBooks()) 
        {
            if (libraryBook.getIsbn().equals(isbn)) 
            {
                borrowingMember.returnBook(libraryBook);
                System.out.println("Book returned successfully!");
                bookFound = true;
                break;
            }
        }

        if (!bookFound) 
        {
            System.out.println("You are not currently borrowing this book.");
        }
    }
    
    // The starting point of the background threads for the book notifications and the fine processing
    public void backgroundTask()
    {
        notificationThread.start();
        fineCalculationThread.start();
    }
    
    //Stop the threads
    public void stopBackgroundTask() 
    {
       notificationThread.interrupt();
        fineCalculationThread.interrupt();
    }
    
    //Inner class for background notification task about the status of the books
    private class booksNotificationTask implements Runnable
    {
        public void run() 
        {
            try 
            {
                while (!Thread.currentThread().isInterrupted()) 
                {
                    createNotificationLog();
                    Thread.sleep(60000);
                }
            } 
            catch (InterruptedException e) 
            {
                System.out.println("Books notification task interrupted. Exiting...");
            }
        }
        
        private void createNotificationLog() 
        {
            try (FileWriter file = new FileWriter("library_data/notification log.txt", true)) 
            {
                for (Member member : members) 
                {
                    for (Book book : member.getBorrowedBooks()) 
                    {
                        if (book.isBookOverdue()) 
                        {
                            long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
                            double overdueFine = member.calculateOverdueFine(daysOverdue);
                            file.write("Overdue Notification -\nUser: " + member.getName() + "\nEmail: " + member.getEmail()
                                    + "\nBook Title: " + book.getTitle() + "\nOverdue Date: " + book.getDueDate()
                                    + "\nOverdue Fine: " + overdueFine + "\n\n");
                        } 
                        else 
                        {
                            file.write("Due Notification -\nUser: " + member.getName() + "\nEmail: " + member.getEmail()
                                    + "\nBook Title: " + book.getTitle() + "\nDue Date: " + book.getDueDate() + "\n\n");
                        }
                    }
                }
                System.out.println("The Notification log has been generated successfully...");
            } 
            catch (IOException e) 
            {
                System.err.println("An error occurred while generating the notification log.");
                System.err.println("Error message: " + e.getMessage());
            }
        }  
    }
    
    //Inner class for background fine calculation task for overdue books
    private class fineCalculationTask implements Runnable
    {
        public void run() 
        {
            try 
            {
                while (!Thread.currentThread().isInterrupted()) 
                {
                    System.out.println("Fine calculation task has started...");
                    for (Member member : members) 
                    {
                        
                        member.updateOverdueFines();
                        double totalFines = member.calculateTotalOverdueFines();
                        if (totalFines > 0) 
                        {
                            System.out.println("Fines calculated for " + member.getName() + ": R " + totalFines);
                        }
                    }
                    Thread.sleep(60000);
                }
            } 
            catch (InterruptedException e) 
            {
                System.out.println("Fine calculation task interrupted. Exiting...");
            }
        }
    }
    
    
    //Method that save data of the books and members to json files or in sort saves library data to json files
    public void saveData() 
    {
        try 
        {
            // Save books data to books.json 
            try (FileWriter file = new FileWriter("./library_data/" + jsonBooksFile)) 
            {
                JSONObject bookObj = new JSONObject();
                JSONArray booksArray = new JSONArray();
                for (Book book : books) 
                {
                    bookObj.put("title", book.getTitle());
                    bookObj.put("author", book.getAuthor());
                    bookObj.put("isbn", book.getIsbn());
                    bookObj.put("available", book.isAvailable());
                    bookObj.put("borrowedDate", book.getBorrowedDate() == null ? "none" : book.getBorrowedDate().toString());
                    bookObj.put("dueDate", book.getDueDate() == null ? "none" : book.getDueDate().toString());
                    booksArray.add(bookObj);
                }
                file.write(booksArray.toJSONString());
            }

            // Save members data to members.json
            try (FileWriter file = new FileWriter("./library_data/" + jsonMembersFile)) 
            {
                JSONArray memberArray = new JSONArray();
                for (Member member : members) 
                {
                    JSONObject memberObject = new JSONObject();
                    memberObject.put("name", member.getName());
                    memberObject.put("email", member.getEmail());
                    JSONArray borrowedBooksArray = new JSONArray();
                    for (Book book : member.getBorrowedBooks()) 
                    {
                        JSONObject bookObj = new JSONObject();
                        bookObj.put("title", book.getTitle());
                        bookObj.put("author", book.getAuthor());
                        bookObj.put("isbn", book.getIsbn());
                        bookObj.put("borrowedDate", book.getBorrowedDate().toString());
                        bookObj.put("dueDate", book.getDueDate().toString());
                        borrowedBooksArray.add(bookObj);
                    }
                    memberObject.put("borrowedBooks", borrowedBooksArray);
                    memberArray.add(memberObject);
                }
                file.write(memberArray.toJSONString());
            }
        } 
        catch (IOException e) 
        {
            System.err.println("Error saving library data: " + e.getMessage());
        }
    }
    
    //Method that load the books and members data from json files or in sort loads library data from json files
    public void loadData()
    {
        // Load books data from JSON file
        try (FileReader fileReader = new FileReader("./library_data/" + jsonBooksFile)) 
        {
            JSONParser parser = new JSONParser();
            JSONArray booksArray = (JSONArray) parser.parse(fileReader);

            // Process each book object in the JSON array
            for (Object obj : booksArray) 
            {
                JSONObject bookObj = (JSONObject) obj;
                String title = (String) bookObj.get("title");
                String author = (String) bookObj.get("author");
                String isbn = (String) bookObj.get("isbn");
                boolean available = (boolean) bookObj.get("available");
                String borrowedDateString = (String) bookObj.get("borrowedDate");
                String dueDateString = (String) bookObj.get("dueDate");
                
                if(borrowedDateString.equals("none"))
                {
                    borrowedDateString = null;
                }
                if(dueDateString.equals("none"))
                {
                    dueDateString = null;
                }

                LocalDate borrowedDate = LocalDate.parse(borrowedDateString);
                LocalDate dueDate = LocalDate.parse(dueDateString);

                // Create Book object and add to the books list
                Book book = new Book(title, author, isbn);
                book.setAvailable(available);
                book.setBorrowedDate(borrowedDate);
                book.setDueDate(dueDate);
                books.add(book);
            }
        } 
        catch (IOException | ParseException e) 
        {
            System.err.println("Error loading books data: " + e.getMessage());
            System.err.println("Creating empty books.json file...");
            createEmptyJsonFile("./library_data/" + jsonBooksFile);
        }
        catch(Exception z)
        {
         System.err.println("Error loading books data: " + z.getMessage());
         createEmptyJsonFile("./library_data/" + jsonBooksFile);
        }
        // Load members data from JSON file
        try (FileReader fileReader = new FileReader("./library_data/" + jsonMembersFile)) 
        {
            JSONParser parser = new JSONParser();
            JSONArray membersArray = (JSONArray) parser.parse(fileReader);

            // Process each member object in the JSON array
            for (Object obj : membersArray) 
            {
                JSONObject memberObj = (JSONObject) obj;
                String name = (String) memberObj.get("name");
                String email = (String) memberObj.get("email");

                JSONArray borrowedBooksArray = (JSONArray) memberObj.get("borrowedBooks");
                List<Book> borrowedBooks = new ArrayList<>();

                // Process each borrowed book object in the array
                for (Object bookObj : borrowedBooksArray) 
                {
                    JSONObject borrowedBookObj = (JSONObject) bookObj;
                    String bookTitle = (String) borrowedBookObj.get("title");
                    String bookAuthor = (String) borrowedBookObj.get("author");
                    String bookIsbn = (String) borrowedBookObj.get("isbn");
                    String borrowedBookDateString = (String) borrowedBookObj.get("borrowedDate");
                    String dueDateString = (String) borrowedBookObj.get("dueDate");

                    LocalDate borrowedBookDate = LocalDate.parse(borrowedBookDateString);
                    LocalDate dueDate = LocalDate.parse(dueDateString);

                    Book borrowedBook = new Book(bookTitle, bookAuthor, bookIsbn);
                    borrowedBook.setBorrowedDate(borrowedBookDate); 
                    borrowedBook.setDueDate(dueDate); 
                    borrowedBooks.add(borrowedBook);
                }

                // Create Member object and add to the members list
                Member member = new Member(name, email);
                member.setBorrowedBooks(borrowedBooks); 
                members.add(member);
            }
        } 
        catch (IOException | ParseException e) 
        {
            System.err.println("Error loading members data: " + e.getMessage());
            System.err.println("Creating empty members.json file...");
            createEmptyJsonFile("./library_data/" + jsonMembersFile);
        }
        catch(Exception z)
        {
         System.err.println("Error loading books data: " + z.getMessage());
         createEmptyJsonFile("./library_data/" + jsonMembersFile);
        }
    }
    
    private void createEmptyJsonFile(String filename) 
    {
        try (FileWriter file = new FileWriter(filename)) {
            file.write("[]");
            System.out.println(filename + " created successfully.");
        } catch (IOException e) {
            System.err.println("Error creating " + filename + ": " + e.getMessage());
        }
    }
    
    // Method to check due dates for a member's borrowed books
    public void checkDueDates() 
    {
        System.out.print("Enter member email to check due dates: ");
        String email = input.nextLine();
        Member member = findMemberByEmail(email);
        if (member != null) 
        {
            List<Book> borrowedBooks = member.getBorrowedBooks();
            if (!borrowedBooks.isEmpty()) 
            {
                System.out.println("Due dates for books borrowed by " + member.getName() + ":");
                for (Book book : borrowedBooks) 
                {
                    System.out.println("Book: " + book.getTitle() + ", Due Date: " + book.getDueDate());
                }
            } 
            else 
            {
                System.out.println("No books currently borrowed by this member.");
            }
        } 
        else 
        {
            System.out.println("Member not found!");
        }
    }

    // Method to find a member by email
    private Member findMemberByEmail(String email) 
    {
        for (Member member : members) 
        {
            if (member.getEmail().equals(email)) 
            {
                return member;
            }
        }
        return null;
    }
    
    // Method to view fines for a member
    public void viewFines() 
    {
        System.out.print("Enter member email to view fines: ");
        String email = input.nextLine();
        Member member = findMemberByEmail(email);
        if (member != null) 
        {
            double totalFines = member.calculateTotalOverdueFines();
            System.out.println("Total fines for " + member.getName() + ": R" + totalFines);
        } 
        else 
        {
            System.out.println("Member not found!");
        }
    }
    
    
    
    public static void main(String[] args) 
    {
        LibraryManagementSystem library = new LibraryManagementSystem();
        library.loadData();
        library.backgroundTask();
        boolean runProgram = true;
        do
        {   
            
            try {
                System.out.println("\nLibrary Management System Menu:\n1. Add Book\n2. Search Books\n3. Register Member\n4. Checkout Book\n5. Return Book\n6. Check Due Dates\n7. View Fines\n0. Exit\nEnter your choice: ");
                int choice = Integer.parseInt(library.input.nextLine());
                switch (choice) 
                {
                    case 1: 
                        library.addBook(); 
                        break;
                    case 2: 
                        library.searchBooks(); 
                        break;
                    case 3: 
                        library.registerMember();
                        break;
                    case 4: 
                        library.checkoutBook(); 
                        break;
                    case 5: 
                        library.returnBook(); 
                        break;
                    case 6:
                        library.checkDueDates();
                        
                        break;
                    case 7:
                        library.viewFines();
                        break;    
                    case 0: 
                        System.out.println("Exiting..."); 
                        library.input.close();
                        library.stopBackgroundTask();
                        runProgram = false;
                        break;
                    default: 
                        System.out.println("Invalid choice! Please enter a number between 0 and 7.");
                }
            }
            catch (NumberFormatException e) 
            {
                System.out.println("Invalid choice! Please enter a number.");
            }
            library.saveData();
        }
        while (runProgram);
        library.saveData();
    }
}
