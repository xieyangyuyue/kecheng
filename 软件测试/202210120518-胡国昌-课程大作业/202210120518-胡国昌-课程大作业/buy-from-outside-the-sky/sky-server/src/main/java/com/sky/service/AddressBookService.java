package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void save(AddressBook addressBook);

    List<AddressBook> list();

    void updateById(Long id);

    AddressBook getAddressById(Long id);

    void updateAddress(AddressBook addressBook);

    AddressBook getDefault();

    void delete(Long id);
}
