package com.easemob.livedemo.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.LiveManager;
import com.hyphenate.exceptions.HyphenateException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.edit_text) EditText editText;
    @BindView(R.id.recView) RecyclerView recyclerView;
    @BindView(R.id.empty_view) TextView emptyView;
    @BindView(R.id.btn_cancel) TextView cancelView;
    List<LiveRoom> searchedList;

    LiveListFragment.PhotoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        searchedList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!TextUtils.isEmpty(v.getText())){
                        searchLiveRoom(v.getText().toString());
                    }else{
                        Toast.makeText(SearchActivity.this,"请输入房间号", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });


    }

    private void searchLiveRoom(final String searchText){
        executeTask(new ThreadPoolManager.Task<LiveRoom>() {
            @Override public LiveRoom onRequest() throws HyphenateException {
                return LiveManager.getInstance().getLiveRoomDetails(searchText);
            }

            @Override public void onSuccess(LiveRoom liveRoom) {
                emptyView.setVisibility(View.INVISIBLE);
                searchedList.clear();
                searchedList.add(liveRoom);
                if(adapter == null) {
                    adapter = new LiveListFragment.PhotoAdapter(SearchActivity.this, searchedList);
                    recyclerView.setAdapter(adapter);
                }else{
                    adapter.notifyDataSetChanged();
                }
            }

            @Override public void onError(HyphenateException exception) {
                emptyView.setVisibility(View.VISIBLE);
            }
        });

    }

}
