package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;
import android.app.ListActivity;

import com.easemob.livedemo.common.DemoHelper;
import com.easemob.livedemo.common.DemoMsgHelper;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

public class GiftStatisticsViewModel extends AndroidViewModel {
    private MediatorLiveData<List<ReceiveGiftEntity>> giftObservable;
    private MediatorLiveData<Integer> senderNumObservable;
    private final ReceiveGiftDao giftDao;

    public GiftStatisticsViewModel(@NonNull Application application) {
        super(application);
        giftDao = DemoHelper.getReceiveGiftDao();
        giftObservable = new MediatorLiveData<>();
        senderNumObservable = new MediatorLiveData<>();
    }

    public MediatorLiveData<List<ReceiveGiftEntity>> getGiftObservable() {
        return giftObservable;
    }

    public void getGiftListFromDb() {
        giftObservable.addSource(giftDao.loadAll(DemoMsgHelper.getInstance().getCurrentRoomId()), response -> giftObservable.postValue(response));
    }

    public MediatorLiveData<Integer> getSenderNumObservable() {
        return senderNumObservable;
    }

    public void getGiftSenderNumFromDb() {
        senderNumObservable.addSource(giftDao.loadSenders(DemoMsgHelper.getInstance().getCurrentRoomId()), response -> senderNumObservable.postValue(response));
    }
}
