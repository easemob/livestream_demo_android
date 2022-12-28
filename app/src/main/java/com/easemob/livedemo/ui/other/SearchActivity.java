package com.easemob.livedemo.ui.other;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easemob.livedemo.ui.live.adapter.LiveListAdapter;
import com.hyphenate.easeui.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.easemob.livedemo.R;
import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.ui.base.BaseLiveActivity;
import com.easemob.livedemo.ui.base.GridMarginDecoration;
import com.easemob.livedemo.ui.cdn.CdnLiveAudienceActivity;
import com.easemob.livedemo.ui.widget.SearchEditText;
import com.easemob.livedemo.utils.Utils;

public class SearchActivity extends BaseLiveActivity implements OnItemClickListener {

    @BindView(R.id.et_search)
    SearchEditText editText;
    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.iv_back)
    ImageView backView;
    @BindView(R.id.search_delete_iv)
    ImageView searchDeleteView;

    private List<LiveRoom> allDataList;
    private List<LiveRoom> resultList;

    private LiveListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        allDataList = new ArrayList<>((List<LiveRoom>) intent.getSerializableExtra("liverooms"));
        resultList = new ArrayList<>(allDataList.size());
    }

    @Override
    public void onItemClick(View view, int position) {
        LiveRoom liveRoom = adapter.getItem(position);
        if (DemoHelper.isCdnLiveType(liveRoom.getVideo_type())) {
            CdnLiveAudienceActivity.actionStart(mContext, liveRoom);
        } else {
            // LiveAudienceActivity.actionStart(mContext, liveRoom);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false));
        adapter = new LiveListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridMarginDecoration(mContext, 10));
        adapter.setOnItemClickListener(this);

        editText.requestFocus();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(v.getText())) {
                        searchLiveRoom(v.getText().toString());
                    } else {
                        Toast.makeText(SearchActivity.this, R.string.search_tip, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchDeleteView.setVisibility(View.GONE);
                } else {
                    searchDeleteView.setVisibility(View.VISIBLE);
                }
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(v);
                finish();
            }
        });

        searchDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClear();
            }
        });

        searchDeleteView.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    private void searchClear() {
        editText.setText("");
        emptyView.setVisibility(View.GONE);
        resultList.clear();
        adapter.setData(resultList);
        recyclerView.setVisibility(View.GONE);

    }

    private void searchLiveRoom(final String searchText) {
        resultList.clear();
        Utils.hideKeyboard(editText);
        for (LiveRoom liveRoom : allDataList) {
            if (liveRoom.getName().toLowerCase().contains(searchText.toLowerCase())) {
                resultList.add(liveRoom);
            }
        }
        if (resultList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setText(String.format(this.getResources().getString(R.string.search_empty_result), searchText));
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(resultList);
        }
    }

}
