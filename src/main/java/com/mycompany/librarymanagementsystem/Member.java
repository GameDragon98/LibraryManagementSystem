package com.mycompany.librarymanagementsystem;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Member
{
    private String  name;
    private String  email;
    private List<Book> borrowedBooks;
    private List<LocalDate> borrowedDates;
    private List<Double> overdueFines;
    
    
    // Constructor to initialize a Member object
    public Member(String userName, String userEmail)
    {
        this.name = userName;
        if (!isValidEmail (userEmail))
        {
            throw new IllegalArgumentException ("Invalid email format."); 
        }
        this.email = userEmail;
        this.borrowedBooks = new ArrayList<>();
        this.borrowedDates = new ArrayList<>();
        this.overdueFines = new ArrayList<>();
    }
    
    // Method checks email format, to see if it's valid 
    private boolean isValidEmail(String userEmail)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return userEmail.matches(emailRegex);
    }
    
    //Getter methods
    public String getName() 
    {
        return name;
    }
    public String getEmail() 
    {
        return email;
    }
    public List<Book> getBorrowedBooks() 
    {
        return borrowedBooks;
    }
    
    
    // Setter method
    public void setBorrowedBooks(List<Book> borrowedBooks) 
    {
        this.borrowedBooks = borrowedBooks;
    }
    
    
    
    // Method to allow a member to borrow a book
    public void borrowBook(Book libraryBook) 
    {
        if (libraryBook.isAvailable()) 
        {
            libraryBook.setBorrowedDate(LocalDate.now());
            borrowedBooks.add(libraryBook);
            borrowedDates.add(LocalDate.now());
            overdueFines.add(0.0);
            libraryBook.toggleAvailability();
        } 
        else 
        {
            throw new IllegalStateException("Book is not available for borrowing");
        }
    }
    
    // Method to allow a member to return a borrowed book
    public void returnBook(Book libraryBook) 
    {
        if (borrowedBooks.contains(libraryBook)) 
        {
            int index = borrowedBooks.indexOf(libraryBook);
            libraryBook.toggleAvailability();
            borrowedBooks.remove(index);
            borrowedDates.remove(index);
            overdueFines.remove(index);
        } 
        else 
        {
            System.out.println("Book not borrowed by this member!");
        }
    } 
    
    // Method to update overdue fines for each day a book is overdue
    public void updateOverdueFines() 
    {
        for (int i = 0; i < borrowedBooks.size(); i++) 
        {
            if (i < borrowedDates.size()) 
            {
                LocalDate borrowedDate = borrowedDates.get(i);
                Book book = borrowedBooks.get(i);
                if (book.isBookOverdue()) 
                {
                    long daysOverdue = LocalDate.now().toEpochDay() - borrowedDate.toEpochDay();
                    double overdueFine = calculateOverdueFine(daysOverdue);
                    overdueFines.set(i, overdueFine);
                }
            }
        }
    }
    
    
    //Method to calculate overdue fines for each day a book is overdue(Calculates the total overdue fines for all borrowed books of a member)
    public double calculateTotalOverdueFines() 
    {
        double totalOverdueFines = 0.0;
        for (double fine : overdueFines) 
        {
            totalOverdueFines += fine;
        }
        return totalOverdueFines;
    }

    // Method to calculate overdue fine for a book (Calculates the overdue fine for a single book)
    public double calculateOverdueFine(long daysOverdue) 
    {
        return 1.50 * daysOverdue;
    }
}
