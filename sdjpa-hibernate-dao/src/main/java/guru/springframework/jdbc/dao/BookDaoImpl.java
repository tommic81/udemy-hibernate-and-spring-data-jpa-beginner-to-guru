package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;

@Component
public class BookDaoImpl implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Book findBookByTitle(String bookTitle) {
        EntityManager em = getEntityManager();
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b " +
                "WHERE b.title = :title", Book.class);

        query.setParameter("title", bookTitle);

        Book b = query.getSingleResult();
        em.close();
        return b;
    }

    @Override
    public Book getById(long id) {
        return getEntityManager().find(Book.class, id);
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();

    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.flush();
        em.getTransaction().commit();

        em.close();
        return book;
    }

    @Override
    public Book updateBook(Book saved) {
        EntityManager em = getEntityManager();
        //em.joinTransaction();
        em.getTransaction().begin();
        em.merge(saved);
        em.flush();
        em.clear();
        Book b =  em.find(Book.class, saved.getId());
        em.getTransaction().commit();
        em.close();
        return b;
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Book author = em.find(Book.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }
}
