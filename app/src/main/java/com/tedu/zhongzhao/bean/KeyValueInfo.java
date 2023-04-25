package com.tedu.zhongzhao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by huangyx on 2018/4/10.
 */
@Entity
public class KeyValueInfo {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String key;
    private String value;

    @Generated(hash = 2080011091)
    public KeyValueInfo(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Generated(hash = 927346261)
    public KeyValueInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "key=" + key + ", " + "value=" + value;
    }
}
