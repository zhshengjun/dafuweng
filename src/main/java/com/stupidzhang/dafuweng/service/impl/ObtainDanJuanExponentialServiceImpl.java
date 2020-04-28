package com.stupidzhang.dafuweng.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stupidzhang.dafuweng.base.ExportFileDTO;
import com.stupidzhang.dafuweng.base.Valuation;
import com.stupidzhang.dafuweng.enums.TypeEnum;
import com.stupidzhang.dafuweng.service.ObtainDanJuanExponentialService;
import com.stupidzhang.dafuweng.util.DateUtils;
import com.stupidzhang.dafuweng.util.ExcelTools;
import com.stupidzhang.dafuweng.util.ExcelWaterRemarkUtils;
import com.stupidzhang.dafuweng.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ObtainDanJuanExponentialServiceImpl implements ObtainDanJuanExponentialService {
    @Override
    public ExportFileDTO obtainDanJuanExponential() {
        ExportFileDTO export = new ExportFileDTO();

        // 获取蛋卷数据
        List<Valuation> valuations = getValuations();

        if (CollectionUtils.isEmpty(valuations)) {
            export.setName("获取数据异常");
            return export;
        }
        String date = valuations.get(0).getDate();
        export.setName("蛋卷基金" + date + "日估值");

        Map<String, List<Valuation>> separate = separate(valuations);

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = creatSheetAndDefault(date, hssfWorkbook);

        //初始化颜色
        initColor(hssfWorkbook);
        HSSFFont font = hssfWorkbook.createFont();
        // 粗体显示
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);

        HSSFCellStyle whiteStyle = setTitle(date, hssfWorkbook, sheet, font);

        int line = 2;
        line = writeList(separate.get("low"), hssfWorkbook, sheet, line, IndexedColors.GREEN, font);
        line = writeList(separate.get("mid"), hssfWorkbook, sheet, line, IndexedColors.YELLOW, font);
        line = writeList(separate.get("high"), hssfWorkbook, sheet, line, IndexedColors.RED, font);

        // 写入十年国债
        line = writeNational(hssfWorkbook, sheet, font, line);

        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(valuations.size() + 2, valuations.size() + 2, 8, 9));
        creatWhiteRow(sheet, whiteStyle, line);

        // 水印
        //waterMark(hssfWorkbook, sheet);

        export.setWorkbook(hssfWorkbook);
        return export;
    }

    /**
     * 十年国债
     * @param hssfWorkbook
     * @param sheet
     * @param font
     * @param line
     * @return
     */
    private int writeNational(HSSFWorkbook hssfWorkbook, HSSFSheet sheet, HSSFFont font, int line) {
        String value = getNational();
        HSSFRow row = sheet.createRow(line++);
        for (int i = 1; i < 10; i++) {
            HSSFCell cell = row.createCell(i);
            HSSFCellStyle style = creatCellStyle(hssfWorkbook, IndexedColors.BLUE, font, null);
            cell.setCellStyle(style);
            if (i == 1) {
                ExcelTools.writeCell(cell, "十年期国债");
            } else if (i == 2) {
                HSSFCellStyle numStyle = creatCellStyle(hssfWorkbook, IndexedColors.BLUE, font, HSSFDataFormat.getBuiltinFormat("0.00%"));
                cell.setCellStyle(numStyle);
                ExcelTools.writeCell(cell, Double.parseDouble(value) / 100d);
            } else if (i == 8) {
                ExcelTools.writeCell(cell, "贫民窟的大富翁");
            }
        }
        return line;
    }

    private Integer creatWhiteRow(HSSFSheet sheet, HSSFCellStyle whiteStyle, int line) {
        HSSFRow row = sheet.createRow(line);
        for (int i = 0; i < 11; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(whiteStyle);
        }
        return line++;
    }

    private HSSFSheet creatSheetAndDefault(String date, HSSFWorkbook hssfWorkbook) {
        HSSFSheet sheet = hssfWorkbook.createSheet();
        hssfWorkbook.setSheetName(0, date + "日估值");
        sheet.setDefaultRowHeight(((short) (20 * 20)));
        sheet.setDefaultColumnWidth(12);
        return sheet;
    }


    /**
     * 设置表头
     *
     * @param date
     * @param hssfWorkbook
     * @param sheet
     * @param font
     * @return
     */
    private HSSFCellStyle setTitle(String date, HSSFWorkbook hssfWorkbook, HSSFSheet sheet, HSSFFont font) {

        HSSFCellStyle whiteStyle = creatCellStyle(hssfWorkbook, IndexedColors.WHITE, font, null);
        whiteStyle.setFont(font);
        creatWhiteRow(sheet, whiteStyle, 0);
        String year = DateUtils.getCurrentYear();
        HSSFRow row = sheet.createRow(1);
        String[] head = {DateUtils.formatYmdDate(DateUtils.parseDateYmd(year + "-" + date)), "指数类型", "PE", "PE百分位", "PB", "PB百分位", "股息率", "ROE", "指数代码"};
        HSSFCellStyle headStyle = creatCellStyle(hssfWorkbook, IndexedColors.WHITE1, font, null);
        for (int i = 1; i <= head.length; i++) {

            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            if (i == 1) {
                ExcelTools.writeCell(cell, Double.valueOf(head[i - 1]));
            }
            ExcelTools.writeCell(cell, head[i - 1]);
        }
        return whiteStyle;
    }

    /**
     * 初始化颜色
     *
     * @param hssfWorkbook
     */
    private void initColor(HSSFWorkbook hssfWorkbook) {
        HSSFPalette palette = hssfWorkbook.getCustomPalette();
        palette.setColorAtIndex(IndexedColors.GREEN.getIndex(), (byte) (0xff & 169), (byte) (0xff & 210), (byte) (0xff & 142));
        palette.setColorAtIndex(IndexedColors.YELLOW.getIndex(), (byte) (0xff & 255), (byte) (0xff & 230), (byte) (0xff & 153));
        palette.setColorAtIndex(IndexedColors.RED.getIndex(), (byte) (0xff & 236), (byte) (0xff & 107), (byte) (0xff & 23));
        palette.setColorAtIndex(IndexedColors.WHITE.getIndex(), (byte) (0xff & 255), (byte) (0xff & 255), (byte) (0xff & 255));
        palette.setColorAtIndex(IndexedColors.BLUE.getIndex(), (byte) (0xff & 5), (byte) (0xff & 180), (byte) (0xff & 138));
    }


    /**
     * 写入数据
     *
     * @param low
     * @param workbook
     * @param sheet
     * @param rowNum
     * @param color
     * @param font
     * @return
     */
    private Integer writeList(List<Valuation> low, HSSFWorkbook workbook, HSSFSheet sheet, Integer rowNum, IndexedColors color, HSSFFont font) {
        HSSFCellStyle whiteStyle = creatCellStyle(workbook, IndexedColors.WHITE1, font, null);
        HSSFCellStyle style = creatCellStyle(workbook, color, font, null);
        HSSFCellStyle numStyle = creatCellStyle(workbook, color, font, HSSFDataFormat.getBuiltinFormat("0.00"));
        HSSFCellStyle percentStyle = creatCellStyle(workbook, color, font, HSSFDataFormat.getBuiltinFormat("0.00%"));

        HSSFRow row;
        // 写入估值
        if (low.size() > 0) {
            int size = low.size();
            for (int i = 0; i < size; i++) {
                int y = 0;
                // 空白单元格
                row = sheet.createRow(rowNum++);
                HSSFCell cell = createCell(row, whiteStyle, y);

                Valuation t = low.get(i);
                cell = createCell(row, style, ++y);
                ExcelTools.writeCell(cell, t.getName());

                cell = createCell(row, style, ++y);
                ExcelTools.writeCell(cell, TypeEnum.getName(t.getTtype()));
                // PE
                cell = createCell(row, numStyle, ++y);
                ExcelTools.writeCell(cell, t.getPe());
                // PE 百分比
                cell = createCell(row, percentStyle, ++y);
                ExcelTools.writeCell(cell, t.getPe_percentile());

                // PB
                cell = createCell(row, numStyle, ++y);
                ExcelTools.writeCell(cell, t.getPb());
                // PB 百分比
                cell = createCell(row, percentStyle, ++y);
                ExcelTools.writeCell(cell, t.getPb_percentile());

                // 股息率 yeiled
                cell = createCell(row, percentStyle, ++y);
                ExcelTools.writeCell(cell, t.getYeild());

                // ROE
                cell = createCell(row, percentStyle, ++y);
                ExcelTools.writeCell(cell, t.getRoe());

                // 指数代码
                cell = createCell(row, style, ++y);
                ExcelTools.writeCell(cell, t.getIndex_code());
                // 空白单元格
                cell = createCell(row, whiteStyle, ++y);
            }
        }
        return rowNum;
    }


    /**
     * 区分数据
     *
     * @param valuations
     * @return
     */
    private Map<String, List<Valuation>> separate(List<Valuation> valuations) {
        Map<String, List<Valuation>> map = new HashMap<>();
        // 分类
        List<Valuation> low = valuations.stream().filter(va -> (va.getPb_percentile() < 0.25 && va.getPe_percentile() < 0.25 && va.getPe() < 12))
                .sorted(Comparator.comparing(Valuation::getRoe)).collect(Collectors.toList());
        List<Valuation> high = valuations.stream().filter(va -> (va.getPb_percentile() > 0.70 || va.getPe_percentile() > 0.70 || va.getPe() > 20))
                .sorted(Comparator.comparing(Valuation::getRoe)).collect(Collectors.toList());
        List<String> lowCode = low.stream().map(Valuation::getIndex_code).collect(Collectors.toList());
        List<String> highCode = high.stream().map(Valuation::getIndex_code).collect(Collectors.toList());
        List<Valuation> mid = valuations.stream().filter(va -> (!lowCode.contains(va.getIndex_code()) && !highCode.contains(va.getIndex_code())))
                .sorted(Comparator.comparing(Valuation::getRoe)).collect(Collectors.toList());
        map.put("low", low);
        map.put("mid", mid);
        map.put("high", high);

        return map;

    }


    /**
     * 创建单元格格式
     *
     * @param hssfWorkbook
     * @param lemonChiffon
     * @param font
     * @param dataFormat
     * @return
     */
    private HSSFCellStyle creatCellStyle(HSSFWorkbook hssfWorkbook, IndexedColors lemonChiffon, HSSFFont font, Short dataFormat) {
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        if (lemonChiffon != null) {
            style.setFillForegroundColor(lemonChiffon.index);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (font != null) {
            style.setFont(font);
        }
        // 水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        // 垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (dataFormat != null) {
            style.setDataFormat(dataFormat);
        }
        return style;
    }

    /**
     * 创建单元格
     *
     * @param row
     * @param style
     * @param line
     * @return
     */
    private HSSFCell createCell(HSSFRow row, HSSFCellStyle style, Integer line) {
        HSSFCell cell = row.createCell(line);
        cell.setCellStyle(style);
        return cell;
    }

    /**
     * 蛋卷基金数据
     *
     * @return
     */
    private List<Valuation> getValuations() {
        // 获取数据
        JSONObject result = JSON.parseObject(HttpUtil.doGet("https://danjuanapp.com/djapi/index_eva/dj"));
        String items = JSON.parseObject(result.getString("data")).getString("items");
        List<String> exclude = Arrays.asList("CSIH30269", "CSI931157", "HSFML25", "CSI931142", "SPCQVCP", "SH000827", "SZ399417", "SH000852", "SZ399967", "CSI930782", "CSI931079", "CSI930652");
        List<Valuation> valuations = JSON.parseArray(items, Valuation.class);
        return valuations.stream().filter(va -> !exclude.contains(va.getIndex_code())).collect(Collectors.toList());
    }

    /**
     * 十年国债
     *
     * @return
     */
    private String getNational() {
        String html = HttpUtil.doGet2("https://cn.investing.com/rates-bonds/china-10-year-bond-yield");
        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("span[class=arial_26 inlineblock pid-29227-last]");
        return rows.get(0).text();
    }


    private void waterMark(HSSFWorkbook hssfWorkbook, HSSFSheet sheet) {
        String path = System.getProperty("user.dir");
        path = path + "/logo.png";
        try {
            log.warn("开始创建水印图片");
            ExcelWaterRemarkUtils.createWaterMark("公众号·股海沉思", path);
            int rows = sheet.getFirstRowNum() + sheet.getLastRowNum();
            //获取excel实际所占列
            int cell = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum();
            //根据行与列计算实际所需多少水印
            log.warn("开始打印增加水印");
            ExcelWaterRemarkUtils.putWaterRemarkToExcel(hssfWorkbook, sheet, path, 1, 2, 3, 5, cell / 4, rows / 4, 0, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
