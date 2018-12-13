package com.example.demo.service;

import com.example.demo.domain.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.exception.BookAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookService.class);
    private final BookRepository repository;

    @Autowired
    public BookService(final BookRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Book saveBook(@NotNull @Valid final Book book) {
        LOGGER.debug("Creating {}", book);
        Optional<Book> existing = repository.findById(book.getId());
        if (existing.isPresent()) {
            throw new BookAlreadyExistsException(
                    String.format("There already exists a book with id=%s", book.getId()));
        }
        return repository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> getList() {
        LOGGER.debug("Retrieving the list of all users");
        return repository.findAll();
    }

    public Book getBook(Long bookId) {
        return repository.findById(bookId).get();
    }

    @Transactional
    public void deleteBook(final Long bookId) {
        LOGGER.debug("deleting {}", bookId);
        repository.deleteById(bookId);
    }

}
