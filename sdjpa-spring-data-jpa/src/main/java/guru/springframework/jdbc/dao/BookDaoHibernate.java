package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BookDaoHibernate implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoHibernate(EntityManagerFactory emf) {
        this.emf = emf;
    }
    @Override
    public List<Book> findAllBooksSortByTitle(Pageable pageable) {
        return null;
    }

    @Override
    public List<Book> findAllBooks(Pageable pageable) {

        EntityManager em = getEntityManager();

        try{
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);
            query.setFirstResult(Math.toIntExact(pageable.getOffset()));
            query.setMaxResults(pageable.getPageSize());
            return query.getResultList();

        }finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAllBooks(int pageSize, int offset) {
        return null;
    }

    @Override
    public List<Book> findAllBooks() {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b", Book.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        return null;
    }

    @Override
    public void deleteBookById(Long id) {

    }

    @Override
    public Book getById(Long id) {
        return null;
    }

    @Override
    public Book updateBook(Book saved) {
        return null;
    }

    @Override
    public Book findBookByTitle(String cleanCode) {
        return null;
    }

    private EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}
