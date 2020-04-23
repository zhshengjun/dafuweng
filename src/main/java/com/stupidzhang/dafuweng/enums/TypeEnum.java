package com.stupidzhang.dafuweng.enums;

public enum TypeEnum {

    BROAD_BASE("宽基指数", 1),
    STRATEGY("策略指数", 2),
    INDUSTRY("行业指数", 3);


    private String name;
    private Integer group;

    TypeEnum(String name, Integer group) {
        this.name = name;
        this.group = group;
    }

    public static String getName(Integer group) {
        if (group == null) {
            return null;
        }
        for (TypeEnum step : TypeEnum.values()) {
            if (step.getGroup().equals(group)) {
                return step.getName();
            }
        }
        return null;
    }


    public String getName() {
        return this.name;
    }

    public Integer getGroup() {
        return this.group;
    }
}
