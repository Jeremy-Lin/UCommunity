
package com.inbuy.ucommunity.data;

public class PanelListItem {
    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;
    public int mType;
    public String mName;
    public boolean mSelected;

    public PanelListItem(int type, String name, boolean selected) {
        this.mName = name;
        this.mType = type;
        this.mSelected = selected;
    }
}
