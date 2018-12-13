package com.example.demo.service;

import static org.junit.Assert.*;

import com.example.demo.domain.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.exception.BookAlreadyExistsException;
import com.example.demo.utils.UserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @Before
    public void setUp() throws Exception {
        bookService = new BookService(bookRepository);
    }

    @Test
    public void shouldSaveNewUser_GivenThereDoesNotExistOneWithTheSameId_ThenTheSavedUserShouldBeReturned() throws Exception {
        final Book savedBook = stubRepositoryToReturnUserOnSave();
        final Book book = UserUtil.createBook();
        final Book returnedBook = bookService.saveBook(book);
        // verify repository was called with book
        verify(bookRepository, times(1)).save(book);
        assertEquals("Returned book should come from the repository", savedBook, returnedBook);
    }

    private Book stubRepositoryToReturnUserOnSave() {
        Book book = UserUtil.createBook();
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        return book;
    }

    @Test
    public void shouldSaveNewUser_GivenThereExistsOneWithTheSameId_ThenTheExceptionShouldBeThrown() throws Exception {
        stubRepositoryToReturnExistingUser();
        try {
            bookService.saveBook(UserUtil.createBook());
            fail("Expected exception");
        } catch (BookAlreadyExistsException ignored) {
        }
        verify(bookRepository, never()).save(any(Book.class));
    }

    private void stubRepositoryToReturnExistingUser() {
        final Book book = UserUtil.createBook();
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
    }

    @Test
    public void shouldListAllUsers_GivenThereExistSome_ThenTheCollectionShouldBeReturned() throws Exception {
        stubRepositoryToReturnExistingUsers(10);
        Collection<Book> list = bookService.getList();
        assertNotNull(list);
        assertEquals(10, list.size());
        verify(bookRepository, times(1)).findAll();
    }

    private void stubRepositoryToReturnExistingUsers(int howMany) {
        when(bookRepository.findAll()).thenReturn(UserUtil.createBookList(howMany));
    }

    @Test
    public void shouldListAllUsers_GivenThereNoneExist_ThenTheEmptyCollectionShouldBeReturned() throws Exception {
        stubRepositoryToReturnExistingUsers(0);
        Collection<Book> list = bookService.getList();
        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(bookRepository, times(1)).findAll();
    }

}