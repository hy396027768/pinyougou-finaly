package com.pinyougou.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class POIExcelImportUtil {


    /**
     * 导入表格,返回的文件表格的结果集
     * @param inputStream 文件流
     * @param t 泛行
     * @param dateString  日期格式
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> upload(InputStream inputStream, T t, String dateString) throws IOException {
        List<T> tList = new ArrayList<>();
        //根据指定的文件输入流导入Excel从而产生Workbook对象
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        //获取Excel文档中的第一个表单
        Sheet sheet = workbook.getSheetAt(0);
        //对Sheet中的每一行进行迭代

        Iterator<Row> iterator = sheet.iterator();

        while (iterator.hasNext()) {

            Row row = iterator.next();

            //如果当前行的行号（从0开始）未达到2（第三行）则从新循环
            if (row.getRowNum() < 1) {
                continue;
            }

            Class clazz = t.getClass();
            Field[] fieldArray = clazz.getDeclaredFields();
            try {
                //产生新的对象
                Object obj = clazz.getConstructor().newInstance();
                for (int i = 0; i < fieldArray.length; i++) {
                    Field f = fieldArray[i];
                    f = clazz.getDeclaredField(f.getName().toString());
                    f.setAccessible(true);//暴力反射，解除私有限定
                    if (f.getType().equals(String.class)) {
                        f.set(obj, getCellValue(row.getCell(i)));
                    } else if (f.getType().equals(Integer.class)) {
                        f.set(obj, Integer.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Long.class.getName())) {
                        f.set(obj, Long.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Float.class)) {
                        f.set(obj, Float.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Double.class)) {
                        f.set(obj, Double.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Byte.class)) {
                        f.set(obj, Byte.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Boolean.class)) {
                        f.set(obj, Boolean.valueOf(getCellValue(row.getCell(i))));
                    } else if (f.getType().equals(Date.class)) {
                        SimpleDateFormat sdf = new SimpleDateFormat(dateString);
                        Date date = null;
                        try {
                            date = sdf.parse(getCellValue(row.getCell(i)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        f.set(obj, date);
                    } else {
                        f.set(obj, getCellValue(row.getCell(i)));
                    }
                }
                tList.add((T) obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tList;
    }

    /**
     * 功能:获取单元格的值
     */
    private static String getCellValue(Cell cell) {
        Object result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    result = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date theDate = cell.getDateCellValue();
                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        result = dff.format(theDate);
                    } else {
                        DecimalFormat df = new DecimalFormat("0");
                        result = df.format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    result = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    result = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_ERROR:
                    result = cell.getErrorCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                default:
                    break;
            }
        }
        return result.toString();
    }


    /**
     * @param name     文件名
     * @param clazz    表的pojo字节码
     * @param list     结果集
     * @param response
     * @param <T>
     * @return
     */
    public <T> boolean setHeader(String name, Class clazz, List<T> list, HttpServletResponse response) {


        //创建表格
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 设置表头
        XSSFSheet sheet = workbook.createSheet(name);
        XSSFRow row = sheet.createRow(0);

        //反射获取class对象
        Field[] fields = clazz.getDeclaredFields();

        if (fields != null && fields.length > 0) {
            //遍历字段
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String filedName = f.getName();
                row.createCell(i).setCellValue(filedName);
            }

            //行号
            int rowNum = row.getRowNum();

            if (list != null && list.size() > 0) {
                //遍历结果集封装到单元表格
                for (T obj : list) {
                    XSSFRow tempRow = sheet.createRow(++rowNum);

                    for (int i = 0; i < fields.length; i++) {
                        fields[i].setAccessible(true);
                        try {
                            String valeu = null;


                            try {
                                valeu = fields[i].get(obj).toString();
                            } catch (NullPointerException e) {
                                    continue;
                            }

                            //System.out.println("==================================" + valeu + "==================================");

                            tempRow.createCell(i).setCellValue(valeu);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // 设置导出文件的格式 MIMETYPE
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            // 设置导出文件名
            response.setHeader("Content-Disposition", "attachment;filename=" + name + ".xlsx");
            try {

                ServletOutputStream outputStream = response.getOutputStream();
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return false;
    }


}


