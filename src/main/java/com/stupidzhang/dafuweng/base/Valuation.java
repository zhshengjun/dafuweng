package com.stupidzhang.dafuweng.base;


import lombok.Data;

import java.util.Date;

@Data
public class Valuation {
    /**
     * 主键
     */
    private Long id;
    /**
     * 编码
     */
    private String index_code;
    /**
     * 名称
     */
    private String name;

    /**
     * 指数类型
     */
    private Integer ttype;
    /**
     * PE
     */
    private Double pe;
    /**
     * PE 百分位
     */
    private Double pe_percentile;

    /**
     * PB
     */
    private Double pb;
    /**
     * PB百分位
     */
    private Double pb_percentile;
    /**
     * ROE
     */
    private Double roe;

    /**
     *  股息率
     */
    private Double yeild;
    private Date ts;
    /**
     * 评价等级
     */
    private String eva_type;
    /**
     * 等级顺序
     */
    private Integer eva_type_int;
    private String url;
    private Double bond_yeild;
    private Date begin_at;
    private Date created_at;
    private Date updated_at;
    /**
     * PEG
     */
    private Double peg;
    private Boolean pb_flag;
    private Double pb_over_history;
    private Double pe_over_history;
    /**
     * 日期
     */
    private String date;
}
