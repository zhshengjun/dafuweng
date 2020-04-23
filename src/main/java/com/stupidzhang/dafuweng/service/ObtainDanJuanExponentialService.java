package com.stupidzhang.dafuweng.service;

import com.stupidzhang.dafuweng.base.ExportFileDTO;
import org.springframework.stereotype.Service;

/**
 * @author stupidzhang
 */
public interface ObtainDanJuanExponentialService {


    /**
     * 导出蛋卷基金估值表
     *
     * @return
     */
    ExportFileDTO obtainDanJuanExponential();
}
