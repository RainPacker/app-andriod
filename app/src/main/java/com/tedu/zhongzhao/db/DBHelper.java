package com.tedu.zhongzhao.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.bean.KeyValueInfo;
import com.tedu.zhongzhao.db.gen.DaoMaster;
import com.tedu.zhongzhao.db.gen.DaoSession;
import com.tedu.zhongzhao.db.gen.KeyValueInfoDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * DB helper
 * Created by huangyx on 2018/4/10.
 */
public class DBHelper {

    private static DaoSession session = newSession();

    private static DaoSession newSession() {
        DaoMaster.OpenHelper helper = new DaoMaster.OpenHelper(WorkApplication.getApplication(), "reader_db") {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                super.onUpgrade(db, oldVersion, newVersion);
            }
        };
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDb());
        return daoMaster.newSession();
    }

    /**
     * 保存数据
     *
     * @param keyValues List<KeyValueInfo>
     */
    public static void save(List<KeyValueInfo> keyValues) {
        if (keyValues != null && !keyValues.isEmpty()) {
            KeyValueInfoDao dao = session.getKeyValueInfoDao();
            dao.insertOrReplaceInTx(keyValues);
        }
    }

    /**
     * 保存数据
     *
     * @param keyValue KeyValue
     */
    public static long save(KeyValueInfo keyValue) {
        if (keyValue != null) {
            KeyValueInfoDao dao = session.getKeyValueInfoDao();
            return dao.insertOrReplace(keyValue);
        }
        return -1;
    }

    /**
     * 更新数据
     *
     * @param keyValue KeyValueInfo
     */
    public static void update(KeyValueInfo keyValue) {
        if (keyValue != null) {
            KeyValueInfoDao dao = session.getKeyValueInfoDao();
            dao.update(keyValue);
        }
    }

    /**
     * 所有数据
     *
     * @return List<KeyValueInfo>
     */
    public static List<KeyValueInfo> getKeyValues() {
        KeyValueInfoDao dao = session.getKeyValueInfoDao();
        QueryBuilder<KeyValueInfo> builder = dao.queryBuilder();
        return builder.list();
    }

    /**
     * 根据key获取数据
     *
     * @param key Key
     * @return KeyValue
     */
    public static KeyValueInfo getKeyValue(String key) {
        if (!TextUtils.isEmpty(key)) {
            KeyValueInfoDao dao = session.getKeyValueInfoDao();
            QueryBuilder<KeyValueInfo> builder = dao.queryBuilder();
            builder.where(KeyValueInfoDao.Properties.Key.eq(key));
            return builder.unique();
        }
        return null;
    }

    /**
     * 删除
     *
     * @param key
     */
    public static void delete(String key) {
        delete(getKeyValue(key));
    }

    /**
     * 删除数据
     *
     * @param keyValue
     */
    public static void delete(KeyValueInfo keyValue) {
        if (keyValue != null) {
            session.getKeyValueInfoDao().delete(keyValue);
        }
    }

    /**
     * 删除
     *
     * @param values
     */
    public static void delete(List<KeyValueInfo> values) {
        if (values != null && !values.isEmpty()) {
            session.getKeyValueInfoDao().deleteInTx(values);
        }
    }

    /**
     * 删除
     *
     * @param keys
     */
    public static void deleteByKeys(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            List<KeyValueInfo> values = new ArrayList<KeyValueInfo>();
            KeyValueInfo kv;
            for (String k : keys) {
                kv = getKeyValue(k);
                if (kv != null) {
                    values.add(kv);
                }
            }
            delete(values);
        }
    }
}
