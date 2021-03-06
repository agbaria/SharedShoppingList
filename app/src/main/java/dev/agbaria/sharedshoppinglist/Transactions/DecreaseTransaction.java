package dev.agbaria.sharedshoppinglist.Transactions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by 3la2 on 30/01/2017.
 */

public class DecreaseTransaction implements Transaction.Handler {

    private final int i;

    public DecreaseTransaction(int i) {
        this.i = i;
    }

    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        Integer value = mutableData.getValue(Integer.class);
        if (value == null) {
            return Transaction.success(mutableData);
        }
        mutableData.setValue(value - i);
        return Transaction.success(mutableData);
    }

    @Override
    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

    }
}
