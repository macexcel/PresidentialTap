package macexcel.example.com.mypresidentialtap;

import android.os.AsyncTask;
import android.util.Log;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class Shared {

    public void UpdateTaps(final MobileServiceTable<PresidentialTap> mTable, final String presidentName, final int taps)
    {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final MobileServiceList<PresidentialTap> result =
                                mTable.where().field("name").eq(presidentName).execute().get();

                        if(!result.isEmpty()) {
                            PresidentialTap item = result.get(0);
                            item.taps = item.taps + taps;
                            mTable.update(item).get();
                        }
                    } catch (Exception exception) {
                        // error handler here.
                        Log.e("MobileService", exception.getMessage());
                    }
                    return null;
                }
            }.execute();
        }
        catch(Exception exception) {
            Log.e("MobileService", exception.getMessage());
        }
    }
}
