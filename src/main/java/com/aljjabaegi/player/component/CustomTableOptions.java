package com.aljjabaegi.player.component;
/**
 * Custom Table Options 설정용
 *
 * @author GEON LEE
 * @since 2024-01-29
 * */
public enum CustomTableOptions {
    COLUMNS("columns"),/*String[]*/
    COLUMN_SIZE("columnSize"),/*Integer[]*/
    EDITABLE("editable"),/*boolean*/
    SORTABLE("sortable"),/*boolean*/
    FOCUSABLE("focusable"),/*boolean*/
    REORDERING("reordering"),/*boolean*/
    HEADER_CHECKBOX("headerCheckbox");/*boolean*/

    CustomTableOptions(String columns) {
    }
}
