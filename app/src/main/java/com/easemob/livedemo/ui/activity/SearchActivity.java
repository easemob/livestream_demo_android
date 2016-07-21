package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.easemob.livedemo.R;
import com.easemob.livedemo.data.TestDataRepository;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.recView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.empty_view) TextView emptyView;
    SearchAdapter adapter;
    List<String> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchList = new ArrayList<>();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    if(!TextUtils.isEmpty(v.getText())){
                        searchUser(v.getText().toString());
                    }else{
                        Toast.makeText(SearchActivity.this,"请输入搜索关键字", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchUser(String searchText){
        //没有实际服务器数据，这里只搜索主播
        searchList.clear();
        String[] anchorIds = TestDataRepository.anchorIds;
        for(String anchor : anchorIds){
            if(anchor.contains(searchText.trim()) || anchor.equals(searchText.trim())){
                searchList.add(anchor);
            }
        }
        if(searchList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else{
            emptyView.setVisibility(View.INVISIBLE);
        }
        if(adapter == null){
            adapter = new SearchAdapter(this, searchList);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }

    }

    private class SearchAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private final Context context;
        private final List<String> userlist;

        public SearchAdapter(Context context, List<String> userlist){
            this.context = context;
            this.userlist = userlist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.search_list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String username = userlist.get(position);
            holder.usernameView.setText(username);
        }

        @Override
        public int getItemCount() {
            return userlist.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView avatarView;
        TextView usernameView;
        public MyViewHolder(View itemView) {
            super(itemView);
            avatarView = (ImageView) itemView.findViewById(R.id.avatar);
            usernameView = (TextView) itemView.findViewById(R.id.username);
        }

    }
}
