package com.stupidzhang.dafuweng.base;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author hzhuguoqun
 * @date 2019/12/3 11:42
 */
@Data
public class ExportFileDTO {
    private String name;
    private Workbook workbook;
}
