package com.easemob.livedemo.ui.live.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import com.easemob.livedemo.common.utils.DemoHelper;
import com.easemob.livedemo.common.utils.DemoMsgHelper;
import com.easemob.livedemo.common.db.dao.ReceiveGiftDao;
import com.easemob.livedemo.common.db.entity.ReceiveGiftEntity;

public class GiftStatisticsViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<ReceiveGiftEntity>> giftObservable;
    private final MediatorLiveData<Integer> senderNumObservable;
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
