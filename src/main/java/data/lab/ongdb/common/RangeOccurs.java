package data.lab.ongdb.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.*
 * @Description: TODO(range查询参数)
 * @date 2019/5/31 14:18
 */
public enum RangeOccurs {

    /**
     * 搜索大于某值的字段，不包含该值本身
     **/
    GT("gt", ">"),

    /**
     * 搜索大于某值的字段，包含该值本身
     **/
    GTE("gte", ">="),

    /**
     * 搜索小于某值的字段，不包含该值本身
     **/
    LT("lt", "<"),

    /**
     * 搜索小于某值的字段，包含该值本身
     **/
    LTE("lte", "<=");

    private String symbol;
    private String condition;

    RangeOccurs(String symbol, String condition) {
        this.symbol = symbol;
        this.condition = condition;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCondition() {
        return condition;
    }
}
