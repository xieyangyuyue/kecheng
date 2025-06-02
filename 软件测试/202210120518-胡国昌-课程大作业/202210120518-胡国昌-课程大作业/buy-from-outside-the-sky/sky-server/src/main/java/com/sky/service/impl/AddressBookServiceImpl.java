package com.sky.service.impl;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void save(AddressBook addressBook) {
//        小程序前端缺陷没有设置token
        addressBook.setUserId(1l);
        addressBookMapper.save(addressBook);
    }

    @Override
    public List<AddressBook> list() {
      List<AddressBook> addressBooks=  addressBookMapper.queryById(1l);
        return addressBooks;
    }

    @Override
    @Transactional
    public void updateById(Long id) {
        AddressBook addressBook=AddressBook.builder()
                .isDefault(0)
                .build();
//        先将全部地址设置为非默认地址
        addressBookMapper.update(addressBook);
//        将指定地址设为默认地址
        addressBook.setIsDefault(1);
        addressBook.setId(id);
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getAddressById(Long id) {
       AddressBook addressBook= addressBookMapper.getById(id);
        return addressBook;
    }

    @Override
    public void updateAddress(AddressBook addressBook) {
        addressBook.setUserId(1l);
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getDefault() {
        AddressBook addressBook= addressBookMapper.getByUserIdAndDefault(1l,1);
        return addressBook;
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.deleteById(id);
    }
}
