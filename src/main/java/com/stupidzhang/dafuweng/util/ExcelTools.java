package com.stupidzhang.dafuweng.util;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.format.*;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Number;

/**
 * 生成excel的工具
 * 一般的excel，只需要三步创建一个excel
 * 1、new ExcelTools(excel的名字，title，列头)：添加excel的基本信息
 * 2、addContent(添加的内容):添加内容
 * 3、crateExcel()：输出excel
 * @author machao
 *
 */
public class ExcelTools {
	String excelName;//保存时excel的名字
	String title;//每一个sheet的title
	String[] heads;//sheet的head
	HttpServletResponse response;  //response
	
	WritableWorkbook book;
	ByteArrayOutputStream outputStream;//输出excel的流
	int index = 0 ;//有sheet的index
	int lineIndex = 0;//每一个sheet有多少行 大于10000行就分sheet
	int maxLineNum = 30000;
	private static final Logger log = Logger.getLogger(ExcelTools.class);
	public ExcelTools(){}
	WritableSheet ws=null;

	/**
	 * 新建一个excel
	 * @param excelName excel保存的时候的文件名
	 * @param title 
	 * @param heads 列头
	 * @throws Exception
	 */
	public ExcelTools(String excelName, String title, String[] heads, HttpServletResponse response) throws Exception{
		this.excelName = excelName;
		this.title = title;
		this.heads = heads;
		this.response = response;
		outputStream =  new ByteArrayOutputStream();
		book = createExcel(outputStream);
	}
	public void addContent(List<List<Object>> contents) throws Exception{
		try{
			//init jxl format
			Map<String, WritableCellFormat> formatMap = _initFormat();
			for(List<Object> content :contents)
			{
				if (ws==null || lineIndex > maxLineNum)
				{
					ws=_addSheet(book);
					_addHead(ws);
				}
				_addRow(ws,content,lineIndex,formatMap);
				lineIndex++;
			}
		}catch(Exception ex){
			response.resetBuffer();
			log.error("导出"+title+"异常！", ex);
			throw new Exception("导出"+title+"异常！" , ex);
		}
		
	}
	private Map<String, WritableCellFormat> _initFormat(){
		try{
			WritableCellFormat normalWcf = new WritableCellFormat();
			normalWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableCellFormat strWcf = new WritableCellFormat();
			strWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			strWcf.setAlignment(Alignment.RIGHT);
			
			NumberFormat douNf = new NumberFormat("0.00");
			WritableCellFormat douWcf = new WritableCellFormat(douNf);
			douWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			douWcf.setAlignment(Alignment.RIGHT);
			
			NumberFormat floatNf = new NumberFormat("0.000000");
			WritableCellFormat floatWcf = new WritableCellFormat(floatNf);
			floatWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			floatWcf.setAlignment(Alignment.RIGHT);
			
			Map<String, WritableCellFormat> formatMap = new HashMap<String, WritableCellFormat>();
			formatMap.put("normal", normalWcf);
			formatMap.put("string", strWcf);
			formatMap.put("double", douWcf);
			formatMap.put("float", floatWcf);
			return formatMap;
		}catch(Exception e){
			log.info("导出excel出错：定义格式错误");
			throw new RuntimeException("导出excel出错：定义格式错误");
		}
	}
	private WritableSheet _addSheet(WritableWorkbook wwb) throws Exception {
		log.info(String.format("开始导出Sheet%s",index));
		WritableSheet ws =wwb.createSheet("Sheet"+index, index++);
		ws.getSettings().setShowGridLines(false);
		return ws;
	}
	/**
	 * 设置excel标题和头
	 * @param ws
	 * @throws WriteException
	 */
	private void _addHead(WritableSheet ws) throws Exception{
		this.lineIndex=0;
		// 设置excel标题
		String tmptitle = title;
        WritableFont wfont = new WritableFont(WritableFont.createFont("宋体"), 16, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat wcfFC = new WritableCellFormat(wfont);
        wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);
        wcfFC.setAlignment(Alignment.CENTRE);
		ws.mergeCells(0, 0, 11, 0);
		ws.addCell(new Label(0, 0, tmptitle, wcfFC));
		
		int c = 0;
		for(String head : heads){
			WritableCell acell =aCell(c++,2,head, Alignment.CENTRE);
			ws.addCell( acell );
		}
		lineIndex+=3;
		ws.setRowView(0, 400);
		ws.setRowView(1, 300);
	}
	
