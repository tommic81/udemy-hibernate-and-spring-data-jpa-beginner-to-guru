package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookDaoImpl implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> typedQuery = em.createNamedQuery("book_find_all", Book.class);
            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findByISBN(String isbn) {
        EntityManager em = getEntityManager();
        try{
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);

            Book book = query.getSingleResult();
            return book;
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitle(String bookTitle) {
        EntityManager em = getEntityManager();
        try {
        /*TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b " +
                "WHERE b.title = :title", Book.class);*/

            TypedQuery<Book> query = em.createNamedQuery("find_by_title", Book.class);
            query.setParameter("title", bookTitle);

            Book b = query.getSingleResult();

            return b;
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        EntityManager em = getEntityManager();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
            Root<Book> root = criteriaQuery.from(Book.class);

            ParameterExpression<String> titleParam = criteriaBuilder.parameter(String.class);
            Predicate titlePred = criteriaBuilder.equal(root.get("title"), titleParam);
            criteriaQuery.select(root).where(titlePred);

            TypedQuery<Book> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(titleParam, title);

            return typedQuery.getSingleResult();
        } finally {
            em.close();
        }
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
