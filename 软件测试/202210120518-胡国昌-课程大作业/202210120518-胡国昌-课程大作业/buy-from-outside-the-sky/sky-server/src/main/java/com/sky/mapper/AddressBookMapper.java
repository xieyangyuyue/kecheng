package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label) values " +
            "(#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label})")
    void save(AddressBook addressBook);

    @Select("select * from address_book where user_id=#{userId}")
    List<AddressBook> queryById(long userId);

    void update(AddressBook addressBook);

    @Select("select * from address_book where id=#{id}")
    AddressBook getById(Long id);

    @Select("select * from address_book where user_id=#{userId} and is_default=#{isDefault}")
    AddressBook getByUserIdAndDefault(Long userId, Integer isDefault);

    @Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);
}
