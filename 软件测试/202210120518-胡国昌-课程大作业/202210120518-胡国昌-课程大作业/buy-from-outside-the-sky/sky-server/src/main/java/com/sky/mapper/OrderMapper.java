package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO orders(number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, " +
            "amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, " +
            "delivery_status, delivery_time, pack_amount, tableware_number, tableware_status) VALUES " +
            "(#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{payMethod}, #{payStatus}, " +
            "#{amount}, #{remark}, #{phone}, #{address}, #{userName}, #{consignee}, #{cancelReason}, #{rejectionReason}, " +
            "#{cancelTime}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{deliveryTime}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    void insert(Orders orders);

    Page<Orders> list(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id};")
    Orders getById(Long id);

    @Select("select sum(amount)  from orders where order_time >= #{beginTime} and order_time <= #{endTime}")
    Double turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(*) from orders where order_time >= #{beginTime} and order_time <= #{endTime}")
    Integer orderStatistics(LocalDateTime beginTime, LocalDateTime endTime);
}
