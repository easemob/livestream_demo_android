package com.easemob.livedemo.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.easemob.livedemo.R;
import com.easemob.livedemo.ThreadPoolManager;
import com.easemob.livedemo.data.restapi.LiveException;
import com.easemob.livedemo.data.model.LiveRoom;
import com.easemob.livedemo.data.restapi.ApiManager;
import com.easemob.livedemo.ui.GridMarginDecoration;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveListFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycleview);
        //        GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridMarginDecoration(3));

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        showLiveList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                showLiveList();
            }
        });

    }


    private void showLiveList(){
        swipeRefreshLayout.setRefreshing(true);
        ThreadPoolManager.getInstance().executeTask(new ThreadPoolManager.Task<List<LiveRoom>>() {
            @Override public List<LiveRoom> onRequest() throws LiveException {
                return ApiManager.get().getLiveRoomList(1, 10);
            }

            @Override public void onSuccess(List<LiveRoom> liveRooms) {
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(new PhotoAdapter(getActivity(), liveRooms));
            }

            @Override public void onError(LiveException exception) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    static class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

        private final List<LiveRoom> liveRoomList;
        private final Context context;

        public PhotoAdapter(Context context, List<LiveRoom> liveRoomList){
            this.liveRoomList = liveRoomList;
            this.context = context;
        }
        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final PhotoViewHolder holder = new PhotoViewHolder(LayoutInflater.from(context).
                    inflate(R.layout.layout_livelist_item, parent, false));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    context.startActivity(new Intent(context, LiveAudienceActivity.class)
                            .putExtra("liveroom", liveRoomList.get(position)));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            LiveRoom liveRoom = liveRoomList.get(position);
            holder.anchor.setText(liveRoom.getName());
            holder.audienceNum.setText(liveRoom.getAudienceNum() + "人");
            Glide.with(context)
                    .load(liveRoomList.get(position).getCover())
                    .placeholder(R.color.placeholder)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return liveRoomList.size();
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo)
        ImageView imageView;
        @BindView(R.id.author)
        TextView anchor;
        @BindView(R.id.audience_num) TextView audienceNum;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