	private WritableCell aCell(int col, int row, String text, Alignment align) throws Exception{
		WritableFont wfont = new WritableFont(WritableFont.createFont("宋体"), 8, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat wcfFC = new WritableCellFormat(wfont);
        wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);
        wcfFC.setAlignment(align);
        wcfFC.setBorder(Border.ALL, BorderLineStyle.THIN);
		WritableCell wc =new Label(col, row, text,wcfFC);
		return wc;
	}
	
	/**
	 * 添加每一个行的内容
	 * @param ws
	 * @param order
	 * @param row
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void _addRow(WritableSheet ws, List<Object> contents, Integer row, Map<String, WritableCellFormat> formatMap) throws Exception{
		//内容
		Integer _index = row ;
		int c = 0 ;
		for(Object content :contents){
			WritableCell ac = aCell(c++,_index,content,formatMap );
			ws.addCell(ac);
		}
		ws.setRowView(_index, 300);
	}
	
	
	
	private WritableCell aCell(int col, int row, Object obj, Map<String, WritableCellFormat> formatMap)throws Exception{
		
		if(obj==null){
			return new Blank(col,row,formatMap.get("normal"));
		}
		if(obj instanceof Integer){
//			wcf.setAlignment(Alignment.RIGHT);
			return new jxl.write.Number(col,row,((Number)obj).doubleValue(),formatMap.get("normal"));
		}
		if(obj instanceof Double){
			return new jxl.write.Number(col,row,((Number)obj).doubleValue(),formatMap.get("double"));
		}
		if(obj instanceof Float)
		{
			BigDecimal b = new BigDecimal(obj.toString());
			return new jxl.write.Number(col,row,b.doubleValue(),formatMap.get("float"));
		}
		if(obj instanceof Number){
			return new jxl.write.Number(col,row,((Number)obj).doubleValue(),formatMap.get("float"));
		}
		if(obj instanceof Date){
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			obj = sdf.format((Date)obj);
		}
		if(obj.toString().trim().length()==0){
			return new Blank(col,row,formatMap.get("normal"));
		}
		return new Label(col, row, obj.toString(),formatMap.get("normal"));
	}

	
	/**
	 * 创建excel，返回excel名字
	 * @return
	 * @throws IOException
	 * @throws WriteException
	 */
	public String createExcel() throws IOException, WriteException {
		closeExcel(book);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = excelName + sdf.format(new Date(System.currentTimeMillis())) + ".xls";
//  		str = "abc.xls";
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Content-Disposition", new String(("attachment; filename=" + str).getBytes("GB2312"), "ISO-8859-1")); // 针对中文文件名
//		response.addHeader("Content-Disposition", "attachment; filename=" + str); // 针对中文文件名
		response.getOutputStream().write(outputStream.toByteArray());
		response.getOutputStream().flush();
		return str;
	}
	public WritableWorkbook createExcel(HttpServletResponse response) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/vnd.ms-excel");
		String str = excelName + sdf.format(new Date(System.currentTimeMillis())) + ".xls";
		response.addHeader("Content-Disposition", new String(("attachment; filename=" + str).getBytes("UTF-8"), "ISO-8859-1")); // 针对中文文件名
		return createExcel(response.getOutputStream());
	}
	public WritableWorkbook createExcel(OutputStream out) throws IOException {
		WritableWorkbook wwb = Workbook.createWorkbook(out);
		return wwb;
	}
	public void closeExcel(WritableWorkbook wwb) throws IOException, WriteException {
		wwb.write();
		wwb.close();
	}
    
    /**
	 * 写单元格判断类型
	 * @param cell
	 * @param o
	 */
	public static void writeCell(HSSFCell cell, Object o) {
		if (o == null) {
			cell.setCellValue("");
			return;
		}
		if (o instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			cell.setCellValue(sdf.format((Date) o));
			return;
		}
		if (o instanceof Integer) {
			cell.setCellValue((Integer) o);
			return;
		}
		if (o instanceof Double) {
			cell.setCellValue((Double) o);
			return;
		}
		cell.setCellValue((String) o);
	}

	/**
	 * 写单元格判断类型
	 * @param cell
	 * @param o
	 */
	public static void writeCell(Cell cell, Object o) {
		if (o == null) {
			cell.setCellValue("");
			return;
		}
		if (o instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			cell.setCellValue(sdf.format((Date) o));
			return;
		}
		if (o instanceof Integer) {
			cell.setCellValue((Integer) o);
			return;
		}
		if (o instanceof Double) {
			cell.setCellValue((Double) o);
			return;
		}
		cell.setCellValue((String) o);
	}
}
