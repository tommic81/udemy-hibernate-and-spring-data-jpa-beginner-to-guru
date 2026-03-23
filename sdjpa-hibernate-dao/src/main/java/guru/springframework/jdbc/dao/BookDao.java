package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.List;

public interface BookDao {

    List<Book> findAll();
    Book findByISBN(String isbn);
    Book findBookByTitle(String cleanCode);

    Book getById(long l);

    Book saveNewBook(Book book);

    Book updateBook(Book saved);

    void deleteBookById(Long id);
}
