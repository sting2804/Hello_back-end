package com.test.api.repository;

import com.test.api.domain.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactRepository extends CrudRepository<Contact, Long>, PagingAndSortingRepository<Contact, Long> {
}
