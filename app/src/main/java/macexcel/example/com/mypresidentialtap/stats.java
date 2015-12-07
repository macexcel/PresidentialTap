package macexcel.example.com.mypresidentialtap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.io.Console;
import java.util.ArrayList;

public class stats extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<PresidentialTap> mTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        try {
            String serviceUrl = getString(R.string.serviceUri);
            String key = getString(R.string.serviceKey);

            mClient = new MobileServiceClient(serviceUrl, key, this);
            mTable = mClient.getTable(PresidentialTap.class);

            // Get the presidential items
            new AsyncTask<Void, Void, Void>() {
                final ProgressDialog progress = new ProgressDialog(stats.this);

                @Override
                protected void onPreExecute() {
                    progress.setMessage("Fetching data from mobile service...");
                    progress.show();
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(Void result) {
                    if(progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        // Add presidentiables if not existing
                        AddPresidentsIfNotExists(mTable);

                        // Retrieve all records.
                        final MobileServiceList<PresidentialTap> result = mTable.execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (PresidentialTap item : result) {
                                    switch (item.name) {
                                        case "duterte":
                                            final TextView totalduterte = (TextView)findViewById(R.id.totalduterte);
                                            totalduterte.setText(Integer.toString(item.taps));
                                            break;
                                        case "poe":
                                            final TextView totalpoe = (TextView)findViewById(R.id.totalpoe);
                                            totalpoe.setText(Integer.toString(item.taps));
                                            break;
                                        case "roxas":
                                            final TextView totalroxas = (TextView)findViewById(R.id.totalroxas);
                                            totalroxas.setText(Integer.toString(item.taps));
                                            break;
                                        case "binay":
                                            final TextView totalbinay = (TextView)findViewById(R.id.totalbinay);
                                            totalbinay.setText(Integer.toString(item.taps));
                                            break;
                                        case "miriam":
                                            final TextView totalmiriam = (TextView)findViewById(R.id.totalmiriam);
                                            totalmiriam.setText(Integer.toString(item.taps));
                                            break;
                                    }
                                }
                            }
                        });
                    } catch (Exception exception) {
                        // error handler here.
                        Log.e("MobileService", exception.getMessage());
                    }
                    return null;
                }
            }.execute();
        }
        catch(Exception exception) {
            // display exception message here.
            Toast.makeText(this, exception.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void AddPresidentsIfNotExists(MobileServiceTable<PresidentialTap> mTable) {
        String[] presidents = {"duterte","poe","miriam","roxas","binay"};
        try {
            for(final String p : presidents) {
                final MobileServiceList<PresidentialTap> items = mTable.where().field("name").eq(p).execute().get();
                if(items.isEmpty()) {
                    PresidentialTap president = new PresidentialTap();
                    president.name = p;
                    president.taps = 0;

                    // insert to Azure database table.
                    mTable.insert(president, new TableOperationCallback<PresidentialTap>() {
                        @Override
                        public void onCompleted(PresidentialTap entity, Exception exception, ServiceFilterResponse response) {
                            if(exception == null) {
                                Log.e("MobileService", p + " has been added successfully.");
                            }
                        }
                    });
                }
            }
        }
        catch(Exception exception) {
            Log.e("MobileService", exception.getMessage());
        }
    }

}
