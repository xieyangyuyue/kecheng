package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    public void insertBylist(List<OrderDetail> orderDetailList);

    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> listByOrderId(Long orderId);
    @Select("SELECT od.name, SUM(od.number) number " +
            "FROM order_detail od " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.order_time >= #{beginTime} " +
            "AND o.order_time <= #{endTime} " +
            "GROUP BY od.name " +
            "ORDER BY SUM(od.number) DESC " +
            "LIMIT 10")
    List<GoodsSalesDTO> top10( LocalDateTime beginTime,  LocalDateTime endTime);
}
