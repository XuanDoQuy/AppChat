package com.example.chatfoy.model.object;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatfoy.model.object.User;

import java.util.ArrayList;

public class ShareViewModel extends ViewModel {
    private MutableLiveData<ArrayList<User>> dataListFriend;
    private MutableLiveData<ArrayList<User>> dataListRequest;
    private MutableLiveData<ArrayList<RoomChat>> dataListRoom;

    public MutableLiveData<ArrayList<User>> getDataListFriend() {
        if (dataListFriend==null) dataListFriend = new MutableLiveData<>();
        return dataListFriend;
    }

    public MutableLiveData<ArrayList<User>> getDataListRequest() {
        if (dataListRequest==null) dataListRequest = new MutableLiveData<>();
        return dataListRequest;
    }

    public MutableLiveData<ArrayList<RoomChat>> getDataListRoom() {
        if (dataListRoom==null) dataListRoom = new MutableLiveData<>();
        return dataListRoom;
    }
}
