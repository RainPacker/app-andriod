package com.tedu.chat.ui;

import android.os.Bundle;

import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.tedu.chat.R;
import com.tedu.chat.ui.fragment.ContactListFragment;

/**
 * 好友列表
 * Created by huangyx on 2018/5/5.
 */
public class ContactListActivity extends EaseBaseActivity {

    private EaseContactListFragment fragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_contact);

        fragment = new ContactListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }
}
