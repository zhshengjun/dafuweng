package com.stupidzhang.dafuweng.controller;

import com.stupidzhang.dafuweng.base.ExportFileDTO;
import com.stupidzhang.dafuweng.base.Result;
import com.stupidzhang.dafuweng.service.ObtainDanJuanExponentialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Controller
@Slf4j
@RequestMapping("/index")
public class ObtainDanJuanExponentialController {

    @Autowired
    private ObtainDanJuanExponentialService obtainDanJuanExponentialService;


    @ResponseBody
    @RequestMapping(value = "/valuation/download", method = RequestMethod.GET)
    public void export(HttpServletResponse response) throws UnsupportedEncodingException {
        ExportFileDTO exportFileDTO = obtainDanJuanExponentialService.obtainDanJuanExponential();
        if (exportFileDTO != null) {
            Workbook workbook = exportFileDTO.getWorkbook();
            String title = exportFileDTO.getName() + ".xls";
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition",
                    new String(("attachment;filename=" + title).getBytes("GB2312"), StandardCharsets.ISO_8859_1));
            try {
                OutputStream out = response.getOutputStream();
                workbook.write(out);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("文件导出异常：{}", e);
            }
        }
        return;
    }
}
