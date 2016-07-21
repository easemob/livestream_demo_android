package com.easemob.livedemo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.livedemo.R;
import com.easemob.livedemo.data.TestDataRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.recView)
    RecyclerView recyclerView;
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
                if(actionId == KeyEvent.KEYCODE_ENTER){
                    if(!TextUtils.isEmpty(v.getText())){
                        searchUser(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void searchUser(String searchText){
        //TODO search from server
        searchList.clear();
//        String[] TestDataRepository.anchorIds;
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
