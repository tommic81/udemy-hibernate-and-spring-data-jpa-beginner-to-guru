package guru.springframework.sdjpaintro.bootstrap;

import guru.springframework.sdjpaintro.domain.Author;
import guru.springframework.sdjpaintro.domain.Book;
import guru.springframework.sdjpaintro.repositories.AuthorRepository;
import guru.springframework.sdjpaintro.repositories.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "default"})
@Component
public class DataInitializer implements CommandLineRunner {
    public final BookRepository bookRepository;

    public final AuthorRepository authorRepository;

    public DataInitializer(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        bookRepository.deleteAll();

        Book bookDDD = new Book("domain Driven Design", "123", "RandomHouse", null);

        System.out.println("Id: " + bookDDD.getId());

        Book savedDDD = bookRepository.save(bookDDD);

        System.out.println("Id: " + bookDDD.getId());

        Book bookSIA = new Book("Spring In Action", "234234", "Oriely", null);
        Book savedSIA = bookRepository.save(bookSIA);

        Author author = new Author("Howard", "Lovecraft");
        authorRepository.save(author);

        bookRepository.findAll().forEach(book -> {
            System.out.println("Book Id: " + book.getId());
            System.out.println("Book Title: " + book.getTitle());
        });
    }
}
