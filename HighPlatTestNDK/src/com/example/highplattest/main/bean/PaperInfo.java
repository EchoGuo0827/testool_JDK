package com.example.highplattest.main.bean;

/**
 * ＊ 广州创自信息科技有限公司
 * Created by kavinwang on 2017/2/16.
 */
public class PaperInfo {
    private int leftMargin;
    private int paperHeight;
    private int paperHeightBorder;
    private int paperHeightBoderless;
    private int paperWidth;
    private int paperWidthBorder;
    private int paperWidthBorderless;
    private int topMargin;

    public PaperInfo(int paper_width, int paperHeight, int paper_width_boder, int paperHeightBorder, int leftMargin, int topMargin, int paper_width_boderless, int paper_height_boderless) {
        this.paperHeight = paperHeight;
        this.paperWidth = paper_width;
        this.paperHeightBorder = paperHeightBorder;
        this.paperHeightBoderless = paper_height_boderless;
        this.paperWidthBorder = paper_width_boder;
        this.paperWidthBorderless = paper_width_boderless;
        this.topMargin = topMargin;
        this.leftMargin = leftMargin;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getPaperHeight() {
        return paperHeight;
    }

    public void setPaperHeight(int paperHeight) {
        this.paperHeight = paperHeight;
    }

    public int getPaperHeightBorder() {
        return paperHeightBorder;
    }

    public void setPaperHeightBorder(int paperHeightBorder) {
        this.paperHeightBorder = paperHeightBorder;
    }

    public int getPaperHeightBoderless() {
        return paperHeightBoderless;
    }

    public void setPaperHeightBoderless(int paperHeightBoderless) {
        this.paperHeightBoderless = paperHeightBoderless;
    }

    public int getPaperWidth() {
        return paperWidth;
    }

    public void setPaperWidth(int paperWidth) {
        this.paperWidth = paperWidth;
    }

    public int getPaperWidthBorder() {
        return paperWidthBorder;
    }

    public void setPaperWidthBorder(int paperWidthBorder) {
        this.paperWidthBorder = paperWidthBorder;
    }

    public int getPaperWidthBorderless() {
        return paperWidthBorderless;
    }

    public void setPaperWidthBorderless(int paperWidthBorderless) {
        this.paperWidthBorderless = paperWidthBorderless;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }
}
