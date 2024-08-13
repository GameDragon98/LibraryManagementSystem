package com.mycompany.librarymanagementsystem;
import java.time.LocalDate;
public class Book 
{
    private String title;
    private String author;
    private String isbn;
    private boolean available;
    private LocalDate borrowedBookDate;
    private LocalDate dueDate;

    // Constructor to initialize a Book object
    public Book(String bookTitle, String bookAuthor, String bookIsbn) 
    {
        this.title = bookTitle;
        this.author = bookAuthor;
        this.isbn = bookIsbn;
        this.available = true;
        this.borrowedBookDate = null;
        this.dueDate = null;
    }

    // Getter methods
    public String getTitle()
    {
        return title; 
    }
    public String getAuthor() 
    { 
        return author; 
    }
    public String getIsbn() 
    { 
        return isbn; 
    }  
    public boolean isAvailable()
    { 
        return available; 
    }
    public LocalDate getBorrowedDate()
    {
        return borrowedBookDate;
    }
    public LocalDate getDueDate()
    {
        return dueDate;
    }
    
    
    // Setter method
    public void setAvailable(boolean available) 
    {
        this.available = available;
    }
    public void setBorrowedDate(LocalDate borrowedDate) 
    {
        this.borrowedBookDate = borrowedDate;
        this.dueDate = borrowedDate.plusDays(14);
    }
    public void setDueDate(LocalDate dueDate) 
    {
        this.dueDate = dueDate;
    }
    
    
    // Method to toggle the availability status of the book
    public void toggleAvailability() 
    { 
        available = !available; 
    }

    //Method that checks a book that has been borrowed by member if it's overdue
    public boolean isBookOverdue()
    {
        return LocalDate.now().isAfter(dueDate);
    }
    
    // Method to represent Book object as a string
    @Override
    public String toString() 
    {
        return "Book = (Title: " + title + ", Author: " + author + ", ISBN: " + isbn + ", Available: " + available + ")";
    }
}

