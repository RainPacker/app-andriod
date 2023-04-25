package com.tedu.zhongzhao.ui;

import java.util.List;

/**
 * 导航按钮定义
 * Created by huangyx on 2018/3/12.
 */
public class NavBtnInfo implements java.io.Serializable {
    /**
     * 类型 （1 按钮。2 菜单。）
     */
    private String type;
    /**
     * 标题
     */
    private String title;
    /**
     * 图标
     */
    private IconStyleInfo style;
    /**
     * 对应下一级页面信息
     */
    private List<PageInfo> items;

    private List<PageNotifi> notifies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IconStyleInfo getStyle() {
        return style;
    }

    public void setStyle(IconStyleInfo style) {
        this.style = style;
    }

    public List<PageInfo> getItems() {
        return items;
    }

    public void setItems(List<PageInfo> items) {
        this.items = items;
    }

    public List<PageNotifi> getNotifies() {
        return notifies;
    }

    public void setNotifies(List<PageNotifi> notifies) {
        this.notifies = notifies;
    }

    /**
     * 获取第1个节点
     *
     * @return PageInfo
     */
    public PageInfo getFirst() {
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }
}
