package guru.springframework.jdbc.dao;
import guru.springframework.jdbc.domain.Author;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class AuthorDaoJDBCTemplate implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoJDBCTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Author> findAllAuthorsByLastName(String lastname, Pageable pageable) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM author where last_name = ?");

        if(pageable.getSort().getOrderFor("firstName") != null){
            sb.append(" order by first_name ").append(pageable.getSort().getOrderFor("firstName").getDirection().name());
        }
        sb.append(" limit ? offset ?");
        return jdbcTemplate.query(sb.toString(), getAuthorMapper(), lastname, pageable.getPageSize(), pageable.getOffset());
    }

    @Override
    public Author getById(Long id) {
        return  jdbcTemplate.queryForObject("SELECT * FROM author where id = ?", getAuthorMapper(), id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
         return jdbcTemplate.queryForObject("SELECT * FROM author where firstName = ? and lastName = ?", getAuthorMapper(), firstName, lastName);
    }

    @Override
    public Author saveNewAuthor(Author author) {
        jdbcTemplate.update("INSERT INTO author (firstName, lastName) VALUES (?, ?)",
                author.getFirstName(), author.getLastName());

        Long createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return this.getById(createdId);
    }

    @Override
    public Author updateAuthor(Author author) {
        jdbcTemplate.update("UPDATE author set firstName = ?, lastName = ? where id = ?",
                author.getFirstName(), author.getLastName(), author.getId());

        return this.getById(author.getId());
    }

    @Override
    public void deleteAuthorById(Long id) {
        jdbcTemplate.update("DELETE from author where id = ?", id);
    }

    private AuthorMapper getAuthorMapper(){
        return new AuthorMapper();
    }
}
