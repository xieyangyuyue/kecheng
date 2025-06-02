package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

//todo 订单接口未完成，暂时不限制status状态，可通过AOP切面进行简化代码
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderdetailMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * @param begin
     * @param end
     * @return TurnoverReportVO包含营业额的字符串数据和日期的字符串数据
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
//        构建日期字符串
        List<LocalDate> dateTimes = new ArrayList<>();
        while (begin.isBefore(end.plusDays(1))) { // 包括结束日期
            dateTimes.add(begin);
            begin = begin.plusDays(1);
        }
        String date = StringUtils.join(dateTimes, ",");
//构建营业额字符串
        List<Double> turnovers = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            LocalDateTime beginTime = LocalDateTime.of(dateTime, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(dateTime, LocalTime.MAX);
//            计算营业额,由于订单接口未完成所以不限制status状态
            Double turnover = orderMapper.turnoverStatistics(beginTime, endTime);
            turnover = turnover == null ? 0 : turnover;
            turnovers.add(turnover);
        }
        String turnoverList = StringUtils.join(turnovers, ",");
        return TurnoverReportVO
                .builder()
                .dateList(date)
                .turnoverList(turnoverList).build();
    }

    /**
     * @param begin
     * @param end
     * @return UserReportVO包含当前所有用户统计的字符串数据，新增用户字符串数据和日期的字符串数据
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //        构建日期字符串
        List<LocalDate> dateTimes = new ArrayList<>();
        while (begin.isBefore(end.plusDays(1))) { // 包括结束日期
            dateTimes.add(begin);
            begin = begin.plusDays(1);
        }
        String date = StringUtils.join(dateTimes, ",");
//        构建新增用户字符串和当前所有用户统计字符串
        List<Integer> allUsers = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            LocalDateTime beginTime = LocalDateTime.of(dateTime, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(dateTime, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer allUser = userMapper.userStatistics(map);
            allUsers.add(allUser == null ? 0 : allUser);
            map.put("begin", beginTime);
            Integer newUser = userMapper.userStatistics(map);
            newUsers.add(newUser == null ? 0 : newUser);
        }
        String allUserList = StringUtils.join(allUsers, ",");
        String newUserList = StringUtils.join(newUsers, ",");
        return UserReportVO
                .builder()
                .dateList(date)
                .totalUserList(allUserList)
                .newUserList(newUserList).build();
    }

    /**
     * @param begin
     * @param end
     * @return OrderReportVO
     * 返回六项数据，
     * 包括订单总数，
     * 有效订单数，
     * 订单完成率，
     * 每日订单数组成的字符串数据，
     * 每日有效订单数组成的字符串，
     * 时间组成的字符串数据
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //        构建日期字符串
        List<LocalDate> dateTimes = new ArrayList<>();
        while (begin.isBefore(end.plusDays(1))) { // 包括结束日期
            dateTimes.add(begin);
            begin = begin.plusDays(1);
        }
        String date = StringUtils.join(dateTimes, ",");
//        构建每日订单数组成的字符串数据，原本需要判断订单状态，由于订单接口未完成所以不限制status状态
        List<Integer> orderList = new ArrayList<>();
        for (LocalDate dateTime : dateTimes) {
            LocalDateTime beginTime = LocalDateTime.of(dateTime, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(dateTime, LocalTime.MAX);
//            获取订单数，todo 订单接口未完成，暂时不限制status状态
            Integer order = orderMapper.orderStatistics(beginTime, endTime);
            orderList.add(order == null ? 0 : order);
        }
        Integer totalOrderCount = orderList.stream().reduce(Integer::sum).get();
        String orderListStr = StringUtils.join(orderList, ",");

        return OrderReportVO
                .builder()
                .dateList(date)
                .orderCountList(orderListStr)
                .validOrderCountList(orderListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalOrderCount)
                .orderCompletionRate((double) (totalOrderCount / totalOrderCount))
                .build();
    }

    /**
     * @param begin
     * @param end
     * @return SalesTop10ReportVO包含两个字符串，一个是有名字组成，一个是有销量组成
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
//        查询表中数据，正常情况是查询订单表和订单明细表，限制状态为已完成，在通过order by排序通过limit 10获取前10名，但现在状态未完成，所以直接查询订单表
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderdetailMapper.top10(beginTime, endTime);
        List<String> goodsNameList = new ArrayList<>();
        List<Integer> salesList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList) {
            goodsNameList.add(goodsSalesDTO.getName());
            salesList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(goodsNameList, ","))
                .numberList(StringUtils.join(salesList, ","))
                .build();
    }

    /**
     * 导出报表
     *
     * @param response 通过响应流让客服端下载excel文件
     */
    @Override
    public void export(HttpServletResponse response) {
//        指定时间30天内的订单数据
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
//        获取30天的数据
        BusinessDataVO businessData = workSpaceService.getBusinessData(beginTime, endTime);
//        获取excel模板
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //        写入30天的数据到excel模板中
        try {
//            获取excel模板实例
            XSSFWorkbook workbook = new XSSFWorkbook(resourceAsStream);
//            获取页签
            XSSFSheet sheet = workbook.getSheet("Sheet1");
//            获取行
            XSSFRow row = sheet.getRow(1);
//            获取单元格并设置值
            row.getCell(1).setCellValue("时间" + beginTime.toString() + "-" + endTime.toString());
//            获取第四行
            row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //            获取第五行    
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
//            填写每日订单数据
            for (int i = 0; i < 30; i++) {
                LocalDate localDate = beginTime.toLocalDate();
                localDate = localDate.plusDays(i);
                businessData=workSpaceService.getBusinessData(LocalDateTime.of(localDate,LocalTime.MIN), LocalDateTime.of(localDate,LocalTime.MAX));
                row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(localDate.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
//             写入到响应流中
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            resourceAsStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
