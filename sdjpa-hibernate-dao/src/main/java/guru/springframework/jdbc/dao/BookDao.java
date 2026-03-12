package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

public interface BookDao {
    Book findBookByTitle(String cleanCode);

    Book getById(long l);

    Book saveNewBook(Book book);

    Book updateBook(Book saved);

    void deleteBookById(Long id);
}
